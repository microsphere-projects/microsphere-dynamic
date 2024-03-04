package io.microsphere.dynamic.jdbc.spring.boot.mybatis.validation;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.dynamic.jdbc.spring.boot.config.validation.AbstractConfigurationConfigValidator;
import io.microsphere.dynamic.jdbc.spring.boot.config.validation.ConfigValidator;
import io.microsphere.dynamic.jdbc.spring.boot.config.validation.ValidationErrors;

import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.MYBATIS_PLUS_MODULE;

/**
 * Dynamic JDBC {@link ConfigValidator} for {@link DynamicJdbcConfig.Mybatis}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class MybatisConfigValidator extends AbstractConfigurationConfigValidator<DynamicJdbcConfig.Mybatis> {

    @Override
    protected void doValidate(DynamicJdbcConfig dynamicJdbcConfig, DynamicJdbcConfig.Mybatis mybatis, ValidationErrors errors) {
        if (dynamicJdbcConfig.hasMybatisPlus()) {
            errors.addError("'{}' module and '{}' module must not be present at the same time", getModule(), MYBATIS_PLUS_MODULE);
        }
    }
}
