package io.microsphere.dynamic.jdbc.spring.boot.config.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Abstract {@link ConfigValidator}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class AbstractConfigValidator implements ConfigValidator, BeanFactoryAware, BeanClassLoaderAware, ApplicationContextAware {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected ClassLoader classLoader;

    protected ConfigurableListableBeanFactory beanFactory;

    protected ConfigurableApplicationContext context;

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
