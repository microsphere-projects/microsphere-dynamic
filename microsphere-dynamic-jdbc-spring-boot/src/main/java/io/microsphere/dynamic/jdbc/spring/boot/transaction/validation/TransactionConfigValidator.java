package io.microsphere.dynamic.jdbc.spring.boot.transaction.validation;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.dynamic.jdbc.spring.boot.config.validation.AbstractConfigurationConfigValidator;
import io.microsphere.dynamic.jdbc.spring.boot.config.validation.ConfigValidator;
import io.microsphere.dynamic.jdbc.spring.boot.config.validation.ValidationErrors;
import org.springframework.boot.autoconfigure.transaction.PlatformTransactionManagerCustomizer;
import org.springframework.util.StringUtils;

/**
 * Dynamic JDBC {@link ConfigValidator} for {@link DynamicJdbcConfig.Transaction}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class TransactionConfigValidator extends AbstractConfigurationConfigValidator<DynamicJdbcConfig.Transaction> {

    @Override
    protected void validateName(DynamicJdbcConfig dynamicJdbcConfig, String name, ValidationErrors errors) {
        if (StringUtils.hasText(name)) {
            if (beanFactory.containsBeanDefinition(name)) {
                errors.addError("'{}' modules' bean name['{}'] is already registered in the BeanFactory[{}]", getModule(), name, beanFactory);
            }
        }
    }

    @Override
    protected void doValidate(DynamicJdbcConfig dynamicJdbcConfig, DynamicJdbcConfig.Transaction transaction, ValidationErrors errors) {
        validateCustomizers(dynamicJdbcConfig, transaction, errors);
    }

    private void validateCustomizers(DynamicJdbcConfig dynamicJdbcConfig, DynamicJdbcConfig.Transaction transaction, ValidationErrors errors) {
        validateComponentType(transaction.getCustomizers(), "customizers", PlatformTransactionManagerCustomizer.class, errors);
    }
}
