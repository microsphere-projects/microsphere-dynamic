package io.microsphere.dynamic.jdbc.spring.boot.env;

import io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;

import static io.microsphere.spring.boot.autoconfigure.ConfigurableAutoConfigurationImportFilter.AUTO_CONFIGURE_EXCLUDE_PROPERTY_NAME;
import static io.microsphere.spring.boot.constants.SpringBootPropertyConstants.SPRING_AUTO_CONFIGURE_EXCLUDE_PROPERTY_NAME;
import static io.microsphere.spring.util.EnvironmentUtils.getConversionService;
import static io.microsphere.spring.util.PropertySourcesUtils.normalizePrefix;
import static io.microsphere.text.FormatUtils.format;
import static org.springframework.util.StringUtils.collectionToCommaDelimitedString;

/**
 * Abstract {@link ConfigConfigurationPropertiesSynthesizer} class supplies the template methods for
 * subtype.
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class AbstractConfigConfigurationPropertiesSynthesizer
        implements ConfigConfigurationPropertiesSynthesizer, EnvironmentAware, BeanClassLoaderAware, BeanFactoryAware, ApplicationContextAware {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected ConfigurableEnvironment environment;

    protected ClassLoader classLoader;

    protected ConfigurableApplicationContext context;

    protected ConfigurableListableBeanFactory beanFactory;

    protected String resolvePropertyNamePrefix(Class<?> configurationPropertiesClass) {
        ConfigurationProperties configurationProperties = AnnotationUtils.findAnnotation(configurationPropertiesClass, ConfigurationProperties.class);
        String prefix = "";
        if (configurationProperties == null) {
            throw new IllegalArgumentException(format("The @ConfigurationProperties annotation can't be found in the ConfigurationProperties Class : {}",
                            configurationPropertiesClass.getSimpleName()));
        } else {
            prefix = configurationProperties.prefix();
            if (prefix.isEmpty()) {
                prefix = configurationProperties.value();
            }
            prefix = normalizePrefix(prefix);
            logger.debug("The prefix '{}' was resolved from The ConfigurationProperties Class : {}", prefix, configurationPropertiesClass.getName());
        }
        return prefix;
    }

    protected final void excludeAutoConfigurationProperty(Map<String, Object> properties, Collection<String> exclusionAutoConfigurationClassNames) {
        if (CollectionUtils.isEmpty(exclusionAutoConfigurationClassNames)) {
            return;
        }
        String propertyName = AUTO_CONFIGURE_EXCLUDE_PROPERTY_NAME;
        String configuredClassNames = environment.getProperty(SPRING_AUTO_CONFIGURE_EXCLUDE_PROPERTY_NAME);
        String newPropertyValue = collectionToCommaDelimitedString(exclusionAutoConfigurationClassNames);

        DynamicJdbcPropertyUtils.appendCommaDelimitedPropertyValues(properties, propertyName, configuredClassNames);
        DynamicJdbcPropertyUtils.appendCommaDelimitedPropertyValues(properties, propertyName, newPropertyValue);

        logger.debug("The property[name : '{}'] of exclusion auto-configuration class names \n\tCurrent : {} \n\tNew : {} \n\tEffective : {}",
                propertyName, configuredClassNames, exclusionAutoConfigurationClassNames, properties.get(propertyName));
    }

    protected <T> T getProperty(Map<String, Object> properties, String propertyName, Class<T> propertyType, T defaultValue) {
        Object propertyObject = properties.get(propertyName);
        T propertyValue = defaultValue;
        if (propertyObject != null) {
            ConversionService conversionService = getConversionService(environment);
            if (conversionService != null) {
                if (conversionService.canConvert(propertyObject.getClass(), propertyType)) {
                    propertyValue = conversionService.convert(propertyObject, propertyType);
                }
            }
        }
        return propertyValue;
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.context = (ConfigurableApplicationContext) context;
    }
}
