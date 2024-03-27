package io.microsphere.dynamic.jdbc.spring.boot.context;

import io.microsphere.dynamic.jdbc.spring.boot.config.ConfigurationCapable;
import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.util.StringUtils;

import static io.microsphere.spring.util.AnnotatedBeanDefinitionRegistryUtils.registerBeans;
import static io.microsphere.spring.util.EnvironmentUtils.resolvePlaceholders;
import static org.apache.commons.lang3.ArrayUtils.EMPTY_STRING_ARRAY;
import static org.springframework.cglib.core.Constants.EMPTY_CLASS_ARRAY;
import static org.springframework.util.ClassUtils.resolveClassName;

/**
 *
 * Abstract {@link DynamicJdbcConfig.Config Dynamic JDBC Config Config} {@link BeanDefinition}
 * Registrar
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 * @param <C> the type of {@link DynamicJdbcConfig.Config}
 */
public abstract class AbstractConfigurationConfigBeanDefinitionRegistrar<C extends DynamicJdbcConfig.Config>
        extends AbstractModuleConfigBeanDefinitionRegistrar implements ConfigurationCapable<C> {

    protected final Class<?> configurationClass;

    public AbstractConfigurationConfigBeanDefinitionRegistrar() {
        this.configurationClass = getConfigurationClass();
    }

    @Override
    protected boolean supports(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName, String module,
                               BeanDefinitionRegistry registry) {

        C configuration = getConfiguration(dynamicJdbcConfig);

        if (configuration == null) {
            logger.info("No '{}' modules' Config of DynamicJdbcConfig[name :{}] to register BeanDefinitions", module, dynamicJdbcConfig.getName());
            return false;
        }

        return true;
    }

    @Override
    public final void register(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName, String module,
                               BeanDefinitionRegistry registry) {

        C configuration = getConfiguration(dynamicJdbcConfig);

        registerConfigurationClasses(module, configuration, registry);

        register(dynamicJdbcConfig, dynamicJdbcConfigPropertyName, module, configuration, registry);
    }

    protected final void registerConfigurationClasses(String module, C configuration, BeanDefinitionRegistry registry) {

        String configurations = configuration.getConfigurations();

        if (!StringUtils.hasText(configurations)) {
            logger.info("No '{}' modules'[name : {}] Configuration class was configured to register", module, configuration.getName());
            return;
        }

        registerConfigurationClasses(configurations, registry);
    }

    protected final void registerConfigurationClasses(String configurations, BeanDefinitionRegistry registry) {
        Class<?>[] configurationClasses = loadConfigurationClasses(configurations);
        registerConfigurationClasses(configurationClasses, registry);
    }

    protected final void registerConfigurationClasses(Class<?>[] configurationClasses, BeanDefinitionRegistry registry) {
        registerBeans(registry, configurationClasses);
    }

    private Class<?>[] loadConfigurationClasses(String configurations) {
        String[] configurationClassNames = getConfigurationClassNames(configurations);

        int length = configurationClassNames.length;

        if (length < 1) {
            return EMPTY_CLASS_ARRAY;
        }

        Class<?>[] configurationClasses = new Class[length];
        for (int i = 0; i < length; i++) {
            configurationClasses[i] = resolveClassName(configurationClassNames[i], classLoader);
        }

        return configurationClasses;
    }

    private String[] getConfigurationClassNames(String configurations) {
        if (!StringUtils.hasText(configurations)) {
            return EMPTY_STRING_ARRAY;
        }
        String[] configurationClassNames = resolvePlaceholders(environment, configurations, String[].class, EMPTY_STRING_ARRAY);
        return configurationClassNames;
    }

    protected abstract void register(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName, String module, C configuration,
                                     BeanDefinitionRegistry registry);

}
