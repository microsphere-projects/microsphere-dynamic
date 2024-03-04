package io.microsphere.dynamic.jdbc.spring.boot.context;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.multiple.active.zone.spring.event.ZoneContextChangedEvent;
import io.microsphere.spring.config.env.event.PropertySourcesChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Set;

import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.getDynamicJdbcConfig;


/**
 * An {@link ApplicationListener} listening on {@link PropertySourcesChangedEvent} or {@link ZoneContextChangedEvent}
 * to propagate the {@link DynamicJdbcConfigChangedEvent}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see PropertySourcesChangedEvent
 * @see ZoneContextChangedEvent
 * @see DynamicJdbcConfigChangedEvent
 * @since 1.0.0
 */
class PropagatingDynamicJdbcConfigChangedEventListener implements SmartApplicationListener {

    private static final Logger logger = LoggerFactory.getLogger(PropagatingDynamicJdbcConfigChangedEventListener.class);

    private final Set<String> dynamicJdbcConfigPropertyNames;

    private final ConfigurableApplicationContext context;

    private final ConfigurableEnvironment environment;

    public PropagatingDynamicJdbcConfigChangedEventListener(Set<String> dynamicJdbcConfigPropertyNames, ConfigurableApplicationContext context) {
        this.dynamicJdbcConfigPropertyNames = dynamicJdbcConfigPropertyNames;
        this.context = context;
        this.environment = context.getEnvironment();
        logger.info("Context[id : '{}'] associates the property names of DynamicJdbcConfig : {}", context.getId(), dynamicJdbcConfigPropertyNames);
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType.equals(PropertySourcesChangedEvent.class) || eventType.equals(ZoneContextChangedEvent.class);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        logger.info("Context[id : '{}'] receives event : {}", context.getId(), event);
        if (event instanceof PropertySourcesChangedEvent) {
            onPropertySourcesChangedEvent((PropertySourcesChangedEvent) event);
        } else if (event instanceof ZoneContextChangedEvent) {
            onZoneContextChangedEvent((ZoneContextChangedEvent) event);
        }
    }

    private void onPropertySourcesChangedEvent(PropertySourcesChangedEvent event) {
        Set<String> keys = event.getChangedProperties().keySet();
        for (String key : keys) {
            if (dynamicJdbcConfigPropertyNames.contains(key)) {
                logger.info("The key['{}'] of ConfigChangeEvent[context : '{}'] matches the property names of DynamicJdbcConfig : {}", key,
                        context.getId(), dynamicJdbcConfigPropertyNames);
                publishDynamicJdbcConfigChangedEvent(key);
            }
        }
    }

    private void onZoneContextChangedEvent(ZoneContextChangedEvent event) {
        if (isZoneChanged(event)) {
            // Publish DynamicJdbcConfigChangedEvent if the DynamicJdbcConfig has tge High Availability
            // DataSource
            dynamicJdbcConfigPropertyNames.forEach(propertyName -> {
                DynamicJdbcConfig dynamicJdbcConfig = getDynamicJdbcConfig(environment, propertyName);
                if (dynamicJdbcConfig.hasHighAvailabilityDataSource()) {
                    publishDynamicJdbcConfigChangedEvent(dynamicJdbcConfig, propertyName);
                }
            });
        }
    }

    private boolean isZoneChanged(ZoneContextChangedEvent event) {
        boolean zoneChanged = false;
        List<PropertyChangeEvent> propertyChangeEvents = event.getPropertyChangeEvents();
        for (PropertyChangeEvent propertyChangeEvent : propertyChangeEvents) {
            if ("zone".equals(propertyChangeEvent.getPropertyName())) {
                zoneChanged = true;
                break;
            }
        }
        return zoneChanged;
    }

    private void publishDynamicJdbcConfigChangedEvent(String propertyName) {
        DynamicJdbcConfig dynamicJdbcConfig = getDynamicJdbcConfig(environment, propertyName);
        publishDynamicJdbcConfigChangedEvent(dynamicJdbcConfig, propertyName);
    }

    private void publishDynamicJdbcConfigChangedEvent(DynamicJdbcConfig dynamicJdbcConfig, String propertyName) {
        context.publishEvent(new DynamicJdbcConfigChangedEvent(context, dynamicJdbcConfig, propertyName));
        logger.info("Context[id : '{}']  published a DynamicJdbcConfigChangedEvent[ property name : {}]", context.getId(), propertyName);
    }
}
