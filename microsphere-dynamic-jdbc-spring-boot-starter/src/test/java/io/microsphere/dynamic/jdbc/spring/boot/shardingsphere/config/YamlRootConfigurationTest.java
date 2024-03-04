package io.microsphere.dynamic.jdbc.spring.boot.shardingsphere.config;

import static org.junit.Assert.assertNotNull;

import org.apache.shardingsphere.infra.yaml.config.pojo.YamlRootConfiguration;
import org.apache.shardingsphere.infra.yaml.engine.YamlEngine;
import org.junit.Test;

import io.microsphere.dynamic.jdbc.spring.boot.AbstractTest;

/**
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class YamlRootConfigurationTest extends AbstractTest {

    @Test
    public void test() throws Throwable {
        String yamlContent = getContent("META-INF/sharding-sphere/config/sharding-tables.yaml");
        YamlRootConfiguration yamlRootConfiguration = YamlEngine.unmarshal(yamlContent, YamlRootConfiguration.class);
        assertNotNull(yamlRootConfiguration);
    }
}
