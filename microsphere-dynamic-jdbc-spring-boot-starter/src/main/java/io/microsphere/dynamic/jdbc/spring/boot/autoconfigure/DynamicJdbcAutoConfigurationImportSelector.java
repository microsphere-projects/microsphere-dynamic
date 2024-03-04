package io.microsphere.dynamic.jdbc.spring.boot.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationImportSelector;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;

import static io.microsphere.dynamic.jdbc.spring.boot.autoconfigure.DynamicJdbcAutoConfigurationRepository.getAutoConfigurationClassNames;

/**
 * {@link ImportSelector} implementation imports the Auto-Configuration classes that the Dynamic JDBC requires
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 * @see DynamicJdbcAutoConfigurationRepository
 * @see DynamicJdbcAutoConfigurationImportFilter
 * @see DynamicJdbcAutoConfigurationImportListener
 * 
 */
class DynamicJdbcAutoConfigurationImportSelector extends AutoConfigurationImportSelector implements ImportSelector, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(DynamicJdbcAutoConfigurationImportSelector.class);

    private static final List<AutoConfigurationImportFilter> FILTERS = Arrays.asList(DynamicJdbcAutoConfigurationImportFilter.INSTANCE);

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        ClassLoader classLoader = getBeanClassLoader();
        String[] autoConfigurationClassNames = getAutoConfigurationClassNames(classLoader);

        if (ObjectUtils.isEmpty(autoConfigurationClassNames)) {
            logger.debug("The cache of Dynamic JDBC Auto-Configuration Class names is missing, attempt to reselect...");
            // Or execute AutoConfigurationImportSelector#selectImports,
            // it indicates that DynamicJdbcAutoConfigurationImportListener will be processed,
            // adn than DynamicJdbcAutoConfigurationRepository will be refreshed
            super.selectImports(annotationMetadata);
            // Get the cached result again
            autoConfigurationClassNames = getAutoConfigurationClassNames(classLoader);
        } else {
            // Return the cached result after listening on DynamicJdbcAutoConfigurationImportListener
            return autoConfigurationClassNames;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("JDBC Auto-Configuration Class names : {}", Arrays.asList(autoConfigurationClassNames));
        }

        return autoConfigurationClassNames;
    }

    protected Class<?> getAnnotationClass() {
        return EnableDynamicJdbcAutoConfiguration.class;
    }

    protected List<AutoConfigurationImportFilter> getAutoConfigurationImportFilters() {
        return FILTERS;
    }

    @Override
    public void destroy() throws Exception {
        // Clear DynamicJdbcAutoConfigurationRepository
        DynamicJdbcAutoConfigurationRepository.clear();
    }
}
