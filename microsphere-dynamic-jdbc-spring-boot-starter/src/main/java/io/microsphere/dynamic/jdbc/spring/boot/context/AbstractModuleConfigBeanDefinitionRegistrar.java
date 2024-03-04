package io.microsphere.dynamic.jdbc.spring.boot.context;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.dynamic.jdbc.spring.boot.config.ModuleCapable;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * {@link AbstractConfigBeanDefinitionRegistrar} for Module
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class AbstractModuleConfigBeanDefinitionRegistrar extends AbstractConfigBeanDefinitionRegistrar implements ModuleCapable {

    @Override
    public final void register(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName, BeanDefinitionRegistry registry) {
        String module = getModule();

        Assert.notNull(module, () -> "getModule() method must not return null!");

        if (!supports(dynamicJdbcConfig, dynamicJdbcConfigPropertyName, module, registry)) {
            logger.info("{} is not supported for DynamicJdbcConfig[name :{}] module[name : {}] ", getClass().getSimpleName(),
                    dynamicJdbcConfig.getName(), module);
            return;
        }

        register(dynamicJdbcConfig, dynamicJdbcConfigPropertyName, module, registry);
    }

    protected final void registerBeanAliases(String beanName, String... aliases) {
        if (beanFactory instanceof BeanDefinitionRegistry) {
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
            for (String alias : aliases) {
                registry.registerAlias(beanName, alias);
                logger.debug("'{}' module register alias '{}' for bean name : '{}'", getModule(), alias, beanName);
            }
        }
    }

    protected final void registerBeanAliases(Class<?> beanType, String... aliases) {
        beanFactory.addBeanPostProcessor((MergedBeanDefinitionPostProcessor) (beanDefinition, type, beanName) -> {
            if (ClassUtils.isAssignable(beanType, type)) {
                registerBeanAliases(beanName, aliases);
            }
        });
    }

    protected boolean supports(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName, String module,
                               BeanDefinitionRegistry registry) {
        return true;
    }

    protected abstract void register(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName, String module,
                                     BeanDefinitionRegistry registry);
}
