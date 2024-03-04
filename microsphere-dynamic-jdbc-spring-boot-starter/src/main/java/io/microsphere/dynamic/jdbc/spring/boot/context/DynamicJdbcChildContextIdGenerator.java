package io.microsphere.dynamic.jdbc.spring.boot.context;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import org.springframework.context.ConfigurableApplicationContext;

import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.generateDynamicJdbcChildContextId;

/**
 * The ID Generator of {@link DynamicJdbcChildContext}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public interface DynamicJdbcChildContextIdGenerator {

    DynamicJdbcChildContextIdGenerator DEFAULT = new DynamicJdbcChildContextIdGenerator() {};

    default String generate(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName,
                            ConfigurableApplicationContext parentContext) {
        return generateDynamicJdbcChildContextId(dynamicJdbcConfig);
    }



}
