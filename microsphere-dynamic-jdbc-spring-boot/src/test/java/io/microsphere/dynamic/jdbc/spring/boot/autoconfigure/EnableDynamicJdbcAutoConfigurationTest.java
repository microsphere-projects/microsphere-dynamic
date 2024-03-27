package io.microsphere.dynamic.jdbc.spring.boot.autoconfigure;

import static io.microsphere.dynamic.jdbc.spring.boot.autoconfigure.DynamicJdbcAutoConfigurationRepository.getAutoConfigurationClassNames;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * {@link EnableDynamicJdbcAutoConfiguration} Test
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EnableDynamicJdbcAutoConfigurationTest.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableDynamicJdbcAutoConfiguration
public class EnableDynamicJdbcAutoConfigurationTest implements BeanClassLoaderAware {

    private ClassLoader classLoader;

    @Test
    public void test() {
        String[] autoConfigurationClassNames = getAutoConfigurationClassNames(classLoader);
        assertNotNull(autoConfigurationClassNames);
        assertTrue(autoConfigurationClassNames.length > 0);
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }


}
