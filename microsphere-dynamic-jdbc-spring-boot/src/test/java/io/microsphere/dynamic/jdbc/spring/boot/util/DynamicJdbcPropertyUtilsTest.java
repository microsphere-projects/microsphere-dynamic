package io.microsphere.dynamic.jdbc.spring.boot.util;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static io.microsphere.dynamic.jdbc.spring.boot.AbstractTest.ofSet;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.AUTO_CONFIGURATION_BASE_PACKAGES_PROPERTY_NAME;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.DATASOURCE_MODULE;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.HIGH_AVAILABILITY_DATASOURCE_MODULE;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.MYBATIS_MODULE;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.MYBATIS_PLUS_MODULE;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.SHARDING_SPHERE_MODULE;
import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.TRANSACTION_MODULE;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.appendPropertyValue;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.getAllModulesAutoConfigurationBasePackages;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.getAllModulesPropertyValues;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.getClusterDataSourcePassword;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.getClusterDataSourceUserName;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.getDataSourceDefaultPassword;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.getDataSourceDefaultUserName;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.getDataSourceUrlDefaultQueryParams;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.getModuleAutoConfigurationBannedModules;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.getModuleAutoConfigurationBasePackages;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.getModuleExclusionAutoConfigurationClassNames;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.getModulePropertyNameAliases;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils.isDynamicJdbcEnabled;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * {@link DynamicJdbcPropertyUtils} Test
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:/META-INF/dynamic-jdbc/default.properties",
        properties = {"microsphere.dynamic.jdbc.modules.datasource.default-user-name=root",
                "microsphere.dynamic.jdbc.modules.datasource.default-password=123456"})
@ContextConfiguration(classes = DynamicJdbcPropertyUtilsTest.class)
public class DynamicJdbcPropertyUtilsTest {

    @Autowired
    private ConfigurableEnvironment environment;

    @Autowired
    private ConfigurableApplicationContext context;

    @Test
    public void testIsDynamicJdbcEnabled() {
        assertTrue(isDynamicJdbcEnabled(environment));
    }

    @Test
    public void testGetAllDynamicJdbcModulesAutoConfigurationBasePackages() {
        Set<String> basePackages = getAllModulesAutoConfigurationBasePackages(environment);
        assertEquals(new TreeSet<>(asList("com.baomidou.", "org.apache.shardingsphere.", "org.mybatis.spring.boot.",
                "org.springframework.boot.autoconfigure.transaction.")), basePackages);
    }

    @Test
    public void testGetModuleAutoConfigurationBasePackages() {
        Set<String> basePackages = getModuleAutoConfigurationBasePackages(environment, DATASOURCE_MODULE);
        assertEquals(emptySet(), basePackages);

        basePackages = getModuleAutoConfigurationBasePackages(environment, HIGH_AVAILABILITY_DATASOURCE_MODULE);
        assertEquals(emptySet(), basePackages);

        basePackages = getModuleAutoConfigurationBasePackages(environment, TRANSACTION_MODULE);
        assertEquals(singleton("org.springframework.boot.autoconfigure.transaction."), basePackages);

        basePackages = getModuleAutoConfigurationBasePackages(environment, SHARDING_SPHERE_MODULE);
        assertEquals(singleton("org.apache.shardingsphere."), basePackages);

        basePackages = getModuleAutoConfigurationBasePackages(environment, MYBATIS_MODULE);
        assertEquals(singleton("org.mybatis.spring.boot."), basePackages);

        basePackages = getModuleAutoConfigurationBasePackages(environment, MYBATIS_PLUS_MODULE);
        assertEquals(singleton("com.baomidou."), basePackages);

    }

