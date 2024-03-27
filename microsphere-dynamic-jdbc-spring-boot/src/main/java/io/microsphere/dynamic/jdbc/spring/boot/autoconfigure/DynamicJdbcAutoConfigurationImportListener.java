package io.microsphere.dynamic.jdbc.spring.boot.autoconfigure;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.autoconfigure.AutoConfigurationImportEvent;
import org.springframework.boot.autoconfigure.AutoConfigurationImportListener;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * Dynamic JDBC {@link AutoConfigurationImportListener} uses
 * {@link DynamicJdbcAutoConfigurationImportFilter} to reduce the duplicated loading calculation of
 * {@link DynamicJdbcAutoConfigurationImportSelector}.
 * 
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see DynamicJdbcAutoConfigurationImportFilter
 * @see DynamicJdbcAutoConfigurationImportSelector
 * @see DynamicJdbcAutoConfigurationRepository
 * @see EnableAutoConfiguration
 * @since 1.0.0
 */
public class DynamicJdbcAutoConfigurationImportListener implements AutoConfigurationImportListener, EnvironmentAware, BeanClassLoaderAware {

    private static final DynamicJdbcAutoConfigurationImportFilter filter = DynamicJdbcAutoConfigurationImportFilter.INSTANCE;

    private ClassLoader classLoader;

    private ConfigurableEnvironment environment;

    @Override
    public void onAutoConfigurationImportEvent(AutoConfigurationImportEvent event) {

        if (DynamicJdbcAutoConfigurationRepository.isCached(classLoader)) {
            return;
        }

        List<String> configurationClassNames = event.getCandidateConfigurations();
        String[] matchedConfigurationClassNames = configurationClassNames.stream().filter(filter::match).toArray(String[]::new);
        // Cache the Dynamic JDBC Auto-Configuration Class Names
        DynamicJdbcAutoConfigurationRepository.cache(classLoader, matchedConfigurationClassNames);
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
        filter.setEnvironment(environment);
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
