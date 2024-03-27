package io.microsphere.dynamic.jdbc.spring.boot.transaction.config;

import io.microsphere.dynamic.jdbc.spring.boot.config.AbstractConfigurationConfigPostProcessor;
import io.microsphere.dynamic.jdbc.spring.boot.config.ConfigPostProcessor;
import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;

/**
 * 
 * {@link ConfigPostProcessor} for {@link DynamicJdbcConfig.Transaction}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class TransactionConfigPostProcessor extends AbstractConfigurationConfigPostProcessor<DynamicJdbcConfig.Transaction> {

    @Override
    protected void postProcess(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName, String module,
                               DynamicJdbcConfig.Transaction configuration) {

    }
}
