package io.microsphere.dynamic.jdbc.spring.boot.test;

import java.util.List;

import io.microsphere.dynamic.jdbc.spring.boot.shardingsphere.examples.core.api.ExampleExecuteTemplate;
import io.microsphere.dynamic.jdbc.spring.boot.shardingsphere.examples.core.api.service.ExampleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * TestCase for Sharding Sphere on Sharding Read-Write Splitting
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShardingSphereShardingReadWriteSplittingTest.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("classpath:/META-INF/sharding-sphere/sharding-readwrite-splitting.properties")
@ComponentScan(basePackages = "io.microsphere.dynamic.jdbc.spring.boot.shardingsphere.examples.mybatis")
@EnableAutoConfiguration
public class ShardingSphereShardingReadWriteSplittingTest {

    @Autowired
    private List<ExampleService> exampleServices;

    @Test
    public void test() throws Throwable {
        for (ExampleService exampleService : exampleServices) {
            ExampleExecuteTemplate.run(exampleService);
        }
    }
}
