package io.microsphere.dynamic.jdbc.spring.boot.mybatis.config;

import io.microsphere.dynamic.jdbc.spring.boot.config.AbstractConfigurationConfigPostProcessor;
import io.microsphere.dynamic.jdbc.spring.boot.config.ConfigPostProcessor;
import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;

/**
 * 
 * {@link ConfigPostProcessor} for {@link DynamicJdbcConfig.Mybatis}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class MybatisConfigPostProcessor extends AbstractConfigurationConfigPostProcessor<DynamicJdbcConfig.Mybatis> {

    @Override
    protected void postProcess(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName,
                               String module, DynamicJdbcConfig.Mybatis configuration) {}

}