    @Test
    public void testGetDynamicJdbcModuleAutoConfigurationBannedModules() {
        Set<String> bannedModules = getModuleAutoConfigurationBannedModules(environment, DATASOURCE_MODULE);
        assertEquals(singleton("sharding-sphere"), bannedModules);

        bannedModules = getModuleAutoConfigurationBannedModules(environment, HIGH_AVAILABILITY_DATASOURCE_MODULE);
        assertEquals(singleton("sharding-sphere"), bannedModules);

        bannedModules = getModuleAutoConfigurationBannedModules(environment, TRANSACTION_MODULE);
        assertEquals(emptySet(), bannedModules);

        bannedModules = getModuleAutoConfigurationBannedModules(environment, SHARDING_SPHERE_MODULE);
        assertEquals(ofSet("datasource"), bannedModules);

        bannedModules = getModuleAutoConfigurationBannedModules(environment, MYBATIS_MODULE);
        assertEquals(singleton("mybatis-plus"), bannedModules);

        bannedModules = getModuleAutoConfigurationBannedModules(environment, MYBATIS_PLUS_MODULE);
        assertEquals(singleton("mybatis"), bannedModules);
    }

    @Test
    public void testGetModuleExclusionAutoConfigurationClassNames() {
        Set<String> exclusionClassNames = getModuleExclusionAutoConfigurationClassNames(context, DATASOURCE_MODULE);
        assertEquals(new TreeSet<>(asList("org.apache.shardingsphere.dbdiscovery.spring.boot.DatabaseDiscoveryRuleSpringbootConfiguration",
                "org.apache.shardingsphere.encrypt.spring.boot.EncryptRuleSpringBootConfiguration",
                "org.apache.shardingsphere.parser.spring.boot.SQLParserRuleSpringBootConfiguration",
                "org.apache.shardingsphere.readwritesplitting.spring.boot.ReadwriteSplittingRuleSpringbootConfiguration",
                "org.apache.shardingsphere.shadow.spring.boot.ShadowRuleSpringBootConfiguration",
                "org.apache.shardingsphere.sharding.spring.boot.ShardingRuleSpringBootConfiguration",
                "org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration")), exclusionClassNames);

        exclusionClassNames = getModuleExclusionAutoConfigurationClassNames(context, HIGH_AVAILABILITY_DATASOURCE_MODULE);
        assertEquals(new TreeSet<>(asList("org.apache.shardingsphere.dbdiscovery.spring.boot.DatabaseDiscoveryRuleSpringbootConfiguration",
                "org.apache.shardingsphere.encrypt.spring.boot.EncryptRuleSpringBootConfiguration",
                "org.apache.shardingsphere.parser.spring.boot.SQLParserRuleSpringBootConfiguration",
                "org.apache.shardingsphere.readwritesplitting.spring.boot.ReadwriteSplittingRuleSpringbootConfiguration",
                "org.apache.shardingsphere.shadow.spring.boot.ShadowRuleSpringBootConfiguration",
                "org.apache.shardingsphere.sharding.spring.boot.ShardingRuleSpringBootConfiguration",
                "org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration")), exclusionClassNames);

        exclusionClassNames = getModuleExclusionAutoConfigurationClassNames(context, TRANSACTION_MODULE);
        assertEquals(emptySet(), exclusionClassNames);

        exclusionClassNames = getModuleExclusionAutoConfigurationClassNames(context, SHARDING_SPHERE_MODULE);
        assertEquals(ofSet("org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"), exclusionClassNames);

        exclusionClassNames = getModuleExclusionAutoConfigurationClassNames(context, MYBATIS_MODULE);
        assertEquals(new TreeSet<>(asList("com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration",
                "com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration",
                "com.baomidou.mybatisplus.autoconfigure.MybatisPlusLanguageDriverAutoConfiguration")), exclusionClassNames);

        exclusionClassNames = getModuleExclusionAutoConfigurationClassNames(context, MYBATIS_PLUS_MODULE);
        assertEquals(new TreeSet<>(asList("org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration",
                "org.mybatis.spring.boot.autoconfigure.MybatisLanguageDriverAutoConfiguration")), exclusionClassNames);
    }

    @Test
    public void testGetModulePropertyNameAliases() {
        Set<String> propertyNameAliases = getModulePropertyNameAliases(environment, DATASOURCE_MODULE, "url");
        assertEquals(new TreeSet<>(asList("jdbcUrl", "jdbc-url")), propertyNameAliases);

        propertyNameAliases = getModulePropertyNameAliases(environment, DATASOURCE_MODULE, "jdbcUrl");
        assertEquals(new TreeSet<>(asList("url", "jdbc-url")), propertyNameAliases);

        propertyNameAliases = getModulePropertyNameAliases(environment, DATASOURCE_MODULE, "jdbc-url");
        assertEquals(new TreeSet<>(asList("url", "jdbcUrl")), propertyNameAliases);
    }

