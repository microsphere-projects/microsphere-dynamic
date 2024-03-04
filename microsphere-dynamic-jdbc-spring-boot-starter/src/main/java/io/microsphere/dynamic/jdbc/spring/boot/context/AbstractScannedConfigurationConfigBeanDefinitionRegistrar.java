package io.microsphere.dynamic.jdbc.spring.boot.context;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import static io.microsphere.spring.util.AnnotatedBeanDefinitionRegistryUtils.scanBasePackages;

/**
 * Scanned-feature {@link AbstractConfigurationConfigBeanDefinitionRegistrar}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class AbstractScannedConfigurationConfigBeanDefinitionRegistrar<C extends DynamicJdbcConfig.Config>
        extends AbstractConfigurationConfigBeanDefinitionRegistrar<C> {

    @Override
    protected final void register(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName, String module, C configuration,
                                  BeanDefinitionRegistry registry) {
        String basePackages = getBasePackages(configuration);
        if (shouldScanBasePackages()) {
            scanBasePackages(registry, basePackages);
        }
        register(dynamicJdbcConfig, dynamicJdbcConfigPropertyName, module, configuration, basePackages, registry);
    }

    protected boolean shouldScanBasePackages() {
        return true;
    }

    protected abstract String getBasePackages(C configuration);

    protected abstract void register(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName, String module, C configuration,
                                     String basePackages, BeanDefinitionRegistry registry);

}
