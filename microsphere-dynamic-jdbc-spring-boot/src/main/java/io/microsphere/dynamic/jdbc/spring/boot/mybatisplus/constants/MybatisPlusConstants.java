package io.microsphere.dynamic.jdbc.spring.boot.mybatisplus.constants;

import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.BASE_PACKAGES_PROPERTY_NAME;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.DYNAMIC_JDBC_MODULES_PROPERTY_NAME_PREFIX;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.MYBATIS_PLUS_MODULE;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.PROPERTY_NAME_SEPARATOR;

/**
 * The Constants of Mybatis-plus
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public interface MybatisPlusConstants {

    String MYBATIS_PLUS__MODULE_PROPERTY_NAME_PREFIX = DYNAMIC_JDBC_MODULES_PROPERTY_NAME_PREFIX + PROPERTY_NAME_SEPARATOR + MYBATIS_PLUS_MODULE;

    String DYNAMIC_JDBC_MYBATIS_PLUS_CLASS_PREFIXES_PROPERTY_NAME =
            MYBATIS_PLUS__MODULE_PROPERTY_NAME_PREFIX + PROPERTY_NAME_SEPARATOR + BASE_PACKAGES_PROPERTY_NAME;

    String DYNAMIC_JDBC_MYBATIS_PLUS_BASE_PACKAGES_PLACEHOLDER = "${" + DYNAMIC_JDBC_MYBATIS_PLUS_CLASS_PREFIXES_PROPERTY_NAME + "}";

}
