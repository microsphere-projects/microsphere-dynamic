package io.microsphere.dynamic.jdbc.spring.boot.util;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.dynamic.jdbc.spring.boot.datasource.constants.DataSourceConstants;
import io.microsphere.spring.util.PropertySourcesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.TreeSet;

import static io.microsphere.dynamic.jdbc.spring.boot.autoconfigure.DynamicJdbcAutoConfigurationRepository.getAutoConfigurationClassNames;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.AUTO_CONFIGURATION_BANNED_MODULES_PROPERTY_NAME;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.AUTO_CONFIGURATION_BASE_PACKAGES_PROPERTY_NAME;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.DEFAULT_DYNAMIC_JDBC_ENABLED_PROPERTY_VALUE;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.DEFAULT_PROPERTIES_PROPERTY_NAME;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.DYNAMIC_JDBC_CONFIGS_PROPERTY_NAME_PREFIX;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.DYNAMIC_JDBC_ENABLED_PROPERTY_NAME;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.DYNAMIC_JDBC_MODULES_PROPERTY_NAME_PREFIX;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.MULTIPLE_CONTEXT_AUTO_CONFIGURATION_EXCLUDED_CLASSES_PROPERTY_NAME;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.MULTIPLE_CONTEXT_EXPOSED_BEAN_CLASSES_PROPERTY_NAME;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.MULTIPLE_CONTEXT_PRIMARY_BEAN_CLASSES_PROPERTY_NAME;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.PROPERTY_NAME_ALIASES_PROPERTY_NAME;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.PROPERTY_NAME_SEPARATOR;
import static io.microsphere.dynamic.jdbc.spring.boot.datasource.constants.DataSourceConstants.DATA_SOURCE_DEFAULT_PASSWORD_PROPERTY_NAME;
import static io.microsphere.dynamic.jdbc.spring.boot.datasource.constants.DataSourceConstants.DATA_SOURCE_DEFAULT_USER_NAME_PROPERTY_NAME;
import static io.microsphere.dynamic.jdbc.spring.boot.datasource.constants.DataSourceConstants.DATA_SOURCE_URL_DEFAULT_SCHEME_PROPERTY_NAME;
import static io.microsphere.dynamic.jdbc.spring.boot.datasource.constants.DataSourceConstants.DATA_SOURCE_URL_DEFAULT_SCHEME_PROPERTY_VALUE;
import static io.microsphere.dynamic.jdbc.spring.boot.datasource.constants.DataSourceConstants.PASSWORD_NAME_PROPERTY_NAME;
import static io.microsphere.dynamic.jdbc.spring.boot.datasource.constants.DataSourceConstants.USER_NAME_PROPERTY_NAME;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.getModule;
import static io.microsphere.spring.util.EnvironmentUtils.getConversionService;
import static io.microsphere.spring.util.EnvironmentUtils.resolveCommaDelimitedValueToList;
import static io.microsphere.spring.util.PropertySourcesUtils.getSubProperties;
import static io.microsphere.util.StringUtils.substringBefore;
import static java.time.Duration.ofSeconds;
import static java.util.Collections.emptySet;
import static java.util.Collections.sort;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;
import static org.springframework.util.StringUtils.collectionToCommaDelimitedString;
import static org.springframework.util.StringUtils.commaDelimitedListToSet;
import static org.springframework.util.StringUtils.hasText;

