package io.microsphere.dynamic.jdbc.spring.boot.config.validation;

import io.microsphere.dynamic.jdbc.spring.boot.config.ConfigurationCapable;
import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;

import java.util.Map;

import static org.springframework.util.ClassUtils.isAssignable;
import static org.springframework.util.ClassUtils.isPresent;
import static org.springframework.util.ClassUtils.resolveClassName;
import static org.springframework.util.StringUtils.commaDelimitedListToStringArray;

/**
 * Abstract {@link ConfigValidator} Class for {@link DynamicJdbcConfig.Config}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see ConfigValidator
 * @see DynamicJdbcConfig.Config
 * @since 1.0.0
 */
public abstract class AbstractConfigurationConfigValidator<C extends DynamicJdbcConfig.Config> extends AbstractConfigValidator
        implements ConfigurationCapable<C> {

    @Override
    public final void validate(DynamicJdbcConfig dynamicJdbcConfig, String shardingJdbConfigPropertyName, ValidationErrors errors) {
        C configuration = getConfiguration(dynamicJdbcConfig);
        if (configuration == null) {
            logger.info("No '{}' modules' Config of DynamicJdbcConfig[name :{}] to be validated", getModule(), dynamicJdbcConfig.getName());
            return;
        }
        validateConfiguration(dynamicJdbcConfig, configuration, errors);
        doValidate(dynamicJdbcConfig, configuration, errors);
    }

    private void validateConfiguration(DynamicJdbcConfig dynamicJdbcConfig, C configuration, ValidationErrors errors) {
        validateName(dynamicJdbcConfig, configuration.getName(), errors);
        validateConfigurations(dynamicJdbcConfig, configuration.getConfigurations(), errors);
        validateProperties(dynamicJdbcConfig, configuration.getProperties(), errors);
    }

    protected void validateName(DynamicJdbcConfig dynamicJdbcConfig, String name, ValidationErrors errors) {}

    protected void validateConfigurations(DynamicJdbcConfig dynamicJdbcConfig, String configurations, ValidationErrors errors) {
        validateComponentType(configurations, "configurations", Object.class, errors);
    }

    protected void validateProperties(DynamicJdbcConfig dynamicJdbcConfig, Map<String, Object> properties, ValidationErrors errors) {}

    protected abstract void doValidate(DynamicJdbcConfig dynamicJdbcConfig, C configuration, ValidationErrors errors);

    protected final void validateComponentType(String classValue, String propertyName, Class<?> targetType, ValidationErrors errors) {
        String[] classNames = commaDelimitedListToStringArray(classValue);
        for (String className : classNames) {
            if (!isPresent(className, classLoader)) {
                errors.addError("'{}' modules' '{}' property class '{}' can't be found", getModule(), propertyName, className);
            } else {
                Class<?> componentClass = resolveClassName(className, classLoader);
                if (!isAssignable(targetType, componentClass)) {
                    errors.addError("'{}' modules' '{}' property class '{}' is not the target type '{}'", getModule(), propertyName, className,
                            targetType.getName());
                }
            }
        }
    }


}
