package io.microsphere.dynamic.jdbc.spring.boot.shardingsphere.context;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.dynamic.jdbc.spring.boot.context.AbstractConfigurationConfigBeanDefinitionRegistrar;
import io.microsphere.dynamic.jdbc.spring.boot.context.ConfigBeanDefinitionRegistrar;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.mode.ModeConfiguration;
import org.apache.shardingsphere.infra.yaml.config.pojo.YamlRootConfiguration;
import org.apache.shardingsphere.infra.yaml.config.pojo.YamlRuleConfiguration;
import org.apache.shardingsphere.infra.yaml.config.pojo.mode.YamlModeConfiguration;
import org.apache.shardingsphere.infra.yaml.config.swapper.YamlRuleConfigurationSwapperEngine;
import org.apache.shardingsphere.infra.yaml.config.swapper.mode.ModeConfigurationYamlSwapper;
import org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcUtils.loadShardingSphereYamlRootConfiguration;
import static io.microsphere.spring.util.BeanRegistrar.registerFactoryBean;
import static org.springframework.util.ClassUtils.getShortName;

/**
 * {@link ConfigBeanDefinitionRegistrar} for ShardingSphere
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see ShardingSphereAutoConfiguration
 * @since 1.0.0
 */
public class ShardingSphereConfigurationConfigBeanDefinitionRegistrar
        extends AbstractConfigurationConfigBeanDefinitionRegistrar<DynamicJdbcConfig.ShardingSphere> implements ResourceLoaderAware {
    private static final ModeConfigurationYamlSwapper modeSwapper = new ModeConfigurationYamlSwapper();

    private static final YamlRuleConfigurationSwapperEngine swapperEngine = new YamlRuleConfigurationSwapperEngine();

    private ResourceLoader resourceLoader;

    @Override
    protected void register(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName, String module,
                            DynamicJdbcConfig.ShardingSphere configuration, BeanDefinitionRegistry registry) {
        String configResource = configuration.getConfigResource();
        String beanNamePrefix = getBeanNamePrefix(dynamicJdbcConfig, dynamicJdbcConfigPropertyName, configuration);
        YamlRootConfiguration yamlRootConfiguration = loadShardingSphereYamlRootConfiguration(resourceLoader, configResource);

        registerModeConfigurationBeanDefinition(yamlRootConfiguration, configuration, configResource, beanNamePrefix, registry);
        registerRuleConfigurationBeanDefinitions(yamlRootConfiguration, configuration, configResource, beanNamePrefix, registry);
    }

    private String getBeanNamePrefix(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName,
                                     DynamicJdbcConfig.ShardingSphere shardingSphere) {
        return shardingSphere.getName() + ".";
    }


    private void registerModeConfigurationBeanDefinition(YamlRootConfiguration yamlRootConfiguration,
                                                         DynamicJdbcConfig.ShardingSphere shardingSphere, String configResource, String beanNamePrefix, BeanDefinitionRegistry registry) {
        YamlModeConfiguration mode = yamlRootConfiguration.getMode();
        if (mode == null) {
            logger.info("No YamlModeConfiguration was configured from ShardingSphere[name : {}] Yaml Config Resource[location : {}]",
                    shardingSphere.getName(), configResource);
            return;
        }
        ModeConfiguration modeConfiguration = modeSwapper.swapToObject(mode);
        String beanName = beanNamePrefix + getShortName(modeConfiguration.getType()) + "." + mode.getType();

        registerFactoryBean(registry, beanName, modeConfiguration);
    }

    private void registerRuleConfigurationBeanDefinitions(YamlRootConfiguration yamlRootConfiguration,
                                                          DynamicJdbcConfig.ShardingSphere shardingSphere, String configResource, String beanNamePrefix, BeanDefinitionRegistry registry) {
        Collection<YamlRuleConfiguration> yamlRuleConfigurations = yamlRootConfiguration.getRules();
        if (CollectionUtils.isEmpty(yamlRuleConfigurations)) {
            logger.info("No YamlRuleConfiguration was configured from ShardingSphere[name : {}] Yaml Config Resource[location : {}]",
                    shardingSphere.getName(), configResource);
            return;
        }
        yamlRuleConfigurations
                .forEach(yamlRuleConfiguration -> registerRuleConfigurationBeanDefinition(yamlRuleConfiguration, beanNamePrefix, registry));
    }

    private void registerRuleConfigurationBeanDefinition(YamlRuleConfiguration yamlRuleConfiguration, String beanNamePrefix,
                                                         BeanDefinitionRegistry registry) {
        RuleConfiguration ruleConfiguration = swapperEngine.swapToRuleConfiguration(yamlRuleConfiguration);
        String beanName = beanNamePrefix + getShortName(ruleConfiguration.getClass());
        registerFactoryBean(registry, beanName, ruleConfiguration);
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
