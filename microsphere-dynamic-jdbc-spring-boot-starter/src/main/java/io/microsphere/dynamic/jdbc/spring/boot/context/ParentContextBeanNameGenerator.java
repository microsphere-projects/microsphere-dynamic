package io.microsphere.dynamic.jdbc.spring.boot.context;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * Dynamic JDBC Parent Context(main) Bean Name Generator
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 * @param <CB> the type of child bean
 */
public interface ParentContextBeanNameGenerator<CB> {

    @Nullable
    String generate(String childBeanName, CB childBean, DynamicJdbcConfig dynamicJdbcConfig, ConfigurableApplicationContext childContext);

    /**
     * @return Get the type of child bean
     */
    default Class<CB> getChildBeanType() {
        return (Class<CB>) ResolvableType.forClass(getClass()).getInterfaces()[0].getGeneric(0).resolve();
    }

}
