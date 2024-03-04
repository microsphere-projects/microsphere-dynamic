package io.microsphere.dynamic.jdbc.spring.boot.util;

import io.microsphere.dynamic.jdbc.spring.boot.AbstractTest;
import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import org.junit.Test;
import org.springframework.mock.env.MockEnvironment;

import java.util.Map;

import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.cloneDynamicJdbcConfig;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.generateDynamicDataSourceDynamicJdbcChildContextId;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.generateDynamicDataSourceDynamicJdbcConfigName;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.generateDynamicJdbcChildContextId;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.generateDynamicJdbcConfigBeanName;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.generateSynthesizedPropertySourceName;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.getDataSourceName;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.getDataSourcePassword;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.getDataSourceType;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.getDataSourceUrl;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.getDataSourceUrlEntry;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.getDataSourceUserName;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.getDriverClassName;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.getDynamicJdbcConfigs;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.getModule;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.isDynamicJdbcConfig;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.parseDynamicJdbcConfig;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.resolveDynamicJdbcConfigName;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.getConfigPropertyName;
import static java.util.Collections.emptyMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * {@link DynamicJdbcConfigUtils} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class DynamicJdbcConfigUtilsTest extends AbstractTest {

    @Test
    public void testGetDynamicJdbcConfigs() {
        MockEnvironment environment = new MockEnvironment();
        Map<String, DynamicJdbcConfig> shardingJdbcConfigs = getDynamicJdbcConfigs(environment);

        assertEquals(emptyMap(), shardingJdbcConfigs);

        environment.setProperty(getConfigPropertyName("main"), getContent("META-INF/dynamic-jdbc/sharding-tables-config.json"));
        shardingJdbcConfigs = getDynamicJdbcConfigs(environment);

        assertEquals(1, shardingJdbcConfigs.size());
        DynamicJdbcConfig shardingJdbcConfig = getDynamicJdbcConfig();
        assertNotNull(shardingJdbcConfig);
        assertEquals("sharding-tables-config", shardingJdbcConfig.getName());
    }

    @Test
    public void testGetDynamicJdbcConfig() {
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty(getConfigPropertyName("main"), "classpath:/META-INF/dynamic-jdbc/sharding-tables-config.json");
        DynamicJdbcConfig shardingJdbcConfig = DynamicJdbcConfigUtils.getDynamicJdbcConfig(environment, "microsphere.dynamic.jdbc.configs.main");
        assertEquals("sharding-tables-config", shardingJdbcConfig.getName());
        assertTrue(shardingJdbcConfig.hasDataSource());
    }

    @Test
    public void testCloneDynamicJdbcConfig() {
        DynamicJdbcConfig shardingJdbcConfig = getDynamicJdbcConfig();
        DynamicJdbcConfig clonedDynamicJdbcConfig = cloneDynamicJdbcConfig(shardingJdbcConfig);
        assertEquals(shardingJdbcConfig, clonedDynamicJdbcConfig);
        assertEquals(shardingJdbcConfig.toString(), clonedDynamicJdbcConfig.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseDynamicJdbcConfig() {
        parseDynamicJdbcConfig("test", "");
    }

    @Test
    public void testIsDynamicJdbcConfig() {
        DynamicJdbcConfig config = getDynamicJdbcConfig();
        assertFalse(isDynamicJdbcConfig(config));

        config.setName("Dynamic#A");
        assertTrue(isDynamicJdbcConfig(config));
    }

    @Test
    public void testGenerateDynamicDataSourceDynamicJdbcConfigName() {
        DynamicJdbcConfig config = getDynamicJdbcConfig();
        assertEquals("Dynamic#sharding-tables-config#datasource", generateDynamicDataSourceDynamicJdbcConfigName(config));
    }

    @Test
    public void testGenerateDynamicDataSourceDynamicJdbcChildContextId() {
        DynamicJdbcConfig config = getDynamicJdbcConfig();
        String id = generateDynamicDataSourceDynamicJdbcChildContextId(config);
        assertEquals("Dynamic#DynamicJdbcChildContext[sharding-tables-config]#datasource", id);
    }

    @Test
    public void testGenerateSynthesizedPropertySourceName() {
        String name = generateSynthesizedPropertySourceName("test");
        assertEquals("SynthesizedPropertySource[test]", name);
    }

    @Test
    public void testGetModule() {
        assertEquals("transaction", getModule(new DynamicJdbcConfig.Transaction()));
        assertEquals("sharding-sphere", getModule(new DynamicJdbcConfig.ShardingSphere()));
        assertEquals("mybatis", getModule(new DynamicJdbcConfig.Mybatis()));
        assertEquals("mybatis-plus", getModule(new DynamicJdbcConfig.MybatisPlus()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetModuleWithIllegalArgumentException() {
        getModule(DynamicJdbcConfig.Config.class);
    }

    @Test
    public void testGenerateDynamicJdbcConfigBeanName() {
        String beanName = generateDynamicJdbcConfigBeanName(getDynamicJdbcConfig(), "microsphere.dynamic.jdbc.configs.test");
        assertEquals("DynamicJdbcConfigBean[test].sharding-tables-config", beanName);
    }

    @Test
    public void testGenerateDynamicJdbcChildContextId() {
        DynamicJdbcConfig shardingJdbcConfig = getDynamicJdbcConfig();
        String contextId = generateDynamicJdbcChildContextId(shardingJdbcConfig);
        assertEquals("DynamicJdbcChildContext[sharding-tables-config]", contextId);
    }

    @Test
    public void testResolveDynamicJdbcConfigName() {
        String name = resolveDynamicJdbcConfigName("DynamicJdbcChildContext[test]");
        assertEquals("test", name);
    }

    @Test
    public void testGetDataSourceProperties() {
        DynamicJdbcConfig shardingJdbcConfig = getDynamicJdbcConfig();
        Map<String, String> dataSource = shardingJdbcConfig.getDataSourcePropertiesList().get(0);
        assertEquals("demo_ds", getDataSourceName(dataSource));
        assertEquals("com.zaxxer.hikari.HikariDataSource", getDataSourceType(dataSource));
        assertEquals("com.mysql.jdbc.Driver", getDriverClassName(dataSource));
        assertEquals("jdbc:mysql://127.0.0.1:3306/demo_ds?serverTimezone=UTC&useSSL=false&useUnicode=true&characterEncoding=UTF-8",
                getDataSourceUrl(dataSource));
        Map.Entry<String, String> entry = getDataSourceUrlEntry(dataSource);
        assertEquals("url", entry.getKey());
        assertEquals("jdbc:mysql://127.0.0.1:3306/demo_ds?serverTimezone=UTC&useSSL=false&useUnicode=true&characterEncoding=UTF-8", entry.getValue());
        assertEquals("root", getDataSourceUserName(dataSource));
        assertEquals("123456", getDataSourcePassword(dataSource));
    }

    private DynamicJdbcConfig getDynamicJdbcConfig() {
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty(getConfigPropertyName("main"), getContent("META-INF/dynamic-jdbc/sharding-tables-config.json"));
        Map<String, DynamicJdbcConfig> shardingJdbcConfigs = getDynamicJdbcConfigs(environment);
        assertEquals(1, shardingJdbcConfigs.size());
        return shardingJdbcConfigs.get("microsphere.dynamic.jdbc.configs.main");
    }

}
