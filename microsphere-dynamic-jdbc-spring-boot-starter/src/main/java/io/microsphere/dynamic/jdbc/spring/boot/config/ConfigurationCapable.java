package io.microsphere.dynamic.jdbc.spring.boot.config;

import io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils;
import org.springframework.util.ReflectionUtils;

import static io.microsphere.reflect.TypeUtils.resolveActualTypeArgumentClass;


/**
 * The Capable interface of {@link DynamicJdbcConfig.Config}
 *
 * @param <C> the type of {@link DynamicJdbcConfig.Config}
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public interface ConfigurationCapable<C extends DynamicJdbcConfig.Config> extends ModuleCapable {

    default Class<C> getConfigurationClass() {
        return resolveActualTypeArgumentClass(getClass(), ConfigurationCapable.class, 0);
    }

    default C getConfiguration(DynamicJdbcConfig dynamicJdbcConfig) {
        Object[] container = new Object[1];
        ReflectionUtils.doWithMethods(DynamicJdbcConfig.class, method -> {
            ReflectionUtils.makeAccessible(method);
            container[0] = ReflectionUtils.invokeMethod(method, dynamicJdbcConfig);
        }, method -> method.getName().startsWith("get") && method.getParameterCount() == 0 && method.getReturnType().equals(getConfigurationClass()));

        return (C) container[0];
    }

    default String getModule() {
        return DynamicJdbcConfigUtils.getModule(getConfigurationClass());
    }

}
