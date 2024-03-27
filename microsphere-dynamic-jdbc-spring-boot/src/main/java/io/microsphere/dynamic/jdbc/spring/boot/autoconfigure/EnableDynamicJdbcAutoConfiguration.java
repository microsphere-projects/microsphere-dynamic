package io.microsphere.dynamic.jdbc.spring.boot.autoconfigure;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enable Dynamic JDBC Auto-Configuration
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(DynamicJdbcAutoConfigurationImportSelector.class)
public @interface EnableDynamicJdbcAutoConfiguration {

    Class<?>[] exclude() default {}; // Always empty

    String[] excludeName() default {}; // Always empty
}
