package io.microsphere.dynamic.jdbc.spring.boot.context;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.dynamic.jdbc.spring.boot.context.error.DynamicJdbcInitializeException;
import io.microsphere.dynamic.jdbc.spring.boot.context.error.InitializeErrors;
import io.microsphere.dynamic.jdbc.spring.boot.env.SyncExecutionShutdownHookApplicationListener;
import io.microsphere.dynamic.jdbc.spring.boot.shardingsphere.context.ShardingSphereShutdownHookThreadFilter;
import io.microsphere.spring.boot.context.OnceMainApplicationPreparedEventListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.getDynamicJdbcConfigs;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.getAllModulesAutoConfigurationClassNames;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.getMultipleContextExclusionAutoConfigurationClassNames;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.isDynamicJdbcEnabled;
import static io.microsphere.spring.boot.autoconfigure.ConfigurableAutoConfigurationImportFilter.addExcludedAutoConfigurationClasses;
import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * {@link ApplicationPreparedEvent} {@link ApplicationListener} to prepare for Dynamic JDBC
 * {@link ApplicationContext context} environment
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class DynamicJdbcContextApplicationListener extends OnceMainApplicationPreparedEventListener {

    /**
     * Default order
     */
    public static final int DEFAULT_ORDER = 200;

    public DynamicJdbcContextApplicationListener() {
        super();
        setOrder(DEFAULT_ORDER);
    }

    @Override
    protected void onApplicationEvent(SpringApplication springApplication, String[] args, ConfigurableApplicationContext context) {

        if (isDisable(context)) {
            logger.debug("Current ApplicationContext[id : {}] disable Dynamic JDBC", context.getId());
            return;
        }

        processDynamicJdbcContext(context);
    }

    private boolean isDisable(ConfigurableApplicationContext context) {
        return !isDynamicJdbcEnabled(context.getEnvironment());
    }

    private void processDynamicJdbcContext(ConfigurableApplicationContext context) {
        ConfigurableEnvironment environment = context.getEnvironment();

        Map<String, DynamicJdbcConfig> dynamicJdbcConfigs = getDynamicJdbcConfigs(environment);

        int dynamicJdbcConfigSize = dynamicJdbcConfigs.size();

        if (dynamicJdbcConfigSize == 0) {
            logger.info("No DynamicJdbcConfig was configured, The process of Dynamic JDBC Context will be ignored");
            return;
        }

        registerPropagatingDynamicJdbcConfigChangedEventListener(dynamicJdbcConfigs, context);

        registerSyncExecutionShutdownHookApplicationListener(dynamicJdbcConfigs, context);

        boolean multiple = dynamicJdbcConfigSize > 1;

        if (multiple) {
            logger.info("{} DynamicJdbcConfigs were found, Current ApplicationContext[id : {}] will create DynamicJdbcChildContexts individually!",
                    dynamicJdbcConfigSize, context.getId());
            processDynamicJdbcChildContexts(dynamicJdbcConfigs.entrySet(), context);

        } else { // Single
            logger.info("Only one DynamicJdbcConfig was configured, Dynamic JDBC context will use current context[id : {}]!", context.getId());
            Map.Entry<String, DynamicJdbcConfig> dynamicJdbcConfigEntry = dynamicJdbcConfigs.entrySet().iterator().next();
            processDynamicJdbcContext(dynamicJdbcConfigEntry, context);
        }


    }

    private void registerPropagatingDynamicJdbcConfigChangedEventListener(Map<String, DynamicJdbcConfig> dynamicJdbcConfigs,
                                                                          ConfigurableApplicationContext context) {
        context.addApplicationListener(new PropagatingDynamicJdbcConfigChangedEventListener(dynamicJdbcConfigs.keySet(), context));
    }

    private void registerSyncExecutionShutdownHookApplicationListener(Map<String, DynamicJdbcConfig> dynamicJdbcConfigs,
                                                                      ConfigurableApplicationContext context) {
        if (hasShardingSphere(dynamicJdbcConfigs)) {
            context.addApplicationListener(new SyncExecutionShutdownHookApplicationListener(context, new ShardingSphereShutdownHookThreadFilter()));
        }
    }

    private boolean hasShardingSphere(Map<String, DynamicJdbcConfig> dynamicJdbcConfigs) {
        boolean hasShardingSphere = false;
        for (DynamicJdbcConfig dynamicJdbcConfig : dynamicJdbcConfigs.values()) {
            if (hasShardingSphere = (dynamicJdbcConfig.getShardingSphere() != null)) {
                break;
            }
        }
        return hasShardingSphere;
    }

    private void processDynamicJdbcChildContexts(Set<Map.Entry<String, DynamicJdbcConfig>> dynamicJdbcConfigEntrySet,
                                                 ConfigurableApplicationContext context) {
        int parallelism = dynamicJdbcConfigEntrySet.size();

        ThreadPoolExecutor executorService = (ThreadPoolExecutor) newFixedThreadPool(parallelism);
        InitializeErrors initializeErrors = new InitializeErrors();


        for (Map.Entry<String, DynamicJdbcConfig> dynamicJdbcConfigEntry : dynamicJdbcConfigEntrySet) {
            executorService.execute(() -> {
                try {
                    initializeDynamicJdbcChildContext(dynamicJdbcConfigEntry, context);
                } catch (Throwable t) {
                    initializeErrors.addError(dynamicJdbcConfigEntry.getKey(), t);
                    logger.error("Initialize Dynamic-JDBC failed. DynamicJdbcConfig:[{}]",
                            dynamicJdbcConfigEntry.getKey(), t);
                }
            });
        }

        boolean terminated = false;

        long completedTaskCount = 0;

        while (!terminated) {
            try {
                terminated = executorService.awaitTermination(1, TimeUnit.SECONDS);
                completedTaskCount = executorService.getCompletedTaskCount();
                if (completedTaskCount == parallelism) {
                    break;
                }
            } catch (InterruptedException e) {
                terminated = true;
            }
        }

        if (initializeErrors.hasError()) {
            throw new DynamicJdbcInitializeException(initializeErrors.toString());
        }

        appendExclusionAutoConfigurationProperty(context);
        // help gc
        executorService.shutdownNow();
        executorService = null;
    }

    private void initializeDynamicJdbcChildContext(Map.Entry<String, DynamicJdbcConfig> dynamicJdbcConfigEntry,
                                                   ConfigurableApplicationContext parentContext) {
        DynamicJdbcConfig dynamicJdbcConfig = dynamicJdbcConfigEntry.getValue();
        String dynamicJdbcConfigPropertyName = dynamicJdbcConfigEntry.getKey();

        DynamicJdbcChildContext dynamicJdbcChildContext =
                new DynamicJdbcChildContext(dynamicJdbcConfig, dynamicJdbcConfigPropertyName, parentContext);

        // Register Parent Beans
        dynamicJdbcChildContext.registerParentBeans();
        // Merge Parent Environment
        dynamicJdbcChildContext.mergeParentEnvironment();
        // Refresh Context
        dynamicJdbcChildContext.refresh();
    }

    private void appendExclusionAutoConfigurationProperty(ConfigurableApplicationContext context) {

        ConfigurableEnvironment environment = context.getEnvironment();

        Set<String> exclusionAutoConfigurationClassNames = getMultipleContextExclusionAutoConfigurationClassNames(environment);

        if (exclusionAutoConfigurationClassNames.isEmpty()) {
            exclusionAutoConfigurationClassNames = getAllModulesAutoConfigurationClassNames(context);
        }

        addExcludedAutoConfigurationClasses(environment, exclusionAutoConfigurationClassNames);
    }

    protected void processDynamicJdbcContext(Map.Entry<String, DynamicJdbcConfig> dynamicJdbcConfigEntry, ConfigurableApplicationContext context) {
        DynamicJdbcContextProcessor processor = new DynamicJdbcContextProcessor();
        DynamicJdbcConfig dynamicJdbcConfig = dynamicJdbcConfigEntry.getValue();
        String dynamicJdbcConfigPropertyName = dynamicJdbcConfigEntry.getKey();
        processor.process(dynamicJdbcConfig, dynamicJdbcConfigPropertyName, context);
    }
}
