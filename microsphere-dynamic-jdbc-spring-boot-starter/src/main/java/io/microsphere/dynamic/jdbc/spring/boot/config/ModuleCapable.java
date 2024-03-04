package io.microsphere.dynamic.jdbc.spring.boot.config;

import org.springframework.lang.NonNull;

/**
 * The Capable interface of {@link DynamicJdbcConfig DynamicJdbcConfigs'} Module
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public interface ModuleCapable {

    @NonNull
    String getModule();
}
