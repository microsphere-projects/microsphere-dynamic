package io.microsphere.dynamic.jdbc.spring.boot.config;

import org.springframework.core.Ordered;

/**
 * The Post-Processor interface for Config
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public interface ConfigPostProcessor extends Ordered {

    /**
     * Post-Process {@link DynamicJdbcConfig} that may be modified in the specified implementation
     * 
     * @param dynamicJdbcConfig {@link DynamicJdbcConfig}
     * @param dynamicJdbcConfigPropertyName the property name of {@link DynamicJdbcConfig}
     */
    void postProcess(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName);

}