    @Test
    public void testGetDataSourceUrlDefaultQueryParams() {
        Map<String, String> defaultQueryParams = getDataSourceUrlDefaultQueryParams(environment);
        assertEquals("utf-8", defaultQueryParams.get("characterEncoding"));
        assertEquals("3000", defaultQueryParams.get("socketTimeout"));
        assertEquals("false", defaultQueryParams.get("useSSL"));
        assertEquals("true", defaultQueryParams.get("useUnicode"));
    }

    @Test
    public void testGetDataSourceDefaultUserName() {
        String defaultUserName = getDataSourceDefaultUserName(environment);
        assertEquals("root", defaultUserName);
    }

    @Test
    public void testGetDataSourceDefaultPassword() {
        String defaultUserName = getDataSourceDefaultPassword(environment);
        assertEquals("123456", defaultUserName);
    }

    @Test
    public void testGetDynamicJdbcModuleProperties() {
        Map<String, Object> moduleProperties = DynamicJdbcPropertyUtils.getModuleProperties(environment, new DynamicJdbcConfig.Mybatis());
        assertNotNull(moduleProperties);
        assertEquals("org.mybatis.spring.boot.", moduleProperties.get(AUTO_CONFIGURATION_BASE_PACKAGES_PROPERTY_NAME));
    }

    @Test
    public void testGetDynamicJdbcModuleProperty() {
        String propertyValue = DynamicJdbcPropertyUtils.getModuleProperty(environment, new DynamicJdbcConfig.ShardingSphere(),
                AUTO_CONFIGURATION_BASE_PACKAGES_PROPERTY_NAME);
        assertEquals("org.apache.shardingsphere.", propertyValue);
    }

    @Test
    public void testGetAllDynamicJdbcModulesPropertyValues() {
        List<String> propertyValues = getAllModulesPropertyValues(environment, AUTO_CONFIGURATION_BASE_PACKAGES_PROPERTY_NAME);
        assertEquals(asList("com.baomidou.", "org.apache.shardingsphere.", "org.mybatis.spring.boot.",
                "org.springframework.boot.autoconfigure.transaction."), propertyValues);
    }

    @Test
    public void testAppendPropertyValue() {
        Map<String, Object> properties = new HashMap<>();
        appendPropertyValue(properties, "test-name", "test-value");
        assertEquals("test-value", properties.get("test-name"));
        appendPropertyValue(properties, "test-name", "test-value-1");
        assertEquals("test-value,test-value-1", properties.get("test-name"));
    }

    @Test
    public void testGetClusterDataSourceUserName() {
        MockEnvironment environment = new MockEnvironment();
        assertNull(getClusterDataSourceUserName(environment, null));
        assertNull(getClusterDataSourceUserName(environment, " "));
        assertNull(getClusterDataSourceUserName(environment, ""));
        assertNull(getClusterDataSourceUserName(environment, "clusterId"));

        environment.setProperty("spring.application.name", "test-app");
        assertNull(getClusterDataSourceUserName(environment, "clusterId"));

        environment.setProperty("test-app.clusterId.username", "root");
        assertEquals("root", getClusterDataSourceUserName(environment, "clusterId"));
    }

    @Test
    public void testGetClusterDataSourcePassword() {
        MockEnvironment environment = new MockEnvironment();
        assertNull(getClusterDataSourcePassword(environment, null));
        assertNull(getClusterDataSourcePassword(environment, " "));
        assertNull(getClusterDataSourcePassword(environment, ""));
        assertNull(getClusterDataSourcePassword(environment, "clusterId"));

        environment.setProperty("spring.application.name", "test-app");
        assertNull(getClusterDataSourcePassword(environment, "clusterId"));

        environment.setProperty("test-app.clusterId.password", "1234560");
        assertEquals("1234567890", getClusterDataSourcePassword(environment, "clusterId"));
    }
}
