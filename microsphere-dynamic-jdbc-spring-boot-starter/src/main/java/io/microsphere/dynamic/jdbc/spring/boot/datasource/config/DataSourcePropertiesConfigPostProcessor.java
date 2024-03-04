package io.microsphere.dynamic.jdbc.spring.boot.datasource.config;

import io.microsphere.dynamic.jdbc.spring.boot.config.AbstractConfigPostProcessor;
import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.dynamic.jdbc.spring.boot.datasource.constants.DataSourceConstants;
import io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils;
import io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcPropertyUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * {@link Map DataSource Properties} Post-Processor
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class DataSourcePropertiesConfigPostProcessor extends AbstractConfigPostProcessor
        implements InitializingBean, BeanClassLoaderAware, EnvironmentAware {

    private JdbcURLAssembler jdbcURLAssembler;

    private Map<String, String> defaultDataSourceProperties;

    private static <T> void validateDuplicated(T value, T otherValue, String messageFormat, Object... args) {
        Assert.state(!Objects.equals(value, otherValue), () -> String.format(messageFormat, args));
    }

    @Override
    public void postProcess(DynamicJdbcConfig dynamicJdbcConfig, String shardingJdbcConfigPropertyName) {

        List<Map<String, String>> dataSourcePropertiesList = dynamicJdbcConfig.getDataSourcePropertiesList();

        int size = dataSourcePropertiesList == null ? 0 : dataSourcePropertiesList.size();

        if (size < 1) {
            logger.warn("There is not any DataSource config in DynamicJdbcConfig[beanName : {}] : {}", shardingJdbcConfigPropertyName,
                    dynamicJdbcConfig);
            return;
        }

        for (int i = 0; i < size; i++) {
            Map<String, String> dataSourceProperties = dataSourcePropertiesList.get(i);
            // previousDataSourceProperties will be used to copy its properties to target DataSource
            Map<String, String> previousDataSourceProperties = getPreviousDataSourceProperties(dataSourcePropertiesList, i);
            processDataSourceProperties(dataSourceProperties, previousDataSourceProperties, dynamicJdbcConfig, shardingJdbcConfigPropertyName, i);
        }
    }

    private Map<String, String> getPreviousDataSourceProperties(List<Map<String, String>> dataSourcePropertiesList, int index) {
        Map<String, String> previousDataSource = index < 1 ? defaultDataSourceProperties : dataSourcePropertiesList.get(index - 1);
        // Mutable Map
        return new HashMap<>(previousDataSource);
    }

    private void processDataSourceProperties(Map<String, String> dataSourceProperties, Map<String, String> previousDataSourceProperties,
                                             DynamicJdbcConfig shardingJdbcConfig, String shardingJdbcConfigPropertyName, int index) {
        processDataSourceName(dataSourceProperties, previousDataSourceProperties, shardingJdbcConfigPropertyName, index);
        processDataSourceType(dataSourceProperties, previousDataSourceProperties);
        String jdbcURL = processDataSourceUrl(dataSourceProperties);
        processDataSourceDriverClassName(dataSourceProperties, previousDataSourceProperties, jdbcURL);
        processDataSourceUserName(dataSourceProperties, previousDataSourceProperties);
        processDataSourcePassword(dataSourceProperties, previousDataSourceProperties);
        processDataSourceOthers(dataSourceProperties, previousDataSourceProperties);
    }

    private void processDataSourceName(Map<String, String> dataSourceProperties, Map<String, String> previousDataSourceProperties,
                                       String shardingJdbcConfigPropertyName, int index) {

        String propertyName = DataSourceConstants.NAME_PROPERTY_NAME;

        setPropertyIfAbsent(dataSourceProperties, () -> shardingJdbcConfigPropertyName + "-datasource-" + index, propertyName);
    }

    private void processDataSourceType(Map<String, String> dataSourceProperties, Map<String, String> previousDataSourceProperties) {
        String propertyName = DataSourceConstants.TYPE_PROPERTY_NAME;
        setPropertyIfAbsent(dataSourceProperties, previousDataSourceProperties, propertyName);
    }

    private String processDataSourceUrl(Map<String, String> dataSourceProperties) {
        Map.Entry<String, String> dataSourceUrlEntry = DynamicJdbcConfigUtils.getDataSourceUrlEntry(dataSourceProperties);
        String propertyName = dataSourceUrlEntry.getKey();
        String jdbcURL = dataSourceUrlEntry.getValue();

        jdbcURL = jdbcURLAssembler.assemble(jdbcURL);

        dataSourceProperties.put(propertyName, jdbcURL);

        return jdbcURL;
    }

    private void processDataSourceDriverClassName(Map<String, String> dataSourceProperties, Map<String, String> previousDataSourceProperties,
                                                  String jdbcURL) {
        String propertyName = DataSourceConstants.DRIVER_CLASS_NAME_PROPERTY_NAME;

        setPropertyIfAbsent(dataSourceProperties, previousDataSourceProperties, propertyName);

        String driverClassName = dataSourceProperties.get(propertyName);

        if (!StringUtils.hasText(driverClassName)) {
            driverClassName = deduceDriverClassName(jdbcURL);
            if (driverClassName != null) {
                dataSourceProperties.put(propertyName, driverClassName);
            }
        }
    }

    private String deduceDriverClassName(String jdbcURL) {
        String driverClassName = DatabaseDriver.fromJdbcUrl(jdbcURL).getDriverClassName();
        return driverClassName;
    }

    private void processDataSourceUserName(Map<String, String> dataSourceProperties, Map<String, String> previousDataSourceProperties) {
        String propertyName = DataSourceConstants.USER_NAME_PROPERTY_NAME;
        setPropertyIfAbsent(dataSourceProperties, previousDataSourceProperties, propertyName);

        // No user-name set, used default value
        if (!dataSourceProperties.containsKey(propertyName)) {
            dataSourceProperties.put(propertyName, DynamicJdbcPropertyUtils.getDataSourceDefaultUserName(environment));
        }
    }

    private void processDataSourcePassword(Map<String, String> dataSourceProperties, Map<String, String> previousDataSourceProperties) {
        String propertyName = DataSourceConstants.PASSWORD_NAME_PROPERTY_NAME;
        setPropertyIfAbsent(dataSourceProperties, previousDataSourceProperties, propertyName);

        // No password set, used default value
        if (!dataSourceProperties.containsKey(propertyName)) {
            dataSourceProperties.put(propertyName, DynamicJdbcPropertyUtils.getDataSourceDefaultPassword(environment));
        }
    }

    private void processDataSourceOthers(Map<String, String> dataSourceProperties, Map<String, String> previousDataSourceProperties) {
        // Remaining properties just to copy
        previousDataSourceProperties.forEach((key, value) -> setPropertyIfAbsent(dataSourceProperties, () -> value, key));
    }

    @Override
    public void setEnvironment(Environment environment) {
        super.setEnvironment(environment);
        jdbcURLAssembler = new JdbcURLAssembler((ConfigurableEnvironment) environment);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.defaultDataSourceProperties = initDefaultDataSourceProperties();
    }

    private void setPropertyIfAbsent(Map<String, String> dataSourceProperties, Map<String, String> previousDataSourceProperties, String key) {
        // remove the property from previous DataSource Properties via key
        setPropertyIfAbsent(dataSourceProperties, () -> previousDataSourceProperties.remove(key), key);
    }

    private void setPropertyIfAbsent(Map<String, String> dataSourceProperties, Supplier<String> valueSupplier, String key) {
        String value = dataSourceProperties.get(key);
        if (value == null) {
            String newValue = valueSupplier.get();
            if (newValue != null) {
                dataSourceProperties.put(key, newValue);
            }
        }
    }

    /**
     * Initialize {@link Map<String, String>} with default value
     *
     * @return {@link Map<String, String>}
     */
    private Map<String, String> initDefaultDataSourceProperties() {
        Map<String, String> dataSourceProperties = new HashMap<>();
        dataSourceProperties.put(DataSourceConstants.TYPE_PROPERTY_NAME, DataSourceConstants.DEFAULT_DATASOURCE_TYPE_NAME);
        // dataSourceProperties.put(DRIVER_CLASS_NAME_PROPERTY_NAME, DEFAULT_DRIVER_CLASS_NAME);
        return dataSourceProperties;
    }
}