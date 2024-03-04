package io.microsphere.dynamic.jdbc.spring.boot.shardingsphere.constants;

import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.PROPERTY_NAME_SEPARATOR;

/**
 * The Constants of Dynamic Sphere
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public interface ShardingSphereConstants {

    String SHARDING_SPHERE_PROPERTY_NAME_PREFIX = "spring.shardingsphere";

    String SHARDING_SPHERE_PROPERTIES_PROPERTY_NAME_PREFIX = SHARDING_SPHERE_PROPERTY_NAME_PREFIX + PROPERTY_NAME_SEPARATOR + "props";

    String SHARDING_SPHERE_DATA_SOURCE_PROPERTY_NAME_PREFIX = SHARDING_SPHERE_PROPERTY_NAME_PREFIX + PROPERTY_NAME_SEPARATOR + "datasource";

    String SHARDING_SPHERE_DATA_SOURCE_NAMES_PROPERTY_NAME = SHARDING_SPHERE_DATA_SOURCE_PROPERTY_NAME_PREFIX + PROPERTY_NAME_SEPARATOR + "names";

    String SHARDING_SPHERE_DATASOURCE_CLASS_NAME = "org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource";
}
