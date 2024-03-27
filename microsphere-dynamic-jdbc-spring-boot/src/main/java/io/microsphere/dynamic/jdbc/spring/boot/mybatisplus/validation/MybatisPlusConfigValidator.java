package io.microsphere.dynamic.jdbc.spring.boot.mybatisplus.validation;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.dynamic.jdbc.spring.boot.config.validation.AbstractConfigurationConfigValidator;
import io.microsphere.dynamic.jdbc.spring.boot.config.validation.ConfigValidator;
import io.microsphere.dynamic.jdbc.spring.boot.config.validation.ValidationErrors;

import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.MYBATIS_MODULE;

/**
 * Dynamic JDBC {@link ConfigValidator} for {@link DynamicJdbcConfig.MybatisPlus}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class MybatisPlusConfigValidator extends AbstractConfigurationConfigValidator<DynamicJdbcConfig.MybatisPlus> {

    @Override
    protected void doValidate(DynamicJdbcConfig dynamicJdbcConfig, DynamicJdbcConfig.MybatisPlus mybatisPlus, ValidationErrors errors) {
        if (dynamicJdbcConfig.hasMybatis()) {
            errors.addError("'{}' module and '{}' module must not be present at the same time", getModule(), MYBATIS_MODULE);
        }
    }
}
