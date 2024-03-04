package io.microsphere.dynamic.jdbc.spring.boot.datasource;

import static io.microsphere.dynamic.jdbc.spring.boot.datasource.constants.DataSourceConstants.DATA_SOURCE_MODULE_PROPERTY_NAME_PREFIX;
import static java.util.Collections.emptySet;
import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import io.microsphere.dynamic.jdbc.spring.boot.AbstractTest;

/**
 * {@link DataSourceModuleProperties} Test
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DataSourceModulePropertiesTest.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(locations = "classpath:META-INF/dynamic-jdbc/default.properties")
@EnableConfigurationProperties(DataSourceModuleProperties.class)
public class DataSourceModulePropertiesTest extends AbstractTest {

    @Autowired
    private DataSourceModuleProperties dataSourceModuleProperties;

    @Autowired
    private ConfigurableEnvironment environment;

    @Test
    public void test() {
        Set<String> bannedModules = dataSourceModuleProperties.getBannedModules();
        assertEquals(emptySet(), bannedModules);
    }

    @Test
    public void testBinder() {
        ConfigurationPropertySources.attach(environment);
        Binder binder = new Binder(ConfigurationPropertySources.get(environment));
        DataSourceModuleProperties dataSourceModuleProperties =
                binder.bind(DATA_SOURCE_MODULE_PROPERTY_NAME_PREFIX, DataSourceModuleProperties.class).get();

        Set<String> bannedModules = dataSourceModuleProperties.getBannedModules();
        assertEquals(emptySet(), bannedModules);
    }
}
