package io.microsphere.dynamic.jdbc.spring.boot.shardingsphere.env;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.dynamic.jdbc.spring.boot.datasource.constants.DataSourceConstants;
import io.microsphere.dynamic.jdbc.spring.boot.env.AbstractConfigurationConfigConfigurationPropertiesSynthesizer;
import io.microsphere.dynamic.jdbc.spring.boot.env.ConfigConfigurationPropertiesSynthesizer;
import org.apache.shardingsphere.infra.yaml.config.pojo.YamlRootConfiguration;
import org.apache.shardingsphere.spring.boot.prop.SpringBootPropertiesConfiguration;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static io.microsphere.dynamic.jdbc.spring.boot.shardingsphere.constants.ShardingSphereConstants.SHARDING_SPHERE_DATA_SOURCE_NAMES_PROPERTY_NAME;
import static io.microsphere.dynamic.jdbc.spring.boot.shardingsphere.constants.ShardingSphereConstants.SHARDING_SPHERE_DATA_SOURCE_PROPERTY_NAME_PREFIX;
import static io.microsphere.dynamic.jdbc.spring.boot.shardingsphere.constants.ShardingSphereConstants.SHARDING_SPHERE_PROPERTIES_PROPERTY_NAME_PREFIX;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.getDataSourceType;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.getModuleDefaultProperties;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.joinPropertyName;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcUtils.loadShardingSphereYamlRootConfiguration;

/**
 * {@link ConfigConfigurationPropertiesSynthesizer} for ShardingSphere
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 * @see SpringBootPropertiesConfiguration
 */
public class ShardingSphereConfigConfigurationPropertiesSynthesizer
        extends AbstractConfigurationConfigConfigurationPropertiesSynthesizer<DynamicJdbcConfig.ShardingSphere> implements ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Override
    protected String getConfigurationPropertiesClassName() {
        return "org.apache.shardingsphere.spring.boot.prop.SpringBootPropertiesConfiguration";
    }

    @Override
    protected void synthesize(DynamicJdbcConfig dynamicJdbcConfig, String module, DynamicJdbcConfig.ShardingSphere configuration,
                              Class<?> configurationPropertiesClass, Map<String, Object> properties) {
        Map<String, Map<String, String>> dataSourceMap = dynamicJdbcConfig.getDataSourcePropertiesMap();
        Set<String> dataSourceNames = dataSourceMap.keySet();
        synthesizeShardingSphereDataSourceNamesProperty(dataSourceNames, properties);
        synthesizeShardingSphereDataSourceProperties(dataSourceMap, properties);
        // ShardingSphere Config File(YAML)
        synthesizeShardingSphereProperties(configuration.getConfigResource(), configuration, properties);
    }

    private void synthesizeShardingSphereDataSourceNamesProperty(Set<String> dataSourceNames, Map<String, Object> properties) {
        String propertyName = SHARDING_SPHERE_DATA_SOURCE_NAMES_PROPERTY_NAME;
        String propertyValue = StringUtils.collectionToCommaDelimitedString(dataSourceNames);
        properties.put(propertyName, propertyValue);
    }

    private void synthesizeShardingSphereDataSourceProperties(Map<String, Map<String, String>> dataSourceMap, Map<String, Object> properties) {
        dataSourceMap.forEach((name, dataSource) -> synthesizeShardingSphereDataSourceProperties(name, dataSource, properties));
    }

    private void synthesizeShardingSphereProperties(String configResource, DynamicJdbcConfig.ShardingSphere shardingSphere,
            Map<String, Object> properties) {
        YamlRootConfiguration yamlRootConfiguration = loadShardingSphereYamlRootConfiguration(resourceLoader, configResource);
        Properties shardingSphereProperties = yamlRootConfiguration.getProps();
        if (CollectionUtils.isEmpty(shardingSphereProperties)) {
            logger.info("No Property was configured from ShardingSphere[name : {}] Yaml Config Resource[location: {}]", shardingSphere.getName(),
                    configResource);
            return;
        }
        shardingSphereProperties.forEach((name, value) -> synthesizeShardingSphereDataSourceProperty(name, value, properties));
    }

    private void synthesizeShardingSphereDataSourceProperties(String name, Map<String, String> dataSource, Map<String, Object> properties) {
        String prefix = joinPropertyName(SHARDING_SPHERE_DATA_SOURCE_PROPERTY_NAME_PREFIX, name);
        dataSource.forEach((propertyName, propertyValue) -> {
            String fullPropertyName = joinPropertyName(prefix, propertyName);
            properties.put(fullPropertyName, propertyValue);
        });
        synthesizeShardingSphereDataSourceDefaultProperties(name, dataSource, properties);
    }

    private void synthesizeShardingSphereDataSourceDefaultProperties(String name, Map<String, String> dataSource, Map<String, Object> properties) {
        String type = getDataSourceType(dataSource);
        if (DataSourceConstants.HIKARI_DATASOURCE_CLASS_NAME.equals(type)) {
            Map<String, Object> dataSourceDefaultProperties = getModuleDefaultProperties(environment, getModule(), "datasource");
            if (!CollectionUtils.isEmpty(dataSourceDefaultProperties)) {
                String prefix = joinPropertyName(SHARDING_SPHERE_DATA_SOURCE_PROPERTY_NAME_PREFIX, name);
                dataSourceDefaultProperties.forEach((propertyName, propertyValue) -> {
                    String fullPropertyName = joinPropertyName(prefix, propertyName);
                    properties.putIfAbsent(fullPropertyName, propertyValue);
                });
                properties.putAll(dataSourceDefaultProperties);
            }
        }
    }

    private void synthesizeShardingSphereDataSourceProperty(Object propertyNameSuffix, Object propertyValue, Map<String, Object> properties) {
        String propertyName = joinPropertyName(SHARDING_SPHERE_PROPERTIES_PROPERTY_NAME_PREFIX, propertyNameSuffix);
        properties.put(propertyName, propertyValue);
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
