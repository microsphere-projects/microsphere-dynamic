package io.microsphere.dynamic.jdbc.spring.boot.context;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

/**
 * {@link DynamicJdbcConfig} Changed {@link ApplicationEvent Event}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class DynamicJdbcConfigChangedEvent extends ApplicationContextEvent {

    private final DynamicJdbcConfig dynamicJdbcConfig;

    private final String propertyName;

    /**
     * Create a new ApplicationEvent.
     * 
     * @param context
     * @param dynamicJdbcConfig
     * @param propertyName
     */
    public DynamicJdbcConfigChangedEvent(ConfigurableApplicationContext context, DynamicJdbcConfig dynamicJdbcConfig, String propertyName) {
        super(context);
        this.dynamicJdbcConfig = dynamicJdbcConfig;
        this.propertyName = propertyName;
    }

    @Override
    public ConfigurableApplicationContext getSource() {
        return (ConfigurableApplicationContext) super.getSource();
    }

    public DynamicJdbcConfig getDynamicJdbcConfig() {
        return dynamicJdbcConfig;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
