package io.microsphere.dynamic.jdbc.spring.boot.env;

import io.microsphere.spring.boot.env.DefaultPropertiesPostProcessor;
import org.springframework.core.Ordered;

import java.util.Set;

/**
 * Spring Boot Test Default Properties Processor
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see DefaultPropertiesPostProcessor
 * @since 1.0.0
 */
public class SpringBootTestDefaultPropertiesPostProcessor implements DefaultPropertiesPostProcessor, Ordered {

    @Override
    public void initializeResources(Set<String> defaultPropertiesResources) {
        defaultPropertiesResources.add("META-INF/spring-boot/test-default.properties");
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
