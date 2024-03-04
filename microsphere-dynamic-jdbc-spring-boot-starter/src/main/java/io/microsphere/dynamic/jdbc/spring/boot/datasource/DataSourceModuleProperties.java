package io.microsphere.dynamic.jdbc.spring.boot.datasource;

import io.microsphere.dynamic.jdbc.spring.boot.config.annotation.Module;
import io.microsphere.dynamic.jdbc.spring.boot.context.ModuleProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants.DATASOURCE_MODULE;
import static io.microsphere.dynamic.jdbc.spring.boot.datasource.constants.DataSourceConstants.DATA_SOURCE_MODULE_PROPERTY_NAME_PREFIX;

/**
 * {@link ModuleProperties} for {@link DataSource "data-source"}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
@Module(DATASOURCE_MODULE)
@ConfigurationProperties(prefix = DATA_SOURCE_MODULE_PROPERTY_NAME_PREFIX)
public class DataSourceModuleProperties extends ModuleProperties {

    private Map<String, List<String>> propertyNameAliases = new LinkedHashMap<>();

    private URL url = new URL();

    public Map<String, List<String>> getPropertyNameAliases() {
        return propertyNameAliases;
    }

    public void setPropertyNameAliases(Map<String, List<String>> propertyNameAliases) {
        this.propertyNameAliases = propertyNameAliases;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public static class URL {

        private MultiValueMap<String, String> defaultQueryParams = new LinkedMultiValueMap<>();

        public MultiValueMap<String, String> getDefaultQueryParams() {
            return defaultQueryParams;
        }

        public void setDefaultQueryParams(MultiValueMap<String, String> defaultQueryParams) {
            this.defaultQueryParams = defaultQueryParams;
        }
    }
}
