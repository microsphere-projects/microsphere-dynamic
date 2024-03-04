package io.microsphere.dynamic.jdbc.spring.boot.transaction.env;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.dynamic.jdbc.spring.boot.env.AbstractConfigurationConfigConfigurationPropertiesSynthesizer;
import io.microsphere.dynamic.jdbc.spring.boot.env.ConfigConfigurationPropertiesSynthesizer;
import org.springframework.boot.autoconfigure.transaction.TransactionProperties;

import java.util.Map;

/**
 * {@link ConfigConfigurationPropertiesSynthesizer} for Spring Transaction abstract
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 * @see TransactionProperties
 */
public class TransactionConfigConfigurationPropertiesSynthesizer
        extends AbstractConfigurationConfigConfigurationPropertiesSynthesizer<DynamicJdbcConfig.Transaction> {

    @Override
    protected String getConfigurationPropertiesClassName() {
        return "org.springframework.boot.autoconfigure.transaction.TransactionProperties";
    }

    @Override
    protected void synthesize(DynamicJdbcConfig dynamicJdbcConfig, String module, DynamicJdbcConfig.Transaction configuration,
                              Class<?> configurationPropertiesClass, Map<String, Object> properties) {

    }
}
