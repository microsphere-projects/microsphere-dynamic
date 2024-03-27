package io.microsphere.dynamic.jdbc.spring.boot.util;

import io.microsphere.dynamic.jdbc.spring.boot.env.ConfigurationPropertiesFlatter;
import org.apache.shardingsphere.infra.yaml.config.pojo.YamlRootConfiguration;
import org.apache.shardingsphere.infra.yaml.engine.YamlEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.springframework.util.StringUtils.delimitedListToStringArray;
import static org.springframework.util.StringUtils.hasText;

/**
 * The utilities class for Dynamic Jdbc
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class DynamicJdbcUtils {

    private static final Logger logger = LoggerFactory.getLogger(DynamicJdbcUtils.class);

    private DynamicJdbcUtils() {
    }

    public static YamlRootConfiguration loadShardingSphereYamlRootConfiguration(ResourceLoader resourceLoader, String configResource) {
        YamlRootConfiguration yamlRootConfiguration = null;
        Resource resource = resourceLoader.getResource(configResource);
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] yamlContent = StreamUtils.copyToByteArray(inputStream);
            yamlRootConfiguration = YamlEngine.unmarshal(yamlContent, YamlRootConfiguration.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("ShardingSpheres' YAML Config Resource[location : %s] can't read", configResource), e);
        }
        return yamlRootConfiguration;
    }

    public static Map<String, String> flatProperties(Map<String, Object> nestedProperties) {
        ConfigurationPropertiesFlatter configurationPropertiesFlatter = ConfigurationPropertiesFlatter.getInstance();
        return configurationPropertiesFlatter.flat(nestedProperties);
    }

    public static List<Map<String, String>> flatPropertiesList(List<Map<String, Object>> nestedPropertiesList) {
        int size = nestedPropertiesList == null ? 0 : nestedPropertiesList.size();

        if (size < 1) {
            return emptyList();
        }

        List<Map<String, String>> flattenPropertiesList = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            flattenPropertiesList.add(flatProperties(nestedPropertiesList.get(i)));
        }

        return unmodifiableList(flattenPropertiesList);
    }

    public static Map<String, List<Map<String, String>>> flatPropertiesMap(Map<String, List<Map<String, Object>>> nestedPropertiesMap) {
        int size = nestedPropertiesMap == null ? 0 : nestedPropertiesMap.size();

        if (size < 1) {
            return emptyMap();
        }

        Map<String, List<Map<String, String>>> flattenPropertiesMap = new LinkedHashMap<>(size);

        nestedPropertiesMap.forEach((name, nestedPropertiesList) -> {
            flattenPropertiesMap.put(name, flatPropertiesList(nestedPropertiesList));
        });

        return unmodifiableMap(flattenPropertiesMap);
    }

    /**
     * Resolve the host of database from the specified JDBC URL
     *
     * @param jdbcURL JDBC URL
     * @return the host of database if resolved, or <code>null</code>
     */
    public static String resolveDatabaseHost(String jdbcURL) {
        if (!hasText(jdbcURL)) {
            return null;
        }
        String address = substringBetween(jdbcURL, "//", "/");
        String[] hostAndPort = delimitedListToStringArray(address, ":");
        if (hostAndPort.length < 1) {
            return null;
        }
        String host = hostAndPort[0];
        return host;
    }

    /**
     * Resolve DataBase Cluster ID from JDBC URL when
     * jdbc:mysql://bnb-asset1.cluster-ct1wbptr1zqn.ap-northeast-1.rds.amazonaws.com/asset?useUnicode=true&characterEncoding=utf-8&useSSL=false
     * <p>
     * The default property names of username and password:
     * <p>
     * {service-name}.{db-cluster-id}.username
     * <p>
     * {service-name}.{db-cluster-id}.password
     *
     * @param databaseHost the host of database
     * @return Database Cluster ID if resolved, or <code>null</code>
     */
    public static String resolveDBClusterId(String databaseHost) {
        if (!hasText(databaseHost)) {
            return null;
        }
        String[] parts = delimitedListToStringArray(databaseHost, ".");
        if (parts.length > 1) {
            return parts[0];
        }
        return null;
    }

}
