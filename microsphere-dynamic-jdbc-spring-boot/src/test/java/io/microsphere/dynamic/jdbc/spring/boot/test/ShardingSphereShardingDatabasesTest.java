package io.microsphere.dynamic.jdbc.spring.boot.test;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import io.microsphere.dynamic.jdbc.spring.boot.shardingsphere.examples.core.api.ExampleExecuteTemplate;
import io.microsphere.dynamic.jdbc.spring.boot.shardingsphere.examples.core.api.service.ExampleService;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * TestCase for Sharding Sphere on Sharding DataSources
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShardingSphereShardingDatabasesTest.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(value = "classpath:/META-INF/sharding-sphere/sharding-databases.properties",
        properties = {"microsphere.dynamic.jdbc.modules.datasource.dynamic-context.close-delay=3s"})
@ComponentScan(basePackages = "io.microsphere.dynamic.jdbc.spring.boot.shardingsphere.examples.mybatis")
@EnableAutoConfiguration
public class ShardingSphereShardingDatabasesTest {

    @Autowired
    private List<ExampleService> exampleServices;

    @Autowired
    private ConfigurableApplicationContext context;

    @Autowired
    @Qualifier("myTransaction")
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private List<Interceptor> interceptors;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Test
    public void test() throws Throwable {

        assertEquals(DataSourceTransactionManager.class, platformTransactionManager.getClass());
        assertEquals(1, interceptors.size());
        assertEquals(PaginationInterceptor.class, interceptors.get(0).getClass());

        for (ExampleService exampleService : exampleServices) {
            ExampleExecuteTemplate.run(exampleService);
        }

        // context.publishEvent(new ConfigChangeEvent(singleton("microsphere.dynamic.jdbc.configs.sharding-databases")));

        Thread.sleep(10 * 1000);

        for (ExampleService exampleService : exampleServices) {
            ExampleExecuteTemplate.run(exampleService);
        }
    }
}
