package io.microsphere.dynamic.jdbc.spring.boot.config;


import io.microsphere.dynamic.jdbc.spring.boot.util.FunctionUtils;

/**
 * Abstract {@link ConfigPostProcessor} for {@link DynamicJdbcConfig.Config}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class AbstractConfigurationConfigPostProcessor<C extends DynamicJdbcConfig.Config> extends AbstractModuleConfigPostProcessor
        implements ConfigurationCapable<C> {

    protected final Class<?> configurationClass;

    public AbstractConfigurationConfigPostProcessor() {
        this.configurationClass = getConfigurationClass();
    }

    @Override
    protected boolean supports(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName, String module) {
        C configuration = getConfiguration(dynamicJdbcConfig);

        if (configuration == null) {
            logger.info("No '{}' Configuration of DynamicJdbcConfig[name :{}] was configured", module, dynamicJdbcConfig.getName());
            return false;
        }

        return true;
    }

    @Override
    protected void postProcess(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName, String module) {

        C configuration = getConfiguration(dynamicJdbcConfig);

        processName(dynamicJdbcConfig, dynamicJdbcConfigPropertyName, module, configuration);

        postProcess(dynamicJdbcConfig, dynamicJdbcConfigPropertyName, module, configuration);
    }

    protected void processName(DynamicJdbcConfig config, String dynamicJdbcConfigPropertyName, String module, C configuration) {
        logger.debug("The original name of {} Config : '{}' , DynamicJdbcConfig bean name : '{}'", module, configuration.getName(),
                dynamicJdbcConfigPropertyName);

        FunctionUtils.setIfAbsent(configuration::getName, () -> generateDefaultModuleName(config, module, configuration, dynamicJdbcConfigPropertyName),
                configuration::setName);

        logger.debug("The processed name of {} Config : '{}' , DynamicJdbcConfig bean name : '{}'", module, configuration.getName(),
                dynamicJdbcConfigPropertyName);
    }

    protected String generateDefaultModuleName(DynamicJdbcConfig dynamicJdbcConfig, String module, C configuration,
                                               String dynamicJdbcConfigPropertyName) {
        StringBuilder moduleNameBuilder = new StringBuilder(dynamicJdbcConfig.getName()).append(".").append(module);
        return moduleNameBuilder.toString();
    }

    protected abstract void postProcess(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName, String module, C configuration);

}
