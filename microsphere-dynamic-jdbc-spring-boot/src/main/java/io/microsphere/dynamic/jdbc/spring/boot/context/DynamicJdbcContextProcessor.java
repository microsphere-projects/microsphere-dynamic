package io.microsphere.dynamic.jdbc.spring.boot.context;

import io.microsphere.dynamic.jdbc.spring.boot.config.ConfigPostProcessor;
import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.dynamic.jdbc.spring.boot.config.validation.ConfigValidationException;
import io.microsphere.dynamic.jdbc.spring.boot.config.validation.ConfigValidator;
import io.microsphere.dynamic.jdbc.spring.boot.config.validation.ValidationErrors;
import io.microsphere.dynamic.jdbc.spring.boot.datasource.DynamicDataSource;
import io.microsphere.dynamic.jdbc.spring.boot.env.ConfigConfigurationPropertiesSynthesizer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.cloneDynamicJdbcConfig;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.generateSynthesizedPropertySourceName;
import static io.microsphere.spring.boot.autoconfigure.ConfigurableAutoConfigurationImportFilter.AUTO_CONFIGURE_EXCLUDE_PROPERTY_NAME;
import static io.microsphere.spring.boot.constants.SpringBootPropertyConstants.SPRING_AUTO_CONFIGURE_EXCLUDE_PROPERTY_NAME;
import static io.microsphere.spring.util.PropertySourcesUtils.findConfiguredPropertySourceName;
import static io.microsphere.spring.util.SpringFactoriesLoaderUtils.loadFactories;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

