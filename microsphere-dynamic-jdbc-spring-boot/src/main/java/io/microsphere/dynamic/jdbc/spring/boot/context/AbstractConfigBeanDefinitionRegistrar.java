package io.microsphere.dynamic.jdbc.spring.boot.context;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.AnnotationConfigRegistry;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;

/**
 * Abstract {@link DynamicJdbcConfig Dynamic JDBC Config} {@link BeanDefinition} Registrar
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class AbstractConfigBeanDefinitionRegistrar
        implements ConfigBeanDefinitionRegistrar, BeanClassLoaderAware, BeanFactoryAware, ApplicationContextAware, EnvironmentAware {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected ClassLoader classLoader;

    protected ConfigurableApplicationContext context;

    protected ConfigurableListableBeanFactory beanFactory;

    protected Environment environment;

    protected @Nullable AnnotationConfigRegistry annotationConfigRegistry;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = (ConfigurableApplicationContext) applicationContext;
        if (applicationContext instanceof AnnotationConfigRegistry) {
            this.annotationConfigRegistry = (AnnotationConfigRegistry) context;
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public ApplicationContext getContext() {
        return context;
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
