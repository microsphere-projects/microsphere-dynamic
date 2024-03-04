package io.microsphere.dynamic.jdbc.spring.boot.mybatis.env;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.dynamic.jdbc.spring.boot.env.AbstractConfigurationConfigConfigurationPropertiesSynthesizer;
import io.microsphere.dynamic.jdbc.spring.boot.env.ConfigConfigurationPropertiesSynthesizer;
import io.microsphere.dynamic.jdbc.spring.boot.mybatis.constants.MybatisConstants;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

import static io.microsphere.spring.util.EnvironmentUtils.resolveCommaDelimitedValueToList;

/**
 * {@link ConfigConfigurationPropertiesSynthesizer} for Mybatis
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 * @see MybatisProperties
 */
public class MybatisConfigConfigurationPropertiesSynthesizer
        extends AbstractConfigurationConfigConfigurationPropertiesSynthesizer<DynamicJdbcConfig.Mybatis> {

    public static void synthesizeBasePackagesConfigurationProperties(String basePackagesValue, Map<String, Object> properties, String propertyName,
            Environment environment) {
        List<String> basePackages = resolveCommaDelimitedValueToList(environment, basePackagesValue);
        String propertyValue = StringUtils.collectionToCommaDelimitedString(basePackages);
        properties.put(propertyName, propertyValue);
    }

    @Override
    protected String getConfigurationPropertiesClassName() {
        return "org.mybatis.spring.boot.autoconfigure.MybatisProperties";
    }

    @Override
    protected void synthesize(DynamicJdbcConfig dynamicJdbcConfig, String module, DynamicJdbcConfig.Mybatis configuration,
                              Class<?> configurationPropertiesClass, Map<String, Object> properties) {
        synthesizeMybatisBasePackagesConfigurationProperties(configuration, properties);
    }

    private void synthesizeMybatisBasePackagesConfigurationProperties(DynamicJdbcConfig.Mybatis mybatisPlus, Map<String, Object> properties) {
        String propertyName = MybatisConstants.DYNAMIC_JDBC_MYBATIS_CLASS_PREFIXES_PROPERTY_NAME;
        synthesizeBasePackagesConfigurationProperties(mybatisPlus.getBasePackages(), properties, propertyName, environment);
    }


}