/**
 * Dynamic JDBC Context Processor
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
class DynamicJdbcContextProcessor {

    private List<ConfigPostProcessor> configPostProcessors;

    private List<ConfigValidator> configValidators;

    private List<ConfigConfigurationPropertiesSynthesizer> propertiesSynthesizers;

    private List<ConfigBeanDefinitionRegistrar> beanDefinitionRegistrars;

    void process(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName, ConfigurableApplicationContext context) {

        // Enhance Spring Context
        registerAnnotationConfigProcessors(context);

        // Post-Process DynamicJdbcConfig
        postProcessDynamicJdbcConfig(dynamicJdbcConfig, dynamicJdbcConfigPropertyName, context);

        // Validate DynamicJdbcConfig
        validateDynamicJdbcConfig(dynamicJdbcConfig, dynamicJdbcConfigPropertyName, context);

        // Process Dynamic Modules if enabled
        if (dynamicJdbcConfig.isDynamic()) {
            processDynamic(dynamicJdbcConfig, dynamicJdbcConfigPropertyName, context);
        }

        // Process DynamicJdbc Configuration Properties
        processDynamicJdbcConfigurationProperties(dynamicJdbcConfig, dynamicJdbcConfigPropertyName, context);

        // Register DynamicJdbcConfig BeanDefinitions
        registerDynamicJdbcConfigBeanDefinitions(dynamicJdbcConfig, dynamicJdbcConfigPropertyName, context);
    }

    private void registerAnnotationConfigProcessors(ConfigurableApplicationContext context) {
        BeanDefinitionRegistry beanDefinitionRegistry = resolveBeanDefinitionRegistry(context);
        AnnotationConfigUtils.registerAnnotationConfigProcessors(beanDefinitionRegistry);
    }

    private void postProcessDynamicJdbcConfig(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName,
                                              ConfigurableApplicationContext context) {
        List<ConfigPostProcessor> configPostProcessors = getConfigPostProcessors(context);
        configPostProcessors.forEach(configPostProcessor -> configPostProcessor.postProcess(dynamicJdbcConfig, dynamicJdbcConfigPropertyName));
    }

    private void validateDynamicJdbcConfig(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName,
                                           ConfigurableApplicationContext context) throws ConfigValidationException {
        List<ConfigValidator> configValidators = getConfigValidators(context);
        ValidationErrors validationErrors = new ValidationErrors(dynamicJdbcConfig.getName());
        configValidators.forEach(configValidator -> {
            configValidator.validate(dynamicJdbcConfig, dynamicJdbcConfigPropertyName, validationErrors);
        });
        // If validation is invalid, throws a ConfigValidationException
        if (!validationErrors.isValid()) {
            throw new ConfigValidationException(validationErrors.toString());
        }
    }

    private void processDynamic(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName,
                                ConfigurableApplicationContext context) {
        // For now, it just supports Dynamic DataSource
        processDynamicDataSource(dynamicJdbcConfig, dynamicJdbcConfigPropertyName, context);
        // TODO support more dynamic features
    }

    private void processDynamicDataSource(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName,
                                          ConfigurableApplicationContext context) {
        String beanName = "DynamicJdbcDynamicDataSource";
        registerDynamicDataSourceBeanDefinition(dynamicJdbcConfig, dynamicJdbcConfigPropertyName, beanName, context);
        // remove the DataSource configs from DynamicJdbcConfig
        removeDataSourceConfigs(dynamicJdbcConfig);
    }

    private void removeDataSourceConfigs(DynamicJdbcConfig dynamicJdbcConfig) {
        // Remove DataSource Config
        dynamicJdbcConfig.setDataSource(emptyList());
        // Remove HA DataSource Config
        dynamicJdbcConfig.setHighAvailabilityDataSource(emptyMap());
        // Remove ShardingSphere Config
        dynamicJdbcConfig.setShardingSphere(null);
    }

    private void registerDynamicDataSourceBeanDefinition(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName,
                                                         String beanName, ConfigurableApplicationContext context) {
        BeanDefinition beanDefinition = genericBeanDefinition(DynamicDataSource.class)
                .addConstructorArgValue(cloneDynamicJdbcConfig(dynamicJdbcConfig))
                .addConstructorArgValue(dynamicJdbcConfigPropertyName)
                .addConstructorArgValue(context)
                .getBeanDefinition();
        BeanDefinitionRegistry registry = resolveBeanDefinitionRegistry(context);
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    private void processDynamicJdbcConfigurationProperties(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName,
                                                           ConfigurableApplicationContext context) {
        MapPropertySource dynamicJdbcConfigPropertySource =
                addDynamicJdbcConfigPropertySource(dynamicJdbcConfig, dynamicJdbcConfigPropertyName, context);
        if (context instanceof DynamicJdbcChildContext) {
            addExclusionAutoConfigurationPropertySource(context, dynamicJdbcConfigPropertySource);
        }
    }

    /**
     * Add {@link DynamicJdbcConfig} {@link PropertySource} into {@link ConfigurableEnvironment} for
     * Auto-Configuration classes Dynamic JDBC requires.
     *
     * @param dynamicJdbcConfig             {@link DynamicJdbcConfig}
     * @param dynamicJdbcConfigPropertyName the property name of {@link DynamicJdbcConfig}
     * @param context                       {@link ConfigurableApplicationContext}
     * @return {@link PropertySource}
     */
    private MapPropertySource addDynamicJdbcConfigPropertySource(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName,
                                                                 ConfigurableApplicationContext context) {
        ConfigurableEnvironment environment = context.getEnvironment();
        MutablePropertySources propertySources = environment.getPropertySources();
        String currentPropertySourceName = findConfiguredPropertySourceName(environment, dynamicJdbcConfigPropertyName);
        List<ConfigConfigurationPropertiesSynthesizer> configConfigurationPropertiesSynthesizers =
                getDynamicJdbcConfigurationPropertiesSynthesizers(context);
        MapPropertySource dynamicJdbcPropertySource =
                buildDynamicJdbcPropertySource(dynamicJdbcConfig, dynamicJdbcConfigPropertyName, configConfigurationPropertiesSynthesizers);
        // Add dynamicJdbcPropertySource after current one
        if (currentPropertySourceName != null) {
            propertySources.addAfter(currentPropertySourceName, dynamicJdbcPropertySource);
        } else {
            propertySources.addFirst(dynamicJdbcPropertySource);
        }
        return dynamicJdbcPropertySource;
    }

    private void addExclusionAutoConfigurationPropertySource(ConfigurableApplicationContext context,
                                                             MapPropertySource dynamicJdbcConfigPropertySource) {
        ConfigurableEnvironment environment = context.getEnvironment();
        MutablePropertySources propertySources = environment.getPropertySources();
        Object propertyValue = dynamicJdbcConfigPropertySource.getProperty(AUTO_CONFIGURE_EXCLUDE_PROPERTY_NAME);

        String springExcludePropertyName = SPRING_AUTO_CONFIGURE_EXCLUDE_PROPERTY_NAME;
        String propertySourceName = generateSynthesizedPropertySourceName(springExcludePropertyName);
        propertySources.addFirst(new MapPropertySource(propertySourceName, Collections.singletonMap(springExcludePropertyName, propertyValue)));
    }

    private void registerDynamicJdbcConfigBeanDefinitions(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName,
                                                          ConfigurableApplicationContext context) {
        List<ConfigBeanDefinitionRegistrar> beanDefinitionRegistrars = getDynamicJdbcConfigBeanDefinitionRegistrars(context);
        BeanDefinitionRegistry beanDefinitionRegistry = resolveBeanDefinitionRegistry(context);
        beanDefinitionRegistrars.forEach(beanDefinitionRegistrar -> {
            beanDefinitionRegistrar.register(dynamicJdbcConfig, dynamicJdbcConfigPropertyName, beanDefinitionRegistry);
        });
    }

    private BeanDefinitionRegistry resolveBeanDefinitionRegistry(ConfigurableApplicationContext context) {
        BeanDefinitionRegistry beanDefinitionRegistry = castBeanDefinitionRegistry(context);
        if (beanDefinitionRegistry == null) {
            beanDefinitionRegistry = castBeanDefinitionRegistry(context.getBeanFactory());
        }
        if (beanDefinitionRegistry == null) {
            throw new IllegalArgumentException(String.format("BeanDefinitionRegistry can't be resolved from ApplicationContext[id : %s , class : %s]",
                    context.getId(), context.getClass().getName()));
        }
        return beanDefinitionRegistry;
    }

    private BeanDefinitionRegistry castBeanDefinitionRegistry(Object object) {
        BeanDefinitionRegistry beanDefinitionRegistry = null;
        if (object instanceof BeanDefinitionRegistry) {
            beanDefinitionRegistry = (BeanDefinitionRegistry) object;
        }
        return beanDefinitionRegistry;
    }

    private MapPropertySource buildDynamicJdbcPropertySource(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName,
                                                             List<ConfigConfigurationPropertiesSynthesizer> configConfigurationPropertiesSynthesizers) {
        String propertySourceName = generateSynthesizedPropertySourceName(dynamicJdbcConfigPropertyName);
        Map<String, Object> properties = new HashMap<>();
        configConfigurationPropertiesSynthesizers.forEach(synthesizer -> {
            synthesizer.synthesize(dynamicJdbcConfig, properties);
        });
        return new MapPropertySource(propertySourceName, properties);
    }

    private List<ConfigPostProcessor> getConfigPostProcessors(ConfigurableApplicationContext context) {
        if (this.configPostProcessors == null) {
            this.configPostProcessors = loadFactories(context, ConfigPostProcessor.class);
        }
        return this.configPostProcessors;
    }


    private List<ConfigValidator> getConfigValidators(ConfigurableApplicationContext context) {
        if (this.configValidators == null) {
            this.configValidators = loadFactories(context, ConfigValidator.class);
        }
        return this.configValidators;
    }

    private List<ConfigConfigurationPropertiesSynthesizer> getDynamicJdbcConfigurationPropertiesSynthesizers(
            ConfigurableApplicationContext context) {
        if (this.propertiesSynthesizers == null) {
            this.propertiesSynthesizers = loadFactories(context, ConfigConfigurationPropertiesSynthesizer.class);
        }
        return this.propertiesSynthesizers;
    }

    private List<ConfigBeanDefinitionRegistrar> getDynamicJdbcConfigBeanDefinitionRegistrars(ConfigurableApplicationContext context) {
        if (beanDefinitionRegistrars == null) {
            beanDefinitionRegistrars = loadFactories(context, ConfigBeanDefinitionRegistrar.class);
        }
        return beanDefinitionRegistrars;
    }
}
