package io.microsphere.dynamic.jdbc.spring.boot.shardingsphere.validation;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.dynamic.jdbc.spring.boot.config.validation.AbstractConfigurationConfigValidator;
import io.microsphere.dynamic.jdbc.spring.boot.config.validation.ConfigValidator;
import io.microsphere.dynamic.jdbc.spring.boot.config.validation.ValidationErrors;
import org.springframework.core.io.Resource;

import static org.springframework.util.StringUtils.hasText;

/**
 * Dynamic JDBC {@link ConfigValidator} for {@link DynamicJdbcConfig.ShardingSphere}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class ShardingSphereConfigValidator extends AbstractConfigurationConfigValidator<DynamicJdbcConfig.ShardingSphere> {

    @Override
    protected void doValidate(DynamicJdbcConfig dynamicJdbcConfig, DynamicJdbcConfig.ShardingSphere shardingSphere, ValidationErrors errors) {
        validateConfigResource(shardingSphere, errors);
    }

    private void validateConfigResource(DynamicJdbcConfig.ShardingSphere shardingSphere, ValidationErrors errors) {
        String configResource = shardingSphere.getConfigResource();
        if (hasText(configResource)) {
            Resource resource = context.getResource(configResource);
            if (!resource.exists()) {
                errors.addError("'{}' modules' 'config-resource'['{}'] can't be found", getModule(), configResource);
            }
        } else {
            errors.addError("'{}' modules' 'config-resource' attribute must not be blank", getModule());
        }
    }
}
