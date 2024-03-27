package io.microsphere.dynamic.jdbc.spring.boot.datasource;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.dynamic.jdbc.spring.boot.context.DynamicJdbcChildContext;
import io.microsphere.dynamic.jdbc.spring.boot.context.DynamicJdbcChildContextIdGenerator;
import io.microsphere.dynamic.jdbc.spring.boot.context.DynamicJdbcConfigChangedEvent;
import io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.getDynamicDataSourceChildContextCloseDelay;
import static io.microsphere.text.FormatUtils.format;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

/**
 * General Dynamic {@link DataSource}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class DynamicDataSource implements DataSource, InitializingBean, DisposableBean, BeanFactoryAware {

    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSource.class);

    private static final DynamicJdbcChildContextIdGenerator idGenerator = new DynamicJdbcChildContextIdGenerator() {

        public String generate(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName,
                               ConfigurableApplicationContext parentContext) {
            return DynamicJdbcConfigUtils.generateDynamicDataSourceDynamicJdbcChildContextId(dynamicJdbcConfig);
        }
    };

    private final Object mutex = new Object();

    private final DynamicJdbcConfig dynamicJdbcConfig;

    private final String dynamicJdbcConfigPropertyName;

    private final ConfigurableApplicationContext context;

    private final ScheduledExecutorService closeScheduler;

    private final Duration dynamicDataSourceChildContextCloseDelay;

    private volatile boolean initialized;

    private volatile DataSource delegate;

    private volatile DynamicJdbcChildContext dynamicDataSourceChildContext;

    private BeanFactory beanFactory;

    public DynamicDataSource(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName,
                             ConfigurableApplicationContext context) {
        this.dynamicJdbcConfig = dynamicJdbcConfig;
        this.dynamicJdbcConfigPropertyName = dynamicJdbcConfigPropertyName;
        this.context = context;
        this.closeScheduler = newSingleThreadScheduledExecutor();
        this.dynamicDataSourceChildContextCloseDelay = getDynamicDataSourceChildContextCloseDelay(context.getEnvironment());
    }

    @Override
    public void afterPropertiesSet() {
        if (initialized) {
            logger.debug("DynamicDataSource[contextId : '{}' , config property name : '{}'] has been initialized",
                    context.getId(), dynamicJdbcConfigPropertyName);
            return;
        }
        initializeApplicationListeners();
        // MultiChildContext dynamic datasource will be initialized twice.
        if (null == this.delegate) {
            initializeDataSource();
        }
        initialized = true;
    }

    @Override
    public void destroy() {
        closeDynamicDataSourceChildContext(dynamicDataSourceChildContext, false);
        shutdownScheduler(closeScheduler);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getDelegate().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getDelegate().getConnection(username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return getDelegate().getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        getDelegate().setLogWriter(out);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return getDelegate().getLoginTimeout();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        getDelegate().setLoginTimeout(seconds);
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return getDelegate().getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return getDelegate().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return getDelegate().isWrapperFor(iface);
    }

    protected DataSource getDelegate() {
        DataSource dataSource = this.delegate;
        if (dataSource == null) {
            dataSource = initializeDataSource();
        }
        return dataSource;
    }

    private DataSource initializeDataSource() {
        return initializeDataSource(dynamicJdbcConfig, dynamicJdbcConfigPropertyName, context);
    }

    private void initializeApplicationListeners() {
        initializeRefreshingDynamicDataSourceListener();
    }

    private void initializeRefreshingDynamicDataSourceListener() {
        ConfigurableApplicationContext context = this.context;
        if (context instanceof DynamicJdbcChildContext) {
            DynamicJdbcChildContext childContext = (DynamicJdbcChildContext) context;
            ConfigurableApplicationContext parentContext = childContext.getParentContext();
            parentContext.addApplicationListener(new RefreshingDynamicDataSourceListener());
        } else {
            context.addApplicationListener(new RefreshingDynamicDataSourceListener());
        }
    }

    private DataSource initializeDataSource(DynamicJdbcConfig dynamicJdbcConfig,
                                            String dynamicJdbcConfigPropertyName, ConfigurableApplicationContext context) {
        DataSource latestDataSource = null;
        DynamicJdbcConfig dynamicDataSourceConfig = createDynamicDataSourceConfig(dynamicJdbcConfig);
        DynamicJdbcChildContext dynamicDataSourceChildContext = new DynamicJdbcChildContext(
                dynamicDataSourceConfig, dynamicJdbcConfigPropertyName, context, idGenerator);
        // Merge Parent Environment
        dynamicDataSourceChildContext.mergeParentEnvironment();
        // Refresh Context
        dynamicDataSourceChildContext.refresh();
        // Get the DataSource Bean from Child Context
        latestDataSource = getDataSource(dynamicDataSourceChildContext);
        synchronized (mutex) {
            // Set DataSource
            DataSource previousDataSource = DynamicDataSource.this.delegate;
            DynamicDataSource.this.delegate = latestDataSource;
            // Exchange
            ConfigurableApplicationContext previousDynamicDataSourceChildContext =
                    DynamicDataSource.this.dynamicDataSourceChildContext;
            DynamicDataSource.this.dynamicDataSourceChildContext = dynamicDataSourceChildContext;
            logger.info("DataSource Previous : {} , Current : {}", previousDataSource, latestDataSource);
            logger.info("DynamicDataSourceChildContext Previous : {} , Current : {}",
                    previousDynamicDataSourceChildContext, dynamicDataSourceChildContext);
            closeDynamicDataSourceChildContext(previousDynamicDataSourceChildContext, true);
        }

        return latestDataSource;
    }

    private DataSource getDataSource(ApplicationContext childContext) {
        Map<String, DataSource> dataSourceMap = childContext.getBeansOfType(DataSource.class);
        int size = dataSourceMap.size();
        if (size > 1) {
            throw new IllegalStateException(format("There are {} DataSource Beans[{}] in the {}", size, dataSourceMap, childContext.getId()));
        }

        logger.debug("Get DataSource Bean from {} : {}", childContext.getId(), dataSourceMap);

        DataSource dataSource = dataSourceMap.values().iterator().next();
        return dataSource;
    }

    protected DynamicJdbcConfig createDynamicDataSourceConfig(DynamicJdbcConfig dynamicJdbcConfig) {
        // Clone an instance from source
        DynamicJdbcConfig dynamicDataSourceConfig = DynamicJdbcConfigUtils.cloneDynamicJdbcConfig(dynamicJdbcConfig);
        initDynamicDynamicJdbcConfig(dynamicDataSourceConfig);
        return dynamicDataSourceConfig;
    }

    private void initDynamicDynamicJdbcConfig(DynamicJdbcConfig dynamicJdbcConfig) {
        // First, set the dynamic to be false in order to avoid DynamicJdbcContextProcessor execution
        // recursively
        dynamicJdbcConfig.setDynamic(false);
        // Second, remove other configs except DataSource
        dynamicJdbcConfig.setTransaction(null);
        dynamicJdbcConfig.setMybatis(null);
        dynamicJdbcConfig.setMybatisPlus(null);
        // Third, reset name
        dynamicJdbcConfig.setName(DynamicJdbcConfigUtils.generateDynamicDataSourceDynamicJdbcConfigName(dynamicJdbcConfig));
        // Finally, set BeanFactory
        dynamicJdbcConfig.setBeanFactory(beanFactory);
    }


    private void closeDynamicDataSourceChildContext(ConfigurableApplicationContext dynamicDataSourceChildContext,
                                                    boolean async) {
        if (dynamicDataSourceChildContext != null) {
            if (async) {
                long delay = dynamicDataSourceChildContextCloseDelay.toMillis();
                logger.info("DynamicDataSourceChildContext[{}] will be closed after {} ms",
                        dynamicDataSourceChildContext, delay);
                closeScheduler.schedule(() -> closeContext(dynamicDataSourceChildContext), delay,
                        TimeUnit.MILLISECONDS);
            } else {
                closeContext(dynamicDataSourceChildContext);
            }
        }
    }

    private void closeContext(ConfigurableApplicationContext context) {
        context.close();
        logger.info("DynamicDataSourceChildContext[{}] has been closed", context);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    private void shutdownScheduler(ScheduledExecutorService scheduler) {
        if (scheduler != null && !scheduler.isShutdown() && !scheduler.isTerminated()) {
            scheduler.shutdown();
        }
    }

    private ConfigurableApplicationContext findParentContext(ConfigurableApplicationContext eventSourceContext) {
        DynamicJdbcChildContext dynamicDataSourceChildContext = this.dynamicDataSourceChildContext;
        ConfigurableApplicationContext parentContext = dynamicDataSourceChildContext.getParentContext();

        if (Objects.equals(parentContext, eventSourceContext)) { // Single DynamicJdbcConfig
            return parentContext;
        }

        if (!(parentContext instanceof DynamicJdbcChildContext)) {
            return null;
        }

        if (Objects.equals(((DynamicJdbcChildContext) parentContext).getParentContext(), eventSourceContext)) { // Multiple
            // DynamicJdbcConfig
            return parentContext;
        }

        return null;
    }

    private class RefreshingDynamicDataSourceListener implements ApplicationListener<DynamicJdbcConfigChangedEvent> {

        @Override
        public void onApplicationEvent(DynamicJdbcConfigChangedEvent event) {
            // 如果配置变更，则重建数据源
            DynamicJdbcConfig dynamicJdbcConfig = event.getDynamicJdbcConfig();
            String dynamicJdbcConfigPropertyName = event.getPropertyName();
            ConfigurableApplicationContext context = event.getSource();
            if (Objects.equals(DynamicDataSource.this.dynamicJdbcConfigPropertyName, dynamicJdbcConfigPropertyName)) {
                ConfigurableApplicationContext parentContext = findParentContext(context);
                if (parentContext != null) {
                    initializeDataSource(dynamicJdbcConfig, dynamicJdbcConfigPropertyName, parentContext);
                } else {
                    logger.debug("DynamicJdbcConfigChangedEvent source is DynamicJdbcChildContext , but : {}",
                            context);
                }
            }
        }
    }
}
