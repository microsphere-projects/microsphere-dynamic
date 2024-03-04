package io.microsphere.dynamic.jdbc.spring.boot.context;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.getMultipleContextExposedBeanClasses;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.getMultipleContextPrimaryBeanClasses;
import static io.microsphere.spring.util.BeanDefinitionUtils.findInfrastructureBeanNames;
import static io.microsphere.spring.util.BeanRegistrar.registerBean;
import static io.microsphere.spring.util.BeanRegistrar.registerFactoryBean;
import static io.microsphere.spring.util.SpringFactoriesLoaderUtils.loadFactories;

/**
 * {@link ApplicationListener} for Dynamic JDBC Child Context
 *
 * <ul>
 * <li>On {@link ContextRefreshedEvent} : To register The Dynamic JDBC Beans from Child Context to
 * Parent Context</li>
 * </ul>
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
class DynamicJdbcChildContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {

    private final DynamicJdbcConfig dynamicJdbcConfig;

    private final ConfigurableApplicationContext parentContext;

    private final BeanDefinitionRegistry parentBeanDefinitionRegistry;

    private final Set<String> infrastructureBeanNames;

    private final List<ParentContextBeanNameGenerator> parentContextBeanNameGenerators;

    private final boolean registerParentBeans;

    private final Set<Class<?>> multipleContextExposedBeanClasses;

    private final Set<Class<?>> multipleContextPrimaryBeanClasses;

    public DynamicJdbcChildContextRefreshedListener(DynamicJdbcConfig dynamicJdbcConfig, ConfigurableApplicationContext parentContext,
                                                    ConfigurableListableBeanFactory childContextBeanFactory, boolean registerParentBeans) {
        this.dynamicJdbcConfig = dynamicJdbcConfig;
        this.parentContext = parentContext;
        this.parentBeanDefinitionRegistry = (BeanDefinitionRegistry) parentContext.getBeanFactory();
        this.infrastructureBeanNames = findInfrastructureBeanNames(childContextBeanFactory);
        this.parentContextBeanNameGenerators = loadFactories(parentContext, ParentContextBeanNameGenerator.class);
        this.registerParentBeans = registerParentBeans;
        this.multipleContextExposedBeanClasses = getMultipleContextExposedBeanClasses(parentContext);
        this.multipleContextPrimaryBeanClasses = getMultipleContextPrimaryBeanClasses(parentContext);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ConfigurableApplicationContext childContext = (ConfigurableApplicationContext) event.getApplicationContext();
        registerParentContextClosedEventListener(childContext);
        registerParentBeansFromChildContext(childContext);
    }

    private void registerParentContextClosedEventListener(ConfigurableApplicationContext childContext) {
        parentContext.addApplicationListener((ApplicationListener<ContextClosedEvent>) e -> {
            childContext.close();
        });
    }

    private void registerParentBeansFromChildContext(ConfigurableApplicationContext childContext) {
        if (registerParentBeans) {
            ConfigurableListableBeanFactory childContextBeanFactory = childContext.getBeanFactory();
            DynamicJdbcConfig dynamicJdbcConfig = childContext.getBean(DynamicJdbcConfig.class);
            String[] childBeanDefinitionNames = childContextBeanFactory.getBeanDefinitionNames();
            for (String childBeanDefinitionName : childBeanDefinitionNames) {
                if (isInfrastructureBean(childBeanDefinitionName)) {
                    // It's not required to register any Infrastructure Bean
                    continue;
                }
                Object childBean = childContextBeanFactory.getBean(childBeanDefinitionName);
                String parentBeanName = generateParentBeanName(childBeanDefinitionName, childBean, dynamicJdbcConfig, childContext);
                registerParentBean(parentBeanName, childBean);
            }
        }
    }

    protected boolean isInfrastructureBean(String beanName) {
        return infrastructureBeanNames.contains(beanName);
    }

    private String generateParentBeanName(String childBeanName, Object childBean, DynamicJdbcConfig dynamicJdbcConfig,
                                          ConfigurableApplicationContext childContext) {
        String parentBeanName = null;
        ParentContextBeanNameGenerator generator = findParentContextBeanNameGenerator(childBean);
        if (generator != null) {
            parentBeanName = generator.generate(childBeanName, childBean, dynamicJdbcConfig, childContext);
        }
        return StringUtils.hasText(parentBeanName) ? parentBeanName : generateDefaultParentBeanName(childBeanName, childContext);
    }

    private String generateDefaultParentBeanName(String childBeanName, ConfigurableApplicationContext childContext) {
        String childContextId = childContext.getId();
        return childContextId + "$" + childBeanName;
    }

    private ParentContextBeanNameGenerator findParentContextBeanNameGenerator(Object childBean) {
        Class<?> childBeanType = childBean.getClass();
        return parentContextBeanNameGenerators.stream().filter(g -> g.getChildBeanType().isAssignableFrom(childBeanType)).findFirst().orElse(null);
    }

    private void registerParentBean(String parentBeanName, Object childBean) {
        if (isExposedBeanClass(childBean)) {
            boolean primaryBean = isPrimaryBean(childBean);
            registerBean(parentBeanDefinitionRegistry, parentBeanName, childBean, primaryBean);
        } else {
            registerFactoryBean(parentBeanDefinitionRegistry, parentBeanName, childBean);
        }
    }

    private boolean isExposedBeanClass(Object childBean) {
        boolean exposed = false;
        for (Class<?> multipleContextExposedBeanClass : multipleContextExposedBeanClasses) {
            if (multipleContextExposedBeanClass.isInstance(childBean)) {
                exposed = true;
                break;
            }
        }
        return exposed;
    }

    private boolean isPrimaryBeanClass(Object childBean) {
        boolean isPrimary = false;
        for (Class<?> multipleContextPrimaryBeanClass : multipleContextPrimaryBeanClasses) {
            if (multipleContextPrimaryBeanClass.isInstance(childBean)) {
                isPrimary = true;
                break;
            }
        }
        return isPrimary;
    }

    private boolean isPrimaryBean(Object childBean) {
        if (dynamicJdbcConfig.isPrimary() && isPrimaryBeanClass(childBean)) {
            return true;
        }
        return false;
    }

}
