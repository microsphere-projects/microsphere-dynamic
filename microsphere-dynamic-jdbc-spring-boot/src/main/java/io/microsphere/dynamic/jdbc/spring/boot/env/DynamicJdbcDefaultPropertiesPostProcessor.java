package io.microsphere.dynamic.jdbc.spring.boot.env;

import io.microsphere.spring.boot.env.DefaultPropertiesPostProcessor;

import java.util.Set;

import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.DEFAULT_PROPERTIES_LOCATION;

/**
 * Dynamic JDBC {@link DefaultPropertiesPostProcessor}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see DefaultPropertiesPostProcessor
 * @since 1.0.0
 */
public class DynamicJdbcDefaultPropertiesPostProcessor implements DefaultPropertiesPostProcessor {

    @Override
    public void initializeResources(Set<String> defaultPropertiesResources) {
        defaultPropertiesResources.add(DEFAULT_PROPERTIES_LOCATION);
    }
}
