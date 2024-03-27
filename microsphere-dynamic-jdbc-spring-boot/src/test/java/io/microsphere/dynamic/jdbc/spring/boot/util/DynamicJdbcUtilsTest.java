package io.microsphere.dynamic.jdbc.spring.boot.util;

import io.microsphere.dynamic.jdbc.spring.boot.AbstractTest;
import io.microsphere.dynamic.jdbc.spring.boot.config.ConfigPostProcessor;
import io.microsphere.dynamic.jdbc.spring.boot.context.ConfigBeanDefinitionRegistrar;
import io.microsphere.dynamic.jdbc.spring.boot.env.ConfigConfigurationPropertiesSynthesizer;
import org.junit.Test;
import org.springframework.context.support.GenericApplicationContext;

import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcUtils.resolveDBClusterId;
import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcUtils.resolveDatabaseHost;
import static io.microsphere.spring.util.SpringFactoriesLoaderUtils.loadFactories;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * {@link DynamicJdbcUtils} Test
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class DynamicJdbcUtilsTest extends AbstractTest {

    @Test
    public void testLoadSpringFactories() {
        GenericApplicationContext context = new GenericApplicationContext();

        assertEquals(6, loadFactories(context, ConfigPostProcessor.class).size());

        assertEquals(5, loadFactories(context, ConfigConfigurationPropertiesSynthesizer.class).size());

        assertEquals(5, loadFactories(context, ConfigBeanDefinitionRegistrar.class).size());

    }

    @Test
    public void testLoadSpringFactoriesWithArgs() {

        GenericApplicationContext context = new GenericApplicationContext();

        assertEquals(6, loadFactories(context, ConfigPostProcessor.class, null).size());

        assertEquals(5, loadFactories(context, ConfigConfigurationPropertiesSynthesizer.class, new Object[0]).size());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadSpringFactoriesOnException() {
        GenericApplicationContext context = new GenericApplicationContext();
        loadFactories(context, ConfigConfigurationPropertiesSynthesizer.class, "Error");
    }

    @Test
    public void testResolveDatabaseHost() {
        assertEquals("bnb-asset1.cluster-ct1wbptr1zqn.ap-northeast-1.rds.amazonaws.com", resolveDatabaseHost(
                "jdbc:mysql://bnb-asset1.cluster-ct1wbptr1zqn.ap-northeast-1.rds.amazonaws.com/asset?useUnicode=true&characterEncoding=utf-8&useSSL=false"));
        assertEquals("localhost", resolveDatabaseHost("jdbc:mysql://127.0.0.1:3306/demo_ds?socketTimeout=3000&useUnicode=true"));
        assertEquals("localhost", resolveDatabaseHost("jdbc:mariadb:aurora//127.0.0.1:3306/demo_ds"));
        assertNull(resolveDatabaseHost("127.0.0.1:3306/demo_ds"));
        assertNull(resolveDatabaseHost(" "));
        assertNull(resolveDatabaseHost(""));
        assertNull(resolveDatabaseHost(null));
    }

    @Test
    public void testResolveDatasourceClusterId() {
        assertEquals("bnb-asset1",resolveDBClusterId("bnb-asset1.cluster-ct1wbptr1zqn.ap-northeast-1.rds.amazonaws.com"));
        assertNull(resolveDBClusterId("localhost"));
        assertNull(resolveDBClusterId(" "));
        assertNull(resolveDBClusterId(""));
        assertNull(resolveDBClusterId(null));
    }
}
