package io.microsphere.dynamic.jdbc.spring.boot.context;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.env.EnvironmentCapable;

/**
 * {@link DynamicJdbcConfig JDBC Dynamic Config} {@link BeanDefinition} Registrar
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public interface ConfigBeanDefinitionRegistrar extends EnvironmentCapable {

    /**
     * Register the {@link BeanDefinition} of {@link DynamicJdbcConfig} or its members
     * 
     * @param dynamicJdbcConfig {@link DynamicJdbcConfig}
     * @param dynamicJdbcConfigPropertyName the property name of {@link DynamicJdbcConfig}
     * @param registry {@link BeanDefinitionRegistry}
     */
    void register(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName, BeanDefinitionRegistry registry);
}
