package io.microsphere.dynamic.jdbc.spring.boot.mybatisplus.config;

import io.microsphere.dynamic.jdbc.spring.boot.config.AbstractConfigurationConfigPostProcessor;
import io.microsphere.dynamic.jdbc.spring.boot.config.ConfigPostProcessor;
import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;

/**
 * 
 * {@link ConfigPostProcessor} for {@link DynamicJdbcConfig.MybatisPlus}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class MybatisPlusConfigPostProcessor
    extends AbstractConfigurationConfigPostProcessor<DynamicJdbcConfig.MybatisPlus> {

    @Override
    protected void postProcess(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName,
                               String module, DynamicJdbcConfig.MybatisPlus configuration) {}

}
