package io.microsphere.dynamic.jdbc.spring.boot.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.spring.config.env.event.PropertySourceChangedEvent;
import io.microsphere.spring.config.env.event.PropertySourcesChangedEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * {@link DynamicJdbcContextApplicationListener} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DynamicJdbcContextApplicationListenerTest.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(locations = "classpath:/META-INF/dynamic-jdbc/single-config.properties")
@EnableAutoConfiguration
public class DynamicJdbcContextApplicationListenerTest {

    @Autowired
    private Map<String, DynamicJdbcConfig> shardingJdbcConfigs;

    @Autowired
    private Map<String, DataSource> dataSourceMap;

    @Autowired
    private Map<String, PlatformTransactionManager> platformTransactionManagerMap;

    @Autowired
    private ConfigurableApplicationContext context;

    @Autowired
    private ConfigurableEnvironment environment;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testBeans() throws Throwable {
        assertEquals(1, shardingJdbcConfigs.size());

        DynamicJdbcConfig primaryConfig = shardingJdbcConfigs.get("DynamicJdbcConfigBean[primary].sharding-tables-config");
        assertEquals("sharding-tables-config", primaryConfig.getName());

        assertEquals(1, dataSourceMap.size());
        DataSource dataSource = dataSourceMap.get("DynamicJdbcDynamicDataSource");
        assertNotNull(dataSource);

        DataSource unwrappedDataSource = dataSource.unwrap(DataSource.class);
        assertEquals(ShardingSphereDataSource.class, unwrappedDataSource.getClass());

        Map<String, Object> mappers = context.getBeansWithAnnotation(Mapper.class);
        assertEquals(4, mappers.size());

        String propertyName = "microsphere.dynamic.jdbc.configs.primary";

        Map<String, Object> properties = new HashMap<>();
        properties.put(propertyName, environment.getProperty(propertyName));

        MapPropertySource propertySource = new MapPropertySource(propertyName, properties);

        PropertySourcesChangedEvent propertySourcesChangedEvent = new PropertySourcesChangedEvent(context, PropertySourceChangedEvent.added(context, propertySource));

        context.publishEvent(propertySourcesChangedEvent);

        assertNotEquals(unwrappedDataSource, dataSource.unwrap(DataSource.class));

    }
}
