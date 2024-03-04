package io.microsphere.dynamic.jdbc.spring.boot.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.microsphere.dynamic.jdbc.spring.boot.config.annotation.Module;
import io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants;
import io.microsphere.dynamic.jdbc.spring.boot.datasource.DynamicDataSource;
import io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils;
import io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcUtils;
import io.microsphere.multiple.active.zone.ZoneContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.autoconfigure.transaction.PlatformTransactionManagerCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_ZONE;
import static io.microsphere.multiple.active.zone.spring.ZoneUtils.ZONE_CONTEXT_BEAN_NAME;
import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * Dynamic JDBC Config
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class DynamicJdbcConfig implements BeanFactoryAware {

    private static final String[] EXCLUDE_FIELDS =
            {"dataSourcePropertiesList", "highAvailabilityDataSourcePropertiesMap"};

    private @Nullable String name;

    /**
     * The flag indicates that current {@link DynamicJdbcConfig}'s {@link ApplicationContext} beans are dynamic or not.
     * <p>
     * For now, it only supports {@link DataSource} bean was wrapped to be {@link DynamicDataSource} instance
     */
    private boolean dynamic = true;

    /**
     * The flag indicates that current {@link DynamicJdbcConfig}'s {@link ApplicationContext} beans are primary or not.
     * <p>
     * For now, it only supports {@link DataSource} bean
     */
    private boolean primary = false;

    /**
     * General datasource (easy scenario)
     */
    @JsonProperty(DynamicJdbcConstants.DATASOURCE_MODULE)
    private @Nullable List<Map<String, Object>> dataSource;

    @JsonIgnore
    private List<Map<String, String>> dataSourcePropertiesList;

    /**
     * High Availability datasource (HA scenario) with the availability zone as Key and the properties List as value
     */
    @JsonProperty(DynamicJdbcConstants.HIGH_AVAILABILITY_DATASOURCE_MODULE)
    private @Nullable Map<String, List<Map<String, Object>>> highAvailabilityDataSource;

    @JsonIgnore
    private Map<String, List<Map<String, String>>> highAvailabilityDataSourcePropertiesMap;

    @JsonProperty(DynamicJdbcConstants.TRANSACTION_MODULE)
    private @Nullable Transaction transaction;

    @JsonProperty(DynamicJdbcConstants.SHARDING_SPHERE_MODULE)
    private @Nullable ShardingSphere shardingSphere;

    @JsonProperty(DynamicJdbcConstants.MYBATIS_MODULE)
    private @Nullable Mybatis mybatis;

    @JsonProperty(DynamicJdbcConstants.MYBATIS_PLUS_MODULE)
    private @Nullable MybatisPlus mybatisPlus;

    @JsonIgnore
    private BeanFactory beanFactory;

    @JsonIgnore
    private ZoneContext zoneContext;

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    @Nullable
    public List<Map<String, Object>> getDataSource() {
        return dataSource;
    }

    public void setDataSource(@Nullable List<Map<String, Object>> dataSource) {
        this.dataSource = dataSource;
        this.dataSourcePropertiesList = DynamicJdbcUtils.flatPropertiesList(dataSource);
    }

    @Nullable
    public Map<String, List<Map<String, Object>>> getHighAvailabilityDataSource() {
        return highAvailabilityDataSource;
    }

    public void
    setHighAvailabilityDataSource(@Nullable Map<String, List<Map<String, Object>>> highAvailabilityDataSource) {
        this.highAvailabilityDataSource = highAvailabilityDataSource;
        this.highAvailabilityDataSourcePropertiesMap = DynamicJdbcUtils.flatPropertiesMap(highAvailabilityDataSource);
    }

    @Nullable
    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(@Nullable Transaction transaction) {
        this.transaction = transaction;
    }

    @Nullable
    public DynamicJdbcConfig.ShardingSphere getShardingSphere() {
        return shardingSphere;
    }

    public void setShardingSphere(@Nullable DynamicJdbcConfig.ShardingSphere shardingSphere) {
        this.shardingSphere = shardingSphere;
    }

    @Nullable
    public Mybatis getMybatis() {
        return mybatis;
    }

    public void setMybatis(@Nullable Mybatis mybatis) {
        this.mybatis = mybatis;
    }

    @Nullable
    public MybatisPlus getMybatisPlus() {
        return mybatisPlus;
    }

    public void setMybatisPlus(@Nullable MybatisPlus mybatisPlus) {
        this.mybatisPlus = mybatisPlus;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    // Extension methods (not for JSON)

    @JsonIgnore
    public int getDataSourceSize() {
        return dataSource == null ? 0 : dataSource.size();
    }

    @JsonIgnore
    public int getHighAvailabilityDataSourceZoneSize() {
        return highAvailabilityDataSource == null ? 0 : highAvailabilityDataSource.size();
    }

    @JsonIgnore
    public boolean hasDataSource() {
        return getDataSourceSize() > 0;
    }

    @JsonIgnore
    public boolean hasOnlySingleDataSource() {
        return getDataSourcePropertiesList().size() == 1;
    }

    @JsonIgnore
    public boolean hasHighAvailabilityDataSource() {
        return getHighAvailabilityDataSourceZoneSize() > 0;
    }

    @JsonIgnore
    public boolean hasTransaction() {
        return transaction != null;
    }

    @JsonIgnore
    public boolean hasShardingDataSource() {
        // Maybe will add more sharding providers
        // Now, just Dynamic Sphere works
        return shardingSphere != null;
    }

    @JsonIgnore
    public boolean hasMybatis() {
        return mybatis != null;
    }

    @JsonIgnore
    public boolean hasMybatisPlus() {
        return mybatisPlus != null;
    }

    @JsonIgnore
    public final List<Map<String, String>> getDataSourcePropertiesList() {
        final List<Map<String, String>> dataSourcePropertiesList;
        if (hasHighAvailabilityDataSource()) {
            ZoneContext zoneContext = getZoneContext();
            String zone = zoneContext.getZone();
            dataSourcePropertiesList = highAvailabilityDataSourcePropertiesMap.get(zone);
            if (CollectionUtils.isEmpty(dataSourcePropertiesList)) {
                // If the datasource properties list was not found in the specified zone, take the "defaultZone"
                // result
                return highAvailabilityDataSourcePropertiesMap.get(DEFAULT_ZONE);
            }
        } else {
            dataSourcePropertiesList = this.dataSourcePropertiesList;
        }
        return dataSourcePropertiesList;
    }

    @JsonIgnore
    public Map<String, Map<String, String>> getDataSourcePropertiesMap() {
        if (!hasDataSource() && !hasHighAvailabilityDataSource()) {
            return emptyMap();
        }
        List<Map<String, String>> dataSourcePropertiesList = getDataSourcePropertiesList();
        int size = dataSourcePropertiesList.size();
        Map<String, Map<String, String>> dataSourceMap = new LinkedHashMap<>(size);
        for (int i = 0; i < size; i++) {
            Map<String, String> dataSourceProperties = dataSourcePropertiesList.get(i);
            String name = DynamicJdbcConfigUtils.getDataSourceName(dataSourceProperties);
            dataSourceMap.put(name, dataSourceProperties);
        }
        return unmodifiableMap(dataSourceMap);
    }

    @Override
    public boolean equals(Object obj) {
        // Cost a little performance
        return reflectionEquals(this, obj, EXCLUDE_FIELDS);
    }

    @Override
    public int hashCode() {
        // Cost a little performance
        return reflectionHashCode(this, EXCLUDE_FIELDS);
    }

    @Override
    public String toString() {
        return "DynamicJdbcConfig[" + name + "]";
    }

    @JsonIgnore
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @JsonIgnore
    public @NonNull ZoneContext getZoneContext() {
        if (zoneContext == null) {
            if (beanFactory != null && beanFactory.containsBean(ZONE_CONTEXT_BEAN_NAME)) {
                zoneContext = beanFactory.getBean(ZONE_CONTEXT_BEAN_NAME, ZoneContext.class);
            } else {
                zoneContext = ZoneContext.get();
            }
        }
        return zoneContext;
    }

    /**
     * Dynamic JDBC Configuration base class
     */
    public static abstract class Config {

        /**
         * The Bean name of Configuration
         */
        private @Nullable String name;

        /**
         * The individual Configuration class names of module
         */
        private @Nullable String configurations;

        /**
         * The properties of Configuration
         */
        private @Nullable Map<String, Object> properties;

        @Nullable
        public String getName() {
            return name;
        }

        public void setName(@Nullable String name) {
            this.name = name;
        }

        @Nullable
        public String getConfigurations() {
            return configurations;
        }

        public void setConfigurations(@Nullable String configurations) {
            this.configurations = configurations;
        }

        @Nullable
        public Map<String, Object> getProperties() {
            return properties;
        }

        public void setProperties(@Nullable Map<String, Object> properties) {
            this.properties = properties;
        }

        @Override
        public boolean equals(Object obj) {
            // Cost a little performance
            return reflectionEquals(this, obj);
        }

        @Override
        public int hashCode() {
            // Cost a little performance
            return reflectionHashCode(this);
        }

        @Override
        public String toString() {
            // Cost a little performance
            return reflectionToString(this, SHORT_PREFIX_STYLE);
        }
    }

    /**
     * Transaction Config
     */
    @Module(DynamicJdbcConstants.TRANSACTION_MODULE)
    public static class Transaction extends Config {

        /**
         * The class names of {@link PlatformTransactionManagerCustomizer}
         */
        private @Nullable String customizers;

        @Nullable
        public String getCustomizers() {
            return customizers;
        }

        public void setCustomizers(@Nullable String customizers) {
            this.customizers = customizers;
        }
    }

    /**
     * ShardingSphere Config
     */
    @Module(DynamicJdbcConstants.SHARDING_SPHERE_MODULE)
    public static class ShardingSphere extends Config {

        @JsonProperty("config-resource")
        private @NonNull String configResource;

        @NonNull
        public String getConfigResource() {
            return configResource;
        }

        public void setConfigResource(@NonNull String configResource) {
            this.configResource = configResource;
        }

    }

    /**
     * Mybatis Config
     */
    @Module(DynamicJdbcConstants.MYBATIS_MODULE)
    public static class Mybatis extends Config {

        @JsonProperty("base-packages")
        private @Nullable String basePackages;

        @Nullable
        public String getBasePackages() {
            return basePackages;
        }

        public void setBasePackages(@Nullable String basePackages) {
            this.basePackages = basePackages;
        }
    }

    /**
     * Mybatis Config
     */
    @Module(DynamicJdbcConstants.MYBATIS_PLUS_MODULE)
    public static class MybatisPlus extends Config {

        @JsonProperty("base-packages")
        private String basePackages;

        public String getBasePackages() {
            return basePackages;
        }

        public void setBasePackages(String basePackages) {
            this.basePackages = basePackages;
        }
    }

}
