package io.microsphere.dynamic.jdbc.spring.boot.constants;

/**
 * The constants of Dynamic JDBC
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public interface DynamicJdbcConstants {

    String DATASOURCE_MODULE = "datasource";

    String HIGH_AVAILABILITY_DATASOURCE_MODULE = "ha-datasource";

    String TRANSACTION_MODULE = "transaction";

    String SHARDING_SPHERE_MODULE = "sharding-sphere";

    String MYBATIS_MODULE = "mybatis";

    String MYBATIS_PLUS_MODULE = "mybatis-plus";

    String DYNAMIC_PREFIX = "Dynamic#";

    String DYNAMIC_SUFFIX = "#";

    String DYNAMIC_JDBC_CONFIG_BEAN_NAME_PREFIX = "DynamicJdbcConfigBean[";

    String DYNAMIC_JDBC_CONFIG_BEAN_NAME_SUFFIX = "]";

    String DYNAMIC_JDBC_CHILD_CONTEXT_ID_PREFIX = "DynamicJdbcChildContext[";

    String DYNAMIC_JDBC_CHILD_CONTEXT_ID_SUFFIX = "]";

    String SYNTHESIZED_PROPERTY_SOURCE_NAME_PREFIX = "SynthesizedPropertySource[";

    String SYNTHESIZED_PROPERTY_SOURCE_NAME_SUFFIX = "]";

    // Property

    String PROPERTY_NAME_SEPARATOR = ".";

    String DEFAULT_SEPARATOR = ",";

    String DYNAMIC_JDBC_PROPERTY_NAME_PREFIX = "microsphere.dynamic.jdbc";

    String ENABLED_PROPERTY_NAME = "enabled";

    String CONFIGS_PROPERTY_NAME = "configs";

    String AUTO_CONFIGURATION_PROPERTY_NAME = "auto-configuration";

    String BASE_PACKAGES_PROPERTY_NAME = "base-packages";

    String PROPERTY_NAME_ALIASES_PROPERTY_NAME = "property-name-aliases";

    String MODULE_PROPERTY_NAME = "modules";

    String BANNED_MODULES_PROPERTY_NAME = "banned-modules";

    String DEFAULT_PROPERTIES_PROPERTY_NAME = "default-properties";

    String DYNAMIC_CONTEXT_PROPERTY_NAME = "dynamic-context";

    String MULTIPLE_CONTEXT_PROPERTY_NAME = "multiple-context";

    String EXCLUDE_PROPERTY_NAME = "exclude";

    String EXPOSE_PROPERTY_NAME = "expose";

    String PRIMARY_PROPERTY_NAME = "primary";

    String BEAN_CLASSES_PROPERTY_NAME = "bean-classes";

    String AUTO_CONFIGURATION_BASE_PACKAGES_PROPERTY_NAME = AUTO_CONFIGURATION_PROPERTY_NAME + PROPERTY_NAME_SEPARATOR + BASE_PACKAGES_PROPERTY_NAME;

    String AUTO_CONFIGURATION_BANNED_MODULES_PROPERTY_NAME =
            AUTO_CONFIGURATION_PROPERTY_NAME + PROPERTY_NAME_SEPARATOR + BANNED_MODULES_PROPERTY_NAME;

    String DYNAMIC_JDBC_ENABLED_PROPERTY_NAME = DYNAMIC_JDBC_PROPERTY_NAME_PREFIX + PROPERTY_NAME_SEPARATOR + ENABLED_PROPERTY_NAME;

    boolean DEFAULT_DYNAMIC_JDBC_ENABLED_PROPERTY_VALUE = true;

    String DYNAMIC_JDBC_CONFIGS_PROPERTY_NAME_PREFIX = DYNAMIC_JDBC_PROPERTY_NAME_PREFIX + PROPERTY_NAME_SEPARATOR + CONFIGS_PROPERTY_NAME;

    String DYNAMIC_JDBC_MODULES_PROPERTY_NAME_PREFIX = DYNAMIC_JDBC_PROPERTY_NAME_PREFIX + PROPERTY_NAME_SEPARATOR + MODULE_PROPERTY_NAME;

    String MULTIPLE_CONTEXT_PROPERTY_NAME_PREFIX = DYNAMIC_JDBC_PROPERTY_NAME_PREFIX + PROPERTY_NAME_SEPARATOR + MULTIPLE_CONTEXT_PROPERTY_NAME;

    String MULTIPLE_CONTEXT_AUTO_CONFIGURATION_EXCLUDED_CLASSES_PROPERTY_NAME = MULTIPLE_CONTEXT_PROPERTY_NAME_PREFIX + PROPERTY_NAME_SEPARATOR
            + AUTO_CONFIGURATION_PROPERTY_NAME + PROPERTY_NAME_SEPARATOR + EXCLUDE_PROPERTY_NAME;

    String MULTIPLE_CONTEXT_EXPOSED_BEAN_CLASSES_PROPERTY_NAME =
            MULTIPLE_CONTEXT_PROPERTY_NAME_PREFIX + PROPERTY_NAME_SEPARATOR + BEAN_CLASSES_PROPERTY_NAME + PROPERTY_NAME_SEPARATOR + EXPOSE_PROPERTY_NAME;

    String MULTIPLE_CONTEXT_PRIMARY_BEAN_CLASSES_PROPERTY_NAME =
            MULTIPLE_CONTEXT_PROPERTY_NAME_PREFIX + PROPERTY_NAME_SEPARATOR + BEAN_CLASSES_PROPERTY_NAME + PROPERTY_NAME_SEPARATOR + PRIMARY_PROPERTY_NAME;


    // Resource

    String DEFAULT_PROPERTIES_LOCATION = "META-INF/dynamic-jdbc/default.properties";

}
