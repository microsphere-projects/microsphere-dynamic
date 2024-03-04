package io.microsphere.dynamic.jdbc.spring.boot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.dynamic.jdbc.spring.boot.config.annotation.Module;
import io.microsphere.dynamic.jdbc.spring.boot.datasource.constants.DataSourceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.DATASOURCE_MODULE;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.DYNAMIC_JDBC_CHILD_CONTEXT_ID_PREFIX;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.DYNAMIC_JDBC_CHILD_CONTEXT_ID_SUFFIX;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.DYNAMIC_JDBC_CONFIGS_PROPERTY_NAME_PREFIX;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.DYNAMIC_JDBC_CONFIG_BEAN_NAME_PREFIX;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.DYNAMIC_JDBC_CONFIG_BEAN_NAME_SUFFIX;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.DYNAMIC_PREFIX;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.DYNAMIC_SUFFIX;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.SYNTHESIZED_PROPERTY_SOURCE_NAME_PREFIX;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.SYNTHESIZED_PROPERTY_SOURCE_NAME_SUFFIX;
import static io.microsphere.spring.util.PropertySourcesUtils.findPropertyNamesByPrefix;
import static io.microsphere.spring.util.PropertySourcesUtils.normalizePrefix;
import static io.microsphere.text.FormatUtils.format;
import static io.microsphere.util.StringUtils.substringAfter;
import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;
import static org.apache.commons.lang3.StringUtils.substringBetween;

