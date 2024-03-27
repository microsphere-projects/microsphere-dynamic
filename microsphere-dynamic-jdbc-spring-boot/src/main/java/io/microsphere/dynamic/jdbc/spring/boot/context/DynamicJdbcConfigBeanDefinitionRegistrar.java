package io.microsphere.dynamic.jdbc.spring.boot.context;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.generateDynamicJdbcConfigBeanName;
import static io.microsphere.spring.util.BeanRegistrar.registerFactoryBean;

/**
 * {@link ConfigBeanDefinitionRegistrar} for {@link DynamicJdbcConfig} instance
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class DynamicJdbcConfigBeanDefinitionRegistrar extends AbstractConfigBeanDefinitionRegistrar {

    @Override
    public void register(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName, BeanDefinitionRegistry registry) {
        String dynamicJdbcConfigBeanName = generateDynamicJdbcConfigBeanName(dynamicJdbcConfig, dynamicJdbcConfigPropertyName);
        registerFactoryBean(registry, dynamicJdbcConfigBeanName, dynamicJdbcConfig);
    }

}
