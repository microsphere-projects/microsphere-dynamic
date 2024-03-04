package io.microsphere.dynamic.jdbc.spring.boot.env;

import io.microsphere.dynamic.jdbc.spring.boot.config.ConfigurationCapable;
import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * {@link AbstractConfigConfigurationPropertiesSynthesizer} for {@link DynamicJdbcConfig.Config}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 * @param <C> The type of {@link DynamicJdbcConfig.Config}
 */
public abstract class AbstractConfigurationConfigConfigurationPropertiesSynthesizer<C extends DynamicJdbcConfig.Config>
        extends AbstractModuleConfigConfigurationPropertiesSynthesizer implements ConfigurationCapable<C> {

    protected final Class<?> configurationClass;

    private Class<?> configurationPropertiesClass;

    private C configuration;

    public AbstractConfigurationConfigConfigurationPropertiesSynthesizer() {
        this.configurationClass = getConfigurationClass();
    }

    @Override
    protected boolean supports(DynamicJdbcConfig dynamicJdbcConfig, String module, Map<String, Object> properties) {
        Class<?> configurationPropertiesClass = getConfigurationPropertiesClass();

        if (configurationPropertiesClass == null) {
            logger.info("No ConfigurationProperties class[name : {}] was configured in the Class Path", getConfigurationPropertiesClassName());
            return false;
        }

        this.configuration = getConfiguration(dynamicJdbcConfig);

        if (configuration == null) {
            logger.info("No {} Config of DynamicJdbcConfig[name : {}] for ConfigurationProperties class : {}", module, dynamicJdbcConfig.getName(),
                    configurationPropertiesClass);
            return false;
        }

        logger.debug("DynamicJdbcConfig[name : {}] Module '{}' - Configuration : {}", dynamicJdbcConfig.getName(), module, configuration);

        return true;
    }

    @Override
    protected void synthesize(DynamicJdbcConfig dynamicJdbcConfig, String module, Map<String, Object> properties) {

        Class<?> configurationPropertiesClass = getConfigurationPropertiesClass();

        synthesizeModuleProperties(dynamicJdbcConfig, module, configuration, configurationPropertiesClass, properties);
        synthesizeModuleExclusionProperties(dynamicJdbcConfig, module, configuration, configurationPropertiesClass, properties);
        synthesize(dynamicJdbcConfig, module, configuration, configurationPropertiesClass, properties);

    }

    protected Class<?> getConfigurationPropertiesClass() {
        if (configurationPropertiesClass == null) {
            configurationPropertiesClass = resolveConfigurationPropertiesClass();
        }
        return configurationPropertiesClass;
    }

    protected void synthesizeModuleExclusionProperties(DynamicJdbcConfig dynamicJdbcConfig, String module, C configuration,
                                                       Class<?> configurationPropertiesClass, Map<String, Object> properties) {
        synthesizeModuleExclusionAutoConfigurationProperties(dynamicJdbcConfig, module, configuration, configurationPropertiesClass, properties);
    }

    protected void synthesizeModuleExclusionAutoConfigurationProperties(DynamicJdbcConfig dynamicJdbcConfig, String module, C configuration,
                                                                        Class<?> configurationPropertiesClass, Map<String, Object> properties) {
        synthesizeModuleExclusionAutoConfigurationProperty(module, properties);
    }

    protected Class<?> resolveConfigurationPropertiesClass() {
        Class<?> configurationPropertiesClass = null;
        try {
            configurationPropertiesClass = classLoader.loadClass(getConfigurationPropertiesClassName());
        } catch (Throwable e) {
        }
        return configurationPropertiesClass;
    }

    /**
     * @return The name of {@link Class} that annotated Spring Boot
     *         {@link ConfigurationProperties @ConfigurationProperties} could be null, or absent in the
     *         Class Path.
     */
    protected @Nullable abstract String getConfigurationPropertiesClassName();

    protected void synthesizeModuleProperties(DynamicJdbcConfig dynamicJdbcConfig, String module, C configuration,
                                              Class<?> configurationPropertiesClass, Map<String, Object> properties) {
        Map<String, Object> moduleProperties = configuration.getProperties();
        if (CollectionUtils.isEmpty(moduleProperties)) {
            logger.info("No property of DynamicJdbcConfig[name : {}] Module '{}' Config '{}' was configured!", dynamicJdbcConfig.getName(), module,
                    configuration.getName());
            return;
        }

        String prefix = resolvePropertyNamePrefix(configurationPropertiesClass);

        synthesizeModuleProperties(module, moduleProperties, prefix, properties);
    }


    /**
     * Sub-Class should override this method to synthesize more properties
     * 
     * @param dynamicJdbcConfig {@link DynamicJdbcConfig}
     * @param module module
     * @param configuration {@link C}
     * @param configurationPropertiesClass {@link Class} that annotated Spring Boot
     *        {@link ConfigurationProperties @ConfigurationProperties}
     * @param properties properties to be synthesized
     */
    protected abstract void synthesize(DynamicJdbcConfig dynamicJdbcConfig, String module, C configuration, Class<?> configurationPropertiesClass,
                                       Map<String, Object> properties);

}
