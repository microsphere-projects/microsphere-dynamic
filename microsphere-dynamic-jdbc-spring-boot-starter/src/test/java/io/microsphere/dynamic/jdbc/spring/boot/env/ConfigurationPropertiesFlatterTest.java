package io.microsphere.dynamic.jdbc.spring.boot.env;

import io.microsphere.dynamic.jdbc.spring.boot.AbstractTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * {@link ConfigurationPropertiesFlatter} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ConfigurationPropertiesFlatter.class, ConfigurationPropertiesFlatterTest.class})
public class ConfigurationPropertiesFlatterTest extends AbstractTest {

    @Autowired
    private ConfigurationPropertiesFlatter resolver;

    @Test
    public void testResolve() {
        String jsonContent = getContent("META-INF/dynamic-jdbc/test-config.json");
        Map<String, Object> nestedProperties = fromJson(jsonContent, Map.class);
        Map<String, String> flattenProperties = resolver.flat(nestedProperties);

        assertEquals("test", flattenProperties.get("name"));
        assertEquals("ds1", flattenProperties.get("datasource[0].name"));
        assertEquals("ds2", flattenProperties.get("datasource[1].name"));
        assertEquals("com.zaxxer.hikari.HikariDataSource", flattenProperties.get("datasource[0].type"));
    }
}
