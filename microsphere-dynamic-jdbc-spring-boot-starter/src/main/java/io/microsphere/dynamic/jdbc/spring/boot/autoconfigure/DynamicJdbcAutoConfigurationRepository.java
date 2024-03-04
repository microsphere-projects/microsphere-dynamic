package io.microsphere.dynamic.jdbc.spring.boot.autoconfigure;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import static io.microsphere.spring.util.BeanUtils.invokeBeanInterfaces;
import static java.util.Arrays.deepToString;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;
import static org.apache.commons.lang3.ArrayUtils.EMPTY_STRING_ARRAY;

/**
 * The Repository of Dynamic JDBC Class names of Auto-Configuration by {@link EnableAutoConfiguration @EnableAutoConfiguration}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class DynamicJdbcAutoConfigurationRepository {

    private static final Logger logger = LoggerFactory.getLogger(DynamicJdbcAutoConfigurationRepository.class);

    private static final Map<ClassLoader, String[]> cache = new ConcurrentHashMap<>(1);

    static boolean isCached(ClassLoader classLoader) {
        boolean cached = cache.containsKey(classLoader);
        logger.debug("JDBC Auto-Configuration Class Names cached : {}", cached);
        return cached;
    }

    static void cache(ClassLoader classLoader, Collection<String> autoConfigurationClassNames) {
        cache(classLoader, autoConfigurationClassNames.toArray(new String[autoConfigurationClassNames.size()]));
    }

    static void cache(ClassLoader classLoader, String[] autoConfigurationClassNames) {
        if (!ObjectUtils.isEmpty(autoConfigurationClassNames)) {
            cache.put(classLoader, autoConfigurationClassNames);
            logger.debug("Cache Auto-Configuration Class Names [{}] under ClassLoader : {}", deepToString(autoConfigurationClassNames), classLoader);
        }
    }

    static String[] getAutoConfigurationClassNames(ClassLoader classLoader) {
        String[] autoConfigurationClassNames = cache.get(classLoader);
        if (autoConfigurationClassNames == null) {
            logger.debug("No Auto-Configuration Class Name was cached under ClassLoader : {}", classLoader);
            autoConfigurationClassNames = EMPTY_STRING_ARRAY;
        } else {
            logger.debug("Auto-Configuration Class Names [{}] was found under ClassLoader : {}", deepToString(autoConfigurationClassNames),
                    classLoader);
        }
        return autoConfigurationClassNames;
    }

    public static String[] getAutoConfigurationClassNames(ConfigurableApplicationContext context) {
        ClassLoader classLoader = context.getClassLoader();
        String[] autoConfigurationClassNames = getAutoConfigurationClassNames(classLoader);
        if (ObjectUtils.isEmpty(autoConfigurationClassNames)) {
            autoConfigurationClassNames = loadAutoConfigurationClassNames(context);
        }

        return autoConfigurationClassNames;
    }

    private static String[] loadAutoConfigurationClassNames(ConfigurableApplicationContext context) {
        DynamicJdbcAutoConfigurationImportSelector importSelector = new DynamicJdbcAutoConfigurationImportSelector();
        invokeBeanInterfaces(importSelector, context);
        return importSelector.selectImports(new StandardAnnotationMetadata(LoadingConfiguration.class));
    }

    public static Set<String> getAutoConfigurationClassNames(ConfigurableApplicationContext context, Collection<String> classPrefixes) {

        if (CollectionUtils.isEmpty(classPrefixes)) {
            return emptySet();
        }

        Set<String> autoConfigurationClassNames = new TreeSet<>();

        for (String autoConfigurationClassName : getAutoConfigurationClassNames(context)) {
            for (String classPrefix : classPrefixes) {
                if (StringUtils.startsWith(autoConfigurationClassName, classPrefix)) {
                    autoConfigurationClassNames.add(autoConfigurationClassName);
                    break;
                }
            }
        }

        return unmodifiableSet(autoConfigurationClassNames);
    }

    static void clear() {
        cache.clear();
    }

    @EnableDynamicJdbcAutoConfiguration
    private static class LoadingConfiguration {

    }
}
