package io.microsphere.dynamic.jdbc.spring.boot.mybatisplus.env;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.dynamic.jdbc.spring.boot.env.AbstractConfigurationConfigConfigurationPropertiesSynthesizer;
import io.microsphere.dynamic.jdbc.spring.boot.env.ConfigConfigurationPropertiesSynthesizer;
import io.microsphere.dynamic.jdbc.spring.boot.mybatisplus.constants.MybatisPlusConstants;

import java.util.Map;

import static io.microsphere.dynamic.jdbc.spring.boot.mybatis.env.MybatisConfigConfigurationPropertiesSynthesizer.synthesizeBasePackagesConfigurationProperties;

/**
 * {@link ConfigConfigurationPropertiesSynthesizer} for Mybatis Plus
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 * @see MybatisPlusProperties
 */
public class MybatisPlusConfigConfigurationPropertiesSynthesizer
        extends AbstractConfigurationConfigConfigurationPropertiesSynthesizer<DynamicJdbcConfig.MybatisPlus> {

    @Override
    protected String getConfigurationPropertiesClassName() {
        return "com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties";
    }

    @Override
    protected void synthesize(DynamicJdbcConfig dynamicJdbcConfig, String module, DynamicJdbcConfig.MybatisPlus configuration,
                              Class<?> configurationPropertiesClass, Map<String, Object> properties) {
        synthesizeMybatisPlusBasePackages(configuration, properties);
    }

    private void synthesizeMybatisPlusBasePackages(DynamicJdbcConfig.MybatisPlus mybatisPlus, Map<String, Object> properties) {
        String propertyName = MybatisPlusConstants.DYNAMIC_JDBC_MYBATIS_PLUS_CLASS_PREFIXES_PROPERTY_NAME;
        synthesizeBasePackagesConfigurationProperties(mybatisPlus.getBasePackages(), properties, propertyName, environment);
    }
}
