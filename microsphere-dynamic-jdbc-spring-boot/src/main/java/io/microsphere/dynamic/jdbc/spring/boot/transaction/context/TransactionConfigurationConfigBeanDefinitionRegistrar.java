package io.microsphere.dynamic.jdbc.spring.boot.transaction.context;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.dynamic.jdbc.spring.boot.context.AbstractConfigurationConfigBeanDefinitionRegistrar;
import io.microsphere.dynamic.jdbc.spring.boot.context.ConfigBeanDefinitionRegistrar;
import org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

import static org.springframework.util.StringUtils.commaDelimitedListToStringArray;

/**
 * {@link ConfigBeanDefinitionRegistrar} for Transaction
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 * @see ShardingSphereAutoConfiguration
 */
public class TransactionConfigurationConfigBeanDefinitionRegistrar
        extends AbstractConfigurationConfigBeanDefinitionRegistrar<DynamicJdbcConfig.Transaction> {

    @Override
    protected void register(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName, String module,
                            DynamicJdbcConfig.Transaction transaction, BeanDefinitionRegistry registry) {
        registerAlias(transaction);
        registerCustomizers(dynamicJdbcConfig, module, transaction, registry);
    }

    private void registerAlias(DynamicJdbcConfig.Transaction transaction) {
        String name = transaction.getName();
        if (StringUtils.hasText(name)) {
            String[] aliases = commaDelimitedListToStringArray(name);
            registerBeanAliases(PlatformTransactionManager.class, aliases);
        }
    }

    private void registerCustomizers(DynamicJdbcConfig dynamicJdbcConfig, String module, DynamicJdbcConfig.Transaction transaction,
                                     BeanDefinitionRegistry registry) {
        String customizers = transaction.getCustomizers();
        if (!StringUtils.hasText(customizers)) {
            logger.info("No '{}' modules' PlatformTransactionManagerCustomizer Config of DynamicJdbcConfig[name :{}] to register BeanDefinitions",
                    module, dynamicJdbcConfig.getName());
            return;
        }
        registerConfigurationClasses(customizers, registry);
    }
}
