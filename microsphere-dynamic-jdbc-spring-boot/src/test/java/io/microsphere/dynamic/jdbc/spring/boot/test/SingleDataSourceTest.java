package io.microsphere.dynamic.jdbc.spring.boot.test;

import static io.microsphere.multiple.active.zone.ZoneConstants.ZONE_PROPERTY_NAME;
import static java.util.Collections.singletonMap;

import java.util.List;

import io.microsphere.dynamic.jdbc.spring.boot.shardingsphere.examples.core.api.ExampleExecuteTemplate;
import io.microsphere.dynamic.jdbc.spring.boot.shardingsphere.examples.core.api.service.ExampleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * TestCase for Single DataSource
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SingleDataSourceTest.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(locations = "classpath:/META-INF/sharding-sphere/single-datasource.properties",
        properties = {"spring.shardingsphere.enabled=false", "microsphere.dynamic.jdbc.modules.datasource.dynamic-context.close-delay=3s"})
@ComponentScan(basePackages = "io.microsphere.dynamic.jdbc.spring.boot.shardingsphere.examples.mybatis")
@EnableAutoConfiguration
public class SingleDataSourceTest {

    @Autowired
    private List<ExampleService> exampleServices;

    @Autowired
    private ConfigurableApplicationContext context;

    @Autowired
    private ConfigurableEnvironment environment;

    @Autowired
    @Qualifier("my-tx")
    private DataSourceTransactionManager dataSourceTransactionManager;

    @Test
    public void test() throws Throwable {

        for (ExampleService exampleService : exampleServices) {
            ExampleExecuteTemplate.run(exampleService);
        }

        MutablePropertySources propertySources = environment.getPropertySources();

        propertySources.addFirst(new MapPropertySource("zone", singletonMap(ZONE_PROPERTY_NAME, "test-zone")));

        // context.publishEvent(new ConfigChangeEvent(singleton(ZONE_PROPERTY_NAME)));
        // No Change
        // context.publishEvent(new ConfigChangeEvent(singleton(ZONE_PROPERTY_NAME)));

        for (ExampleService exampleService : exampleServices) {
            ExampleExecuteTemplate.run(exampleService);
        }

        Thread.sleep(5 * 1000);

    }
}
