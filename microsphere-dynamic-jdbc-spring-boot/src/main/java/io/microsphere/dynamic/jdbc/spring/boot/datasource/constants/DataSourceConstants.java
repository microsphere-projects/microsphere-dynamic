package io.microsphere.dynamic.jdbc.spring.boot.datasource.constants;

import javax.sql.DataSource;

import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.DATASOURCE_MODULE;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.DYNAMIC_CONTEXT_PROPERTY_NAME;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.DYNAMIC_JDBC_MODULES_PROPERTY_NAME_PREFIX;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.PROPERTY_NAME_SEPARATOR;

/**
 * {@link DataSource} Constants
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public interface DataSourceConstants {

    String NAME_PROPERTY_NAME = "name";

    String TYPE_PROPERTY_NAME = "type";

    String DRIVER_CLASS_NAME_PROPERTY_NAME = "driverClassName";

    String URL_PROPERTY_NAME = "url";

    String JDBC_URL_CAMEL_PROPERTY_NAME = "jdbcUrl";

    String JDBC_URL_PROPERTY_NAME = "jdbc-url";

    String[] JDBC_URL_PROPERTY_NAMES = new String[] {URL_PROPERTY_NAME, JDBC_URL_PROPERTY_NAME, JDBC_URL_CAMEL_PROPERTY_NAME};

    String USER_NAME_PROPERTY_NAME = "username";

    String PASSWORD_NAME_PROPERTY_NAME = "password";

    String HIKARI_DATASOURCE_CLASS_NAME = "com.zaxxer.hikari.HikariDataSource";

    String DEFAULT_DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";

    String DEFAULT_SCHEME_PROPERTY_NAME = "default-scheme";

    String DEFAULT_QUERY_PARAMS_PROPERTY_NAME = "default-query-params";

    String DEFAULT_USER_NAME_PROPERTY_NAME = "default-user-name";

    String DEFAULT_PASSWORD_PROPERTY_NAME = "default-password";

    String DEFAULT_DATASOURCE_TYPE_NAME = HIKARI_DATASOURCE_CLASS_NAME;

    String DATA_SOURCE_MODULE_PROPERTY_NAME_PREFIX = DYNAMIC_JDBC_MODULES_PROPERTY_NAME_PREFIX + PROPERTY_NAME_SEPARATOR + DATASOURCE_MODULE;

    String DATA_SOURCE_URL_PROPERTY_NAME_PREFIX = DATA_SOURCE_MODULE_PROPERTY_NAME_PREFIX + PROPERTY_NAME_SEPARATOR + URL_PROPERTY_NAME;

    String DATA_SOURCE_URL_DEFAULT_SCHEME_PROPERTY_NAME =
            DATA_SOURCE_URL_PROPERTY_NAME_PREFIX + PROPERTY_NAME_SEPARATOR + DEFAULT_SCHEME_PROPERTY_NAME;

    String DATA_SOURCE_URL_DEFAULT_SCHEME_PROPERTY_VALUE = "jdbc:mysql//";

    String DATA_SOURCE_URL_DEFAULT_QUERY_PARAMS_PROPERTY_NAME_PREFIX =
            DATA_SOURCE_URL_PROPERTY_NAME_PREFIX + PROPERTY_NAME_SEPARATOR + DEFAULT_QUERY_PARAMS_PROPERTY_NAME;

    String DATA_SOURCE_DEFAULT_USER_NAME_PROPERTY_NAME =
            DATA_SOURCE_MODULE_PROPERTY_NAME_PREFIX + PROPERTY_NAME_SEPARATOR + DEFAULT_USER_NAME_PROPERTY_NAME;

    String DATA_SOURCE_DEFAULT_PASSWORD_PROPERTY_NAME =
            DATA_SOURCE_MODULE_PROPERTY_NAME_PREFIX + PROPERTY_NAME_SEPARATOR + DEFAULT_PASSWORD_PROPERTY_NAME;

    String DYNAMIC_DATA_SOURCE_CHILD_CONTEXT_CLOSE_DELAY_PROPERTY_NAME = DATA_SOURCE_MODULE_PROPERTY_NAME_PREFIX + PROPERTY_NAME_SEPARATOR
            + DYNAMIC_CONTEXT_PROPERTY_NAME + PROPERTY_NAME_SEPARATOR + "close-delay";

}
