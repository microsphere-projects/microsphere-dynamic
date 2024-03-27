package io.microsphere.dynamic.jdbc.spring.boot.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import io.microsphere.dynamic.jdbc.spring.boot.AbstractTest;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * {@link DynamicJdbcConfig} Test
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class DynamicJdbcConfigTest extends AbstractTest {

    private static final String CONFIG_LOCATION = "META-INF/dynamic-jdbc/test-config.json";

    @Test
    public void test() throws IOException {
        DynamicJdbcConfig shardingJdbcConfig = fromJsonResource(CONFIG_LOCATION, DynamicJdbcConfig.class);
        assertEquals(
                "{\"name\":\"test\",\"dynamic\":true,\"shardingSphere\":{\"name\":null,\"configurations\":null,"
                        + "\"properties\":null,\"config-resource\":\"classpath:/META-INF/sharding-sphere/config.yaml\"},"
                        + "\"datasource\":[{\"name\":\"ds1\",\"type\":\"com.zaxxer.hikari.HikariDataSource\","
                        + "\"driverClassName\":\"org.apache.derby.jdbc.EmbeddedDriver\",\"url\":\"jdbc:derby:db/ds1;create=true\","
                        + "\"username\":\"\",\"password\":\"\"},{\"name\":\"ds2\",\"url\":\"jdbc:derby:db/ds2;create=true\"}],"
                        + "\"ha-datasource\":null,\"transaction\":{\"name\":\"tx1\",\"configurations\":null,"
                        + "\"properties\":{\"defaultTimeout\":3,\"rollbackOnCommitFailure\":true},\"customizers\":null},"
                        + "\"sharding-sphere\":{\"name\":null,\"configurations\":null,\"properties\":null,"
                        + "\"config-resource\":\"classpath:/META-INF/sharding-sphere/config.yaml\"},\"mybatis\":{\"name\":null,"
                        + "\"configurations\":null,\"properties\":{\"checkConfigLocation\":true,"
                        + "\"configLocation\":\"classpath:/META-INF/mybatis/mybatis-config.xml\","
                        + "\"mapperLocations\":\"META-INF/mappers/AddressMapper.xml,META-INF/mappers/OrderMapper.xml,"
                        + "META-INF/mappers/OrderItemMapper.xml\"},\"base-packages\":\"io.microsphere.dynamic.jdbc.spring.boot\"},"
                        + "\"mybatis-plus\":{\"name\":null,\"configurations\":null,\"properties\":null,"
                        + "\"base-packages\":\"io.microsphere.dynamic.jdbc.spring.boot.config\"}}",
                new ObjectMapper().writeValueAsString(shardingJdbcConfig));


        assertEquals(2, shardingJdbcConfig.getDataSourceSize());
        assertEquals(0, shardingJdbcConfig.getHighAvailabilityDataSourceZoneSize());
        assertTrue(shardingJdbcConfig.hasDataSource());
        assertFalse(shardingJdbcConfig.hasOnlySingleDataSource());
        assertFalse(shardingJdbcConfig.hasHighAvailabilityDataSource());
        assertTrue(shardingJdbcConfig.hasTransaction());
        assertTrue(shardingJdbcConfig.hasShardingDataSource());
        assertTrue(shardingJdbcConfig.hasMybatis());
        assertTrue(shardingJdbcConfig.hasMybatisPlus());

        List<Map<String, String>> dataSourcePropertiesList = shardingJdbcConfig.getDataSourcePropertiesList();
        assertEquals(2, dataSourcePropertiesList.size());

        Map<String, Map<String, String>> dataSourcePropertiesMap = shardingJdbcConfig.getDataSourcePropertiesMap();
        assertEquals(2, dataSourcePropertiesMap.size());
        assertTrue(dataSourcePropertiesMap.containsKey("ds1"));
        assertTrue(dataSourcePropertiesMap.containsKey("ds2"));
    }

    @Test
    public void testHA() {

        DynamicJdbcConfig shardingJdbcConfig = fromJsonResource("META-INF/sharding-sphere/sharding-tables.json", DynamicJdbcConfig.class);

        assertEquals(0, shardingJdbcConfig.getDataSourceSize());
        assertEquals(2, shardingJdbcConfig.getHighAvailabilityDataSourceZoneSize());
        assertFalse(shardingJdbcConfig.hasDataSource());
        assertTrue(shardingJdbcConfig.hasOnlySingleDataSource());
        assertTrue(shardingJdbcConfig.hasHighAvailabilityDataSource());
        assertFalse(shardingJdbcConfig.hasTransaction());
        assertTrue(shardingJdbcConfig.hasShardingDataSource());
        assertTrue(shardingJdbcConfig.hasMybatis());
        assertFalse(shardingJdbcConfig.hasMybatisPlus());

        List<Map<String, String>> dataSourcePropertiesList = shardingJdbcConfig.getDataSourcePropertiesList();
        assertEquals(1, dataSourcePropertiesList.size());

        Map<String, Map<String, String>> dataSourcePropertiesMap = shardingJdbcConfig.getDataSourcePropertiesMap();
        assertEquals(1, dataSourcePropertiesMap.size());
        assertTrue(dataSourcePropertiesMap.containsKey("ds"));
    }
}
