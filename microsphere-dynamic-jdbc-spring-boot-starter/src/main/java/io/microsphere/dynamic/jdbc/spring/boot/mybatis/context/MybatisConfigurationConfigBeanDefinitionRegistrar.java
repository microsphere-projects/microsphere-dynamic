package io.microsphere.dynamic.jdbc.spring.boot.mybatis.context;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.dynamic.jdbc.spring.boot.context.AbstractScannedConfigurationConfigBeanDefinitionRegistrar;
import io.microsphere.dynamic.jdbc.spring.boot.context.ConfigBeanDefinitionRegistrar;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;

import static io.microsphere.spring.util.AnnotatedBeanDefinitionRegistryUtils.registerBeans;

/**
 * {@link BeanNameGenerator} for {@link DynamicJdbcConfig.Mybatis}
 * 
 * {@link ConfigBeanDefinitionRegistrar} for Mybatis
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class MybatisConfigurationConfigBeanDefinitionRegistrar
        extends AbstractScannedConfigurationConfigBeanDefinitionRegistrar<DynamicJdbcConfig.Mybatis> {

    @Override
    protected String getBasePackages(DynamicJdbcConfig.Mybatis mybatis) {
        return mybatis.getBasePackages();
    }

    @Override
    protected void register(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName, String module,
                            DynamicJdbcConfig.Mybatis mybatis, String basePackages, BeanDefinitionRegistry registry) {
        registerBeans(registry, MybatisMapperScanConfiguration.class);
    }
}
