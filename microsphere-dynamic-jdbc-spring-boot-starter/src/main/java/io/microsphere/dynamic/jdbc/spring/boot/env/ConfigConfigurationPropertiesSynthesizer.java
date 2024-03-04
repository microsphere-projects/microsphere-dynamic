package io.microsphere.dynamic.jdbc.spring.boot.env;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;

import java.util.Map;

/**
 * Dynamic JDBC Configuration Properties synthesizer
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public interface ConfigConfigurationPropertiesSynthesizer {

    void synthesize(DynamicJdbcConfig dynamicJdbcConfig, Map<String, Object> properties);

}