/**
 * The utilities class for {@link DynamicJdbcConfig}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class DynamicJdbcConfigUtils {

    private static final Logger logger = LoggerFactory.getLogger(DynamicJdbcConfigUtils.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final ResourceLoader resourceLoader = new DefaultResourceLoader();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private DynamicJdbcConfigUtils() {
    }

    public static Map<String, DynamicJdbcConfig> getDynamicJdbcConfigs(ConfigurableEnvironment environment) {
        String prefix = DYNAMIC_JDBC_CONFIGS_PROPERTY_NAME_PREFIX;

        Set<String> propertyNames = findPropertyNamesByPrefix(environment, prefix);

        int size = propertyNames.size();

        if (size < 1) {
            logger.info("No DynamicJdbcConfig configuration property[prefix : '{}'] from Environment", prefix);
            return emptyMap();
        }

        logger.debug("{} DynamicJdbcConfig configuration properties[prefix : '{}'] were found", size, prefix);

        Map<String, DynamicJdbcConfig> dynamicJdbcConfigs = new HashMap<>(size);

        for (String propertyName : propertyNames) {
            if (StringUtils.isEmpty(environment.getProperty(propertyName))) {
                logger.warn("The json content of DynamicJdbcConfig is empty , property name : '{}'", propertyName);
                continue;
            }
            DynamicJdbcConfig dynamicJdbcConfig = getDynamicJdbcConfig(environment, propertyName);
            dynamicJdbcConfigs.put(propertyName, dynamicJdbcConfig);
            logger.debug("The json content of DynamicJdbcConfig has been parsed , property name : '{}'", propertyName);
        }

        return unmodifiableMap(dynamicJdbcConfigs);
    }

    public static DynamicJdbcConfig getDynamicJdbcConfig(ConfigurableEnvironment environment, String propertyName) {
        return getDynamicJdbcConfig(environment, propertyName, () -> getDynamicJdbcConfigPropertyNameSuffix(propertyName));
    }

    public static String getDynamicJdbcConfigPropertyNameSuffix(String propertyName) {
        return substringAfter(propertyName, normalizePrefix(DYNAMIC_JDBC_CONFIGS_PROPERTY_NAME_PREFIX));
    }

    public static DynamicJdbcConfig getDynamicJdbcConfig(ConfigurableEnvironment environment, String propertyName,
                                                         Supplier<String> defaultNameSupplier) {
        String dynamicJdbcConfigJsonContent = getDynamicJdbcConfigContent(environment, propertyName);
        DynamicJdbcConfig dynamicJdbcConfig = parseDynamicJdbcConfig(propertyName, dynamicJdbcConfigJsonContent);
        //如果没有开启动态数据源，强制开启
        if (!dynamicJdbcConfig.isDynamic()) {
            logger.warn("Force dynamic true. propertyName: {}", propertyName);
            dynamicJdbcConfig.setDynamic(Boolean.TRUE);
        }
        // Set the default name
        FunctionUtils.setIfAbsent(dynamicJdbcConfig::getName, defaultNameSupplier, dynamicJdbcConfig::setName);
        return dynamicJdbcConfig;
    }

    public static String getDynamicJdbcConfigContent(Environment environment, String propertyName) {
        String dynamicJdbcConfigValue = environment.getProperty(propertyName);
        return getDynamicJdbcConfigContent(dynamicJdbcConfigValue);
    }

    private static String getDynamicJdbcConfigContent(String dynamicJdbcConfigPropertyValue) {
        if (dynamicJdbcConfigPropertyValue == null) {
            return null;
        }
        if (dynamicJdbcConfigPropertyValue.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)) {
            return readResourceContent(dynamicJdbcConfigPropertyValue);
        }
        return dynamicJdbcConfigPropertyValue;
    }

    public static String readResourceContent(String resourceLocation) {
        Resource resource = resourceLoader.getResource(resourceLocation);
        String content = null;
        try (InputStream inputStream = resource.getInputStream()) {
            content = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException(format("The resource location['{}'] of DynamicJdbcConfig can't be read", resourceLocation), e);
        }
        return content;
    }

    public static DynamicJdbcConfig parseDynamicJdbcConfig(String propertyName, String dynamicJdbcConfigJsonContent) throws IllegalArgumentException {
        DynamicJdbcConfig dynamicJdbcConfig = null;
        try {
            dynamicJdbcConfig = objectMapper.readValue(dynamicJdbcConfigJsonContent, DynamicJdbcConfig.class);
        } catch (IOException e) {
            String message = "The json content of DynamicJdbcConfig is not well-formed, please check it. The property name is " + propertyName + ". ";
            if (e instanceof JsonProcessingException) {
                message += "The error is " + ((JsonProcessingException) e).getOriginalMessage();
            }
            throw new IllegalArgumentException(message);
        }
        return dynamicJdbcConfig;
    }

    public static DynamicJdbcConfig cloneDynamicJdbcConfig(DynamicJdbcConfig source) {
        String dynamicJdbcConfigJsonContent = null;
        try {
            dynamicJdbcConfigJsonContent = objectMapper.writeValueAsString(source);
        } catch (JsonProcessingException e) {
            String message = format("The instance of DynamicJdbcConfig[name : {}] can't be serialized as a JSON content", source.getName());
            throw new IllegalArgumentException(message, e);
        }
        return parseDynamicJdbcConfig(null, dynamicJdbcConfigJsonContent);
    }

    public static String getModule(DynamicJdbcConfig.Config configuration) {
        return getModule(configuration.getClass());
    }

    public static String getModule(Class<? extends DynamicJdbcConfig.Config> configurationClass) {
        Module module = configurationClass.getAnnotation(Module.class);
        if (module == null) {
            throw new IllegalArgumentException(format("{} must annotate @{}", configurationClass.getName(), Module.class.getName()));
        }
        return module.value();
    }

    public static String generateDynamicJdbcConfigBeanName(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName) {
        String propertyName = dynamicJdbcConfigPropertyName;
        String propertyNameSuffix = getDynamicJdbcConfigPropertyNameSuffix(propertyName);

        String dynamicJdbcConfigName = dynamicJdbcConfig.getName();

        StringBuilder beanNameBuilder =
                new StringBuilder(DYNAMIC_JDBC_CONFIG_BEAN_NAME_PREFIX).append(propertyNameSuffix).append(DYNAMIC_JDBC_CONFIG_BEAN_NAME_SUFFIX);

        if (!Objects.equals(propertyNameSuffix, dynamicJdbcConfigName)) {
            beanNameBuilder.append(".").append(dynamicJdbcConfigName);
        }

        return beanNameBuilder.toString();
    }

    public static String generateSynthesizedPropertySourceName(String dynamicJdbcConfigPropertyName) {
        return SYNTHESIZED_PROPERTY_SOURCE_NAME_PREFIX + dynamicJdbcConfigPropertyName + SYNTHESIZED_PROPERTY_SOURCE_NAME_SUFFIX;
    }

    public static boolean isDynamicJdbcConfig(DynamicJdbcConfig dynamicJdbcConfig) {
        String name = dynamicJdbcConfig.getName();
        return name.startsWith(DYNAMIC_PREFIX);
    }

    public static String generateDynamicDataSourceDynamicJdbcConfigName(DynamicJdbcConfig dynamicJdbcConfig) {
        return generateDynamicModuleDynamicJdbcConfigName(dynamicJdbcConfig, DATASOURCE_MODULE);
    }

    public static String generateDynamicModuleDynamicJdbcConfigName(DynamicJdbcConfig dynamicJdbcConfig, String module) {
        return DYNAMIC_PREFIX + dynamicJdbcConfig.getName() + DYNAMIC_SUFFIX + module;
    }

    public static String generateDynamicDataSourceDynamicJdbcChildContextId(DynamicJdbcConfig dynamicJdbcConfig) {
        return generateDynamicDynamicJdbcChildContextId(dynamicJdbcConfig, DATASOURCE_MODULE);
    }

    public static String generateDynamicDynamicJdbcChildContextId(DynamicJdbcConfig dynamicJdbcConfig, String module) {
        return DYNAMIC_PREFIX + generateDynamicJdbcChildContextId(dynamicJdbcConfig) + DYNAMIC_SUFFIX + module;
    }

    public static String generateDynamicJdbcChildContextId(DynamicJdbcConfig dynamicJdbcConfig) {
        Assert.notNull(dynamicJdbcConfig, "DynamicJdbcConfig argument must not null");
        String dynamicJdbcConfigName = dynamicJdbcConfig.getName();
        return generateDynamicJdbcChildContextId(dynamicJdbcConfigName);
    }

    public static String generateDynamicJdbcChildContextId(String dynamicJdbcConfigName) {
        return DYNAMIC_JDBC_CHILD_CONTEXT_ID_PREFIX + dynamicJdbcConfigName + DYNAMIC_JDBC_CHILD_CONTEXT_ID_SUFFIX;
    }

    public static String resolveDynamicJdbcConfigName(String dynamicJdbcChildContextId) {
        return substringBetween(dynamicJdbcChildContextId, DYNAMIC_JDBC_CHILD_CONTEXT_ID_PREFIX, DYNAMIC_JDBC_CHILD_CONTEXT_ID_SUFFIX);
    }

    public static String getDataSourceName(Map<String, String> dataSourceProperties) {
        return dataSourceProperties.get(DataSourceConstants.NAME_PROPERTY_NAME);
    }

    public static String getDataSourceType(Map<String, String> dataSourceProperties) {
        return dataSourceProperties.get(DataSourceConstants.TYPE_PROPERTY_NAME);
    }

    public static String getDriverClassName(Map<String, String> dataSourceProperties) {
        return dataSourceProperties.get(DataSourceConstants.DRIVER_CLASS_NAME_PROPERTY_NAME);
    }

    public static String getDataSourceUrl(Map<String, String> dataSourceProperties) {
        Map.Entry<String, String> entry = getDataSourceUrlEntry(dataSourceProperties);
        return entry == null ? null : entry.getValue();
    }

    public static Map.Entry<String, String> getDataSourceUrlEntry(Map<String, String> dataSourceProperties) {
        String propertyName = null;
        String dataSourceUrl = null;
        for (String name : DataSourceConstants.JDBC_URL_PROPERTY_NAMES) {
            dataSourceUrl = dataSourceProperties.get(name);
            if (StringUtils.hasText(dataSourceUrl)) {
                propertyName = name;
                break;
            }
        }
        return entry(propertyName, dataSourceUrl);
    }

    public static String getDataSourceUserName(Map<String, String> dataSourceProperties) {
        return dataSourceProperties.get(DataSourceConstants.USER_NAME_PROPERTY_NAME);
    }

    public static String getDataSourcePassword(Map<String, String> dataSourceProperties) {
        return dataSourceProperties.get(DataSourceConstants.PASSWORD_NAME_PROPERTY_NAME);
    }

    static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new ImmutableEntry(key, value);
    }

    private static class ImmutableEntry<K, V> implements Map.Entry<K, V> {

        private final K key;

        private final V value;

        private ImmutableEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException("Immutable Entry");
        }
    }
}