/**
 * Dynamic JDBC Property Utilities class
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class DynamicJdbcPropertyUtils {

    private static final Logger logger = LoggerFactory.getLogger(DynamicJdbcPropertyUtils.class);

    private DynamicJdbcPropertyUtils() {
    }

    public static boolean isDynamicJdbcEnabled(Environment environment) {
        return environment.getProperty(DYNAMIC_JDBC_ENABLED_PROPERTY_NAME, Boolean.TYPE, DEFAULT_DYNAMIC_JDBC_ENABLED_PROPERTY_VALUE);
    }

    public static Set<String> getAllModulesAutoConfigurationBasePackages(ConfigurableEnvironment environment) {
        Set<String> allClassPrefixes = new TreeSet<>();

        List<String> classPrefixesValues = getAllModulesPropertyValues(environment, AUTO_CONFIGURATION_BASE_PACKAGES_PROPERTY_NAME);

        classPrefixesValues.forEach(classPrefixesValue -> allClassPrefixes.addAll(resolveCommaDelimitedValueToList(environment, classPrefixesValue)));

        return unmodifiableSet(allClassPrefixes);
    }

    public static Set<String> getAllModulesAutoConfigurationClassNames(ConfigurableApplicationContext context) {
        ConfigurableEnvironment environment = context.getEnvironment();

        Set<String> modules = getDynamicJdbcConfigModules(environment);
        Set<String> allClassNames = new LinkedHashSet<>();
        modules.forEach(module -> {
            Set<String> classNames = getModuleAutoConfigurationClassNames(context, module);
            allClassNames.addAll(classNames);
        });

        return unmodifiableSet(allClassNames);
    }

    public static Set<String> getMultipleContextExclusionAutoConfigurationClassNames(ConfigurableEnvironment environment) {
        String key = MULTIPLE_CONTEXT_AUTO_CONFIGURATION_EXCLUDED_CLASSES_PROPERTY_NAME;
        Set<String> classNames = environment.getProperty(key, Set.class, emptySet());
        logger.debug("'{}' : {}", key, classNames);
        return classNames;
    }

    public static Set<Class<?>> getMultipleContextExposedBeanClasses(ConfigurableApplicationContext context) {
        ConfigurableEnvironment environment = context.getEnvironment();

        String key = MULTIPLE_CONTEXT_EXPOSED_BEAN_CLASSES_PROPERTY_NAME;
        Set<String> classNames = environment.getProperty(key, Set.class, emptySet());
        logger.debug("'{}' : {}", key, classNames);

        return resolveClassNames(context, classNames);
    }

    public static Set<Class<?>> getMultipleContextPrimaryBeanClasses(ConfigurableApplicationContext context) {
        ConfigurableEnvironment environment = context.getEnvironment();

        String key = MULTIPLE_CONTEXT_PRIMARY_BEAN_CLASSES_PROPERTY_NAME;
        Set<String> classNames = environment.getProperty(key, Set.class, emptySet());
        logger.debug("'{}' : {}", key, classNames);

        return resolveClassNames(context, classNames);
    }

    private static Set<Class<?>> resolveClassNames(ConfigurableApplicationContext context, Set<String> classNames) {
        if (classNames.isEmpty()) {
            return emptySet();
        }

        ClassLoader classLoader = context.getClassLoader();
        Set<Class<?>> classes = new LinkedHashSet<>(classNames.size());
        for (String className : classNames) {
            Class<?> klass = ClassUtils.resolveClassName(className, classLoader);
            if (klass != null) {
                classes.add(klass);
            }
        }
        return unmodifiableSet(classes);
    }

    public static Set<String> getModuleAutoConfigurationBasePackages(Environment environment, String module) {
        Set<String> classPrefixes = getModuleProperty(environment, module, AUTO_CONFIGURATION_BASE_PACKAGES_PROPERTY_NAME, Set.class);
        return classPrefixes == null ? emptySet() : unmodifiableSet(classPrefixes);
    }

    public static Set<String> getModuleAutoConfigurationBannedModules(Environment environment, String module) {
        Set<String> bannedModules = getModuleProperty(environment, module, AUTO_CONFIGURATION_BANNED_MODULES_PROPERTY_NAME, Set.class);
        return bannedModules == null ? emptySet() : unmodifiableSet(bannedModules);
    }

    public static Set<String> getModuleExclusionAutoConfigurationClassNames(ConfigurableApplicationContext context, String module) {
        ConfigurableEnvironment environment = context.getEnvironment();
        Set<String> exclusionAutoConfigurationClassNames = new LinkedHashSet<>();
        Set<String> bannedModules = getModuleAutoConfigurationBannedModules(environment, module);
        bannedModules.forEach(bannedModule -> {
            Set<String> bannedAutoConfigurationClassNames = getModuleAutoConfigurationClassNames(context, bannedModule);
            exclusionAutoConfigurationClassNames.addAll(bannedAutoConfigurationClassNames);
        });
        return unmodifiableSet(exclusionAutoConfigurationClassNames);
    }

    public static Set<String> getModuleAutoConfigurationClassNames(ConfigurableApplicationContext context, String module) {
        ConfigurableEnvironment environment = context.getEnvironment();
        Set<String> classPrefixes = getModuleAutoConfigurationBasePackages(environment, module);
        Set<String> allClassNames = new LinkedHashSet<>(getAutoConfigurationClassNames(context, classPrefixes));
        ClassLoader classLoader = context.getClassLoader();
        Iterator<String> iterator = allClassNames.iterator();
        while (iterator.hasNext()) {
            String className = iterator.next();
            if (!ClassUtils.isPresent(className, classLoader)) {
                iterator.remove();
                logger.debug("Context[id : {}] Auto-Configuration Class '{}' can't be found", context.getId(), className);
            }
        }

        return unmodifiableSet(allClassNames);
    }

    public static Set<String> getModulePropertyNameAliases(Environment environment, String module, String propertyName) {
        String modulePropertyNameAliasesPropertyName = getModulePropertyNameAliasesPropertyName(module, propertyName);
        return environment.getProperty(modulePropertyNameAliasesPropertyName, Set.class, emptySet());
    }

    public static Map<String, Object> getModuleDefaultProperties(ConfigurableEnvironment environment, String module, String... propertyNameSuffixes) {
        String moduleDefaultPropertiesPropertyNamePrefix = getModuleDefaultPropertiesPropertyNamePrefix(module);
        String prefix = joinPropertyName(moduleDefaultPropertiesPropertyNamePrefix, propertyNameSuffixes);
        return getSubProperties(environment, prefix);
    }

    public static String getModulePropertyNameAliasesPropertyName(String module, String propertyName) {
        return joinPropertyName(getModulePropertyNameAliasesPropertyNamePrefix(module), propertyName);
    }

    public static String getModulePropertyNameAliasesPropertyNamePrefix(String module) {
        return joinPropertyName(getModulePropertyNamePrefix(module), PROPERTY_NAME_ALIASES_PROPERTY_NAME);
    }

    public static Map<String, String> getDataSourceUrlDefaultQueryParams(ConfigurableEnvironment environment) {
        return (Map) PropertySourcesUtils.getSubProperties(environment, DataSourceConstants.DATA_SOURCE_URL_DEFAULT_QUERY_PARAMS_PROPERTY_NAME_PREFIX);
    }

    public static String getDataSourceUrlDefaultScheme(Environment environment) {
        return environment.getProperty(DATA_SOURCE_URL_DEFAULT_SCHEME_PROPERTY_NAME, DATA_SOURCE_URL_DEFAULT_SCHEME_PROPERTY_VALUE);
    }

    public static String getDataSourceDefaultUserName(Environment environment) {
        return environment.getProperty(DATA_SOURCE_DEFAULT_USER_NAME_PROPERTY_NAME);
    }

    public static String getDataSourceDefaultPassword(Environment environment) {
        return environment.getProperty(DATA_SOURCE_DEFAULT_PASSWORD_PROPERTY_NAME);
    }

    public static String getClusterDataSourceUserName(Environment environment, String databaseClusterId) {
        return getClusterDataSourceProperty(environment, databaseClusterId, USER_NAME_PROPERTY_NAME);
    }

    public static String getClusterDataSourcePassword(Environment environment, String databaseClusterId) {
        return getClusterDataSourceProperty(environment, databaseClusterId, PASSWORD_NAME_PROPERTY_NAME);
    }

    public static String getClusterDataSourceProperty(Environment environment, String databaseClusterId, String propertyName) {
        if (!hasText(databaseClusterId)) {
            return null;
        }
        String serviceName = environment.getProperty("spring.application.name");
        if (!hasText(serviceName)) {
            return null;
        }
        String fullPropertyName = serviceName + "." + databaseClusterId + "." + propertyName;
        return environment.getProperty(fullPropertyName);
    }

    public static String getModuleProperty(ConfigurableEnvironment environment, DynamicJdbcConfig.Config configuration, String propertyNameSuffix) {
        return getModuleProperty(environment, getModule(configuration), propertyNameSuffix);
    }

    public static String getModuleProperty(Environment environment, String module, String propertyNameSuffix) {
        return getModuleProperty(environment, module, propertyNameSuffix, String.class);
    }

    public static <T> T getModuleProperty(Environment environment, String module, String propertyNameSuffix, Class<T> propertyValueType) {
        return getModuleProperty(environment, module, propertyNameSuffix, propertyValueType, null);
    }

    public static <T> T getModuleProperty(Environment environment, String module, String propertyNameSuffix, Class<T> propertyValueType,
                                          T defaultValue) {
        String modulePropertyName = getModulePropertyName(module, propertyNameSuffix);
        return environment.getProperty(modulePropertyName, propertyValueType, defaultValue);
    }

    public static String getModulePropertyName(String module, String modulePropertyNameSuffix) {
        return joinPropertyName(getModulePropertyNamePrefix(module), modulePropertyNameSuffix);
    }

    public static Map<String, Object> getModuleProperties(ConfigurableEnvironment environment, DynamicJdbcConfig.Config configuration) {
        return getModuleProperties(environment, getModule(configuration));
    }

    public static Map<String, Object> getModuleProperties(ConfigurableEnvironment environment, String module) {
        String prefix = joinPropertyName(DYNAMIC_JDBC_MODULES_PROPERTY_NAME_PREFIX, module);
        return getSubProperties(environment, prefix);
    }

    public static List<String> getAllModulesPropertyValues(ConfigurableEnvironment environment, String propertyName) {

        Set<String> modules = getDynamicJdbcConfigModules(environment);
        List<String> propertyValues = new ArrayList<>(modules.size());

        for (String module : modules) {
            Map<String, Object> moduleProperties = getModuleProperties(environment, module);
            Object propertyValue = moduleProperties.get(propertyName);
            if (propertyValue instanceof String && !propertyValues.contains(propertyValue)) {
                propertyValues.add(String.valueOf(propertyValue));
            }
        }

        // Sort
        sort(propertyValues);
        // Unmodifiable
        return unmodifiableList(propertyValues);
    }

    public static Set<String> getDynamicJdbcConfigModules(ConfigurableEnvironment environment) {
        return getSubPropertyNames(environment, DYNAMIC_JDBC_MODULES_PROPERTY_NAME_PREFIX);
    }

    public static String getConfigPropertyName(String propertyNameSuffix) {
        return joinPropertyName(DYNAMIC_JDBC_CONFIGS_PROPERTY_NAME_PREFIX, propertyNameSuffix);
    }

    public static String getModulePropertyNamePrefix(String module) {
        return joinPropertyName(DYNAMIC_JDBC_MODULES_PROPERTY_NAME_PREFIX, module);
    }

    public static Duration getDynamicDataSourceChildContextCloseDelay(Environment environment) {
        return environment.getProperty(DataSourceConstants.DYNAMIC_DATA_SOURCE_CHILD_CONTEXT_CLOSE_DELAY_PROPERTY_NAME, Duration.class, ofSeconds(60));
    }

    public static void appendPropertyValue(Map<String, Object> properties, String propertyName, String appendPropertyValue) {
        Object propertyValue = properties.get(propertyName);
        if (propertyValue == null) { // Not exists
            propertyValue = appendPropertyValue;
        } else if (propertyValue instanceof String) { // Append to the tail
            propertyValue = propertyValue + "," + appendPropertyValue;
        }
        properties.put(propertyName, propertyValue);
    }

    public static void appendCommaDelimitedPropertyValues(Map<String, Object> properties, String propertyName, String... propertyValues) {

        int length = propertyValues == null ? 0 : propertyValues.length;

        if (length < 1) {
            return;
        }

        Object previousPropertyValue = properties.get(propertyName);

        Set<String> newPropertyValues = new LinkedHashSet<>();

        if (previousPropertyValue != null) {
            Set<String> previousPropertyValues = commaDelimitedListToSet(String.valueOf(previousPropertyValue));
            newPropertyValues.addAll(previousPropertyValues);
        }

        for (int i = 0; i < length; i++) {
            String propertyValue = propertyValues[i];
            newPropertyValues.addAll(commaDelimitedListToSet(propertyValue));
        }

        if (!newPropertyValues.isEmpty()) {
            String propertyValue = collectionToCommaDelimitedString(newPropertyValues);
            properties.put(propertyName, propertyValue);
        }
    }

    protected static <T> T convertProperty(Environment environment, Object propertyObject, Class<T> targetType, T defaultValue) {
        T propertyValue = defaultValue;
        if (propertyObject != null) {
            ConversionService conversionService = getConversionService(environment);
            if (conversionService != null) {
                if (conversionService.canConvert(propertyObject.getClass(), targetType)) {
                    propertyValue = conversionService.convert(propertyObject, targetType);
                }
            }
        }
        return propertyValue;
    }

    public static String joinPropertyName(String propertyNamePrefix, Object... propertyNameSuffixes) {
        StringJoiner propertyNameJoiner = new StringJoiner(PROPERTY_NAME_SEPARATOR);
        String prefix = normalizePrefix(propertyNamePrefix);
        propertyNameJoiner.add(prefix);
        for (Object propertyNameSuffix : propertyNameSuffixes) {
            if (propertyNameSuffix instanceof String) {
                String suffix = normalizePrefix((String) propertyNameSuffix);
                propertyNameJoiner.add(suffix);
            }
        }
        return propertyNameJoiner.toString();
    }

    private static SortedSet<String> getSubPropertyNames(ConfigurableEnvironment environment, String prefix) {
        SortedSet<String> subPropertyNames = new TreeSet<>();
        Map<String, Object> flattenSubProperties = getSubProperties(environment, prefix);
        for (String subPropertyName : flattenSubProperties.keySet()) {
            subPropertyNames.add(substringBefore(subPropertyName, PROPERTY_NAME_SEPARATOR));
        }
        return Collections.unmodifiableSortedSet(subPropertyNames);
    }

    private static String normalizePrefix(String value) {
        return value.endsWith(PROPERTY_NAME_SEPARATOR) ? value.substring(0, value.length() - PROPERTY_NAME_SEPARATOR.length()) : value;
    }

    private static String getModuleDefaultPropertiesPropertyNamePrefix(String module) {
        return joinPropertyName(getModulePropertyNamePrefix(module), DEFAULT_PROPERTIES_PROPERTY_NAME);
    }
}
