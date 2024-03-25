package io.microsphere.dynamic.jdbc.spring.boot.config.validation;


import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;

/**
 * Config Validator
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public interface ConfigValidator {

    /**
     * Validate {@link DynamicJdbcConfig}
     * 
     * @param dynamicJdbcConfig {@link DynamicJdbcConfig}
     * @param dynamicJdbcConfigPropertyName the property name of {@link DynamicJdbcConfig}
     * @param validationErrors {@link ValidationErrors}
     */
    void validate(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName, ValidationErrors validationErrors);

}
