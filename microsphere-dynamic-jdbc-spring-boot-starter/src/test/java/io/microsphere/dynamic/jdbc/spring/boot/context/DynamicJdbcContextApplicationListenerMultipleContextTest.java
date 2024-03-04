package io.microsphere.dynamic.jdbc.spring.boot.context;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.dynamic.jdbc.spring.boot.shardingsphere.examples.core.api.service.ExampleService;
import io.microsphere.dynamic.jdbc.spring.boot.shardingsphere.examples.core.api.service.ExampleServiceImpl;
import org.apache.ibatis.annotations.Mapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;

import static io.microsphere.multiple.active.zone.ZoneConstants.ZONE_PROPERTY_NAME;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * {@link DynamicJdbcContextApplicationListener} Test
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DynamicJdbcContextApplicationListenerMultipleContextTest.class, ExampleServiceImpl.class

}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(locations = "classpath:/META-INF/dynamic-jdbc/multiple-configs.properties")
@EnableAutoConfiguration
public class DynamicJdbcContextApplicationListenerMultipleContextTest {

    @Autowired
    private Map<String, DynamicJdbcConfig> shardingJdbcConfigs;

    @Autowired
    private Map<String, DataSource> dataSourceMap;

    @Autowired
    private Map<String, PlatformTransactionManager> platformTransactionManagers;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ConfigurableEnvironment environment;

    @Autowired
    private ExampleService exampleService;

    @Test
    public void testBeans() throws Exception {
        assertEquals(3, shardingJdbcConfigs.size());

        Map<String, Object> mappers = context.getBeansWithAnnotation(Mapper.class);
        assertEquals(12, mappers.size());

        assertEquals(1, platformTransactionManagers.size());
        assertTrue(platformTransactionManagers.containsKey("myTransaction"));

        exampleService.processSuccess();

        MutablePropertySources propertySources = environment.getPropertySources();

        propertySources.addFirst(new MapPropertySource("zone", singletonMap(ZONE_PROPERTY_NAME, "test-zone")));

        // context.publishEvent(new ConfigChangeEvent(singleton("microsphere.dynamic.jdbc.configs.single-database-sharding-tables")));

        // context.publishEvent(new ConfigChangeEvent(singleton(ZONE_PROPERTY_NAME)));
        // No Change
        // context.publishEvent(new ConfigChangeEvent(singleton(ZONE_PROPERTY_NAME)));

        Thread.sleep(10 * 1000);

    }
}
