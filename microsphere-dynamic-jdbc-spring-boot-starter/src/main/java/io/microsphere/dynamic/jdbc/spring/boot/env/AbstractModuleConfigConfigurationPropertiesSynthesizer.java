package io.microsphere.dynamic.jdbc.spring.boot.env;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.dynamic.jdbc.spring.boot.config.ModuleCapable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.getModuleAutoConfigurationClassNames;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.getModuleExclusionAutoConfigurationClassNames;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.getModulePropertyNameAliases;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcUtils.flatProperties;

/**
 * {@link AbstractConfigConfigurationPropertiesSynthesizer} for Module
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class AbstractModuleConfigConfigurationPropertiesSynthesizer extends AbstractConfigConfigurationPropertiesSynthesizer
        implements ModuleCapable {

    @Override
    public final void synthesize(DynamicJdbcConfig dynamicJdbcConfig, Map<String, Object> properties) {

        String module = getModule();

        Assert.notNull(module, () -> "getModule() method must not return null!");

        if (!supports(dynamicJdbcConfig, module, properties)) {
            logger.info("DynamicJdbcConfig[name : '{}'] module '{}' is not supported", dynamicJdbcConfig.getName(), module);
            // if (isDynamicDynamicJdbcConfig(dynamicJdbcConfig)) {
            excludeModuleAutoConfigurationProperty(module, properties);
            // }
            return;
        }

        logger.debug("DynamicJdbcConfig[name : {}] Module '{}' pre-synthesize properties {}", dynamicJdbcConfig.getName(), module, properties);
        synthesize(dynamicJdbcConfig, module, properties);
        logger.debug("DynamicJdbcConfig[name : {}] Module '{}' post-synthesize properties {}", dynamicJdbcConfig.getName(), module, properties);
    }

    private void excludeModuleAutoConfigurationProperty(String module, Map<String, Object> properties) {
        Set<String> moduleAutoConfigurationClassNames = getModuleAutoConfigurationClassNames(context, module);
        excludeAutoConfigurationProperty(properties, moduleAutoConfigurationClassNames);
    }

    protected boolean supports(DynamicJdbcConfig dynamicJdbcConfig, String module, Map<String, Object> properties) {
        return true;
    }

    protected abstract void synthesize(DynamicJdbcConfig dynamicJdbcConfig, String module, Map<String, Object> properties);

    protected final void synthesizeModuleExclusionAutoConfigurationProperty(String module, Map<String, Object> properties) {
        Set<String> exclusionAutoConfigurationClassNames = getModuleExclusionAutoConfigurationClassNames(context, module);
        logger.debug("Module '{}' will append exclusion auto-configuration class names : {}", module, exclusionAutoConfigurationClassNames);
        excludeAutoConfigurationProperty(properties, exclusionAutoConfigurationClassNames);
    }

    protected void synthesizeConfigurationProperties(String module, Class<?> configurationPropertiesClass, Map<String, Object> sourceProperties,
                                                     Map<String, Object> properties) {
        if (CollectionUtils.isEmpty(sourceProperties)) {
            return;
        }

        String prefix = resolvePropertyNamePrefix(configurationPropertiesClass);

        synthesizeModuleProperties(module, sourceProperties, prefix, properties);
    }

    protected void synthesizeModuleProperties(String module, Map<String, Object> sourceProperties, String prefix, Map<String, Object> properties) {
        if (CollectionUtils.isEmpty(sourceProperties)) {
            logger.debug("No {} module property was configured", module);
            return;
        }

        Map<String, String> flattenProperties = flatProperties(sourceProperties);

        flattenProperties.forEach((propertyName, propertyValue) -> {
            Set<String> fullPropertyNames = resolveFullPropertyNames(module, prefix, propertyName);
            fullPropertyNames.forEach(fullPropertyName -> {
                if (filterModuleProperty(module, propertyName, fullPropertyName, propertyValue)) {
                    properties.put(fullPropertyName, propertyValue);
                    logger.debug("'{}' module synthesizes a new property [ source name : '{}'] : {} = {}", module, propertyName, fullPropertyName,
                            propertyValue);
                }
            });
        });
    }

    protected boolean filterModuleProperty(String module, String sourcePropertyName, String synthesizePropertyName, String propertyValue) {
        return true;
    }

    private Set<String> resolveFullPropertyNames(String module, String prefix, String name) {
        Set<String> aliases = getModulePropertyNameAliases(environment, module, name);
        Set<String> propertyNames = new HashSet<>(aliases.size() + 1);
        propertyNames.add(prefix + name);
        aliases.forEach(alias -> propertyNames.add(prefix + alias));
        return propertyNames;
    }

}
