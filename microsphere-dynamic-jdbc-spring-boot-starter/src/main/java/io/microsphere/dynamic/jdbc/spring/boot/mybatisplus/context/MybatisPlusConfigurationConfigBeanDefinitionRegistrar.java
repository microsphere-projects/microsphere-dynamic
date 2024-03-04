package io.microsphere.dynamic.jdbc.spring.boot.mybatisplus.context;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.dynamic.jdbc.spring.boot.context.AbstractScannedConfigurationConfigBeanDefinitionRegistrar;
import io.microsphere.dynamic.jdbc.spring.boot.context.ConfigBeanDefinitionRegistrar;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import static io.microsphere.spring.util.AnnotatedBeanDefinitionRegistryUtils.registerBeans;

/**
 * {@link ConfigBeanDefinitionRegistrar} for Mybatis plus
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class MybatisPlusConfigurationConfigBeanDefinitionRegistrar
    extends AbstractScannedConfigurationConfigBeanDefinitionRegistrar<DynamicJdbcConfig.MybatisPlus> {

    @Override
    protected String getBasePackages(DynamicJdbcConfig.MybatisPlus mybatisPlus) {
        return mybatisPlus.getBasePackages();
    }

    @Override
    protected void register(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName, String module,
                            DynamicJdbcConfig.MybatisPlus mybatisPlus, String basePackages, BeanDefinitionRegistry registry) {
        registerBeans(registry, MybatisPlusMapperScanConfiguration.class);
    }
}
