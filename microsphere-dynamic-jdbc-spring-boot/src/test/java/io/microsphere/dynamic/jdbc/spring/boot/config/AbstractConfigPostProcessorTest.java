package io.microsphere.dynamic.jdbc.spring.boot.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * {@link AbstractConfigPostProcessor} Test
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AbstractConfigPostProcessorTest.TestConfigPostProcessor.class)
public class AbstractConfigPostProcessorTest {

    @Autowired
    private AbstractConfigPostProcessor configPostProcessor;

    @Autowired
    private Environment environment;

    @Autowired
    private ConfigurableApplicationContext context;

    @Test
    public void test() {
        assertEquals(TestConfigPostProcessor.class, configPostProcessor.getClass());
        assertEquals(0, configPostProcessor.getOrder());
        assertSame(environment, configPostProcessor.environment);
        assertSame(context.getClassLoader(), configPostProcessor.classLoader);
        assertSame(context, configPostProcessor.context);
    }

    static class TestConfigPostProcessor extends AbstractConfigPostProcessor {

        @Override
        public void postProcess(DynamicJdbcConfig shardingJdbcConfig, String shardingJdbcConfigPropertyName) {

        }
    }
}
