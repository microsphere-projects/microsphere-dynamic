package io.microsphere.dynamic.jdbc.spring.boot.mybatisplus.context;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.EnumTypeHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.util.HashSet;
import java.util.Set;

import static io.microsphere.dynamic.jdbc.spring.boot.mybatisplus.constants.MybatisPlusConstants.DYNAMIC_JDBC_MYBATIS_PLUS_BASE_PACKAGES_PLACEHOLDER;
import static org.springframework.util.StringUtils.tokenizeToStringArray;

/**
 * The Configuration class to register the annotated {@link Mapper} interfaces under
 * {@link DynamicJdbcConfig.MybatisPlus#getBasePackages()}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
@MapperScan(basePackages = DYNAMIC_JDBC_MYBATIS_PLUS_BASE_PACKAGES_PLACEHOLDER)
class MybatisPlusMapperScanConfiguration {

    private static final ResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();

    private static final MetadataReaderFactory METADATA_READER_FACTORY = new CachingMetadataReaderFactory(RESOURCE_PATTERN_RESOLVER);

    private static final String MYBATIS_TYPE_HANDLERS_PACKAGE_VALUE = "io.microsphere.dynamic.jdbc.spring.boot.mybatisplus.handle";

    @Bean
    public ConfigurationCustomizer configurationCustomizer(MybatisPlusProperties mybatisProperties, DynamicJdbcConfig dynamicJdbcConfig) {

        return new ConfigurationCustomizer() {

            @Override
            public void customize(Configuration configuration) {
                String typeHandlersPackage = mybatisProperties.getTypeHandlersPackage();
                String typeEnumsPackage = mybatisProperties.getTypeEnumsPackage();
                if (StringUtils.isEmpty(typeEnumsPackage)) {
                    DynamicJdbcConfig.MybatisPlus mybatisPlus = dynamicJdbcConfig.getMybatisPlus();
                    typeEnumsPackage = mybatisPlus.getBasePackages();
                }
                // 如果包含了架构组的扩展Handle
                if (StringUtils.isNotEmpty(typeHandlersPackage) && StringUtils.contains(typeHandlersPackage, MYBATIS_TYPE_HANDLERS_PACKAGE_VALUE)
                        && StringUtils.isNotEmpty(typeEnumsPackage)) {
                    Set<Class> classes;
                    if (typeEnumsPackage.contains(StringPool.STAR) && !typeEnumsPackage.contains(StringPool.COMMA)
                            && !typeEnumsPackage.contains(StringPool.SEMICOLON)) {
                        classes = scanTypePackage(typeEnumsPackage);
                    } else {
                        String[] typeHandlersPackageArray =
                                tokenizeToStringArray(typeEnumsPackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
                        classes = new HashSet<>();
                        for (String typePackage : typeHandlersPackageArray) {
                            Set<Class> scanTypePackage = scanTypePackage(typePackage);
                            if (scanTypePackage.isEmpty()) {
                            } else {
                                classes.addAll(scanTypePackage);
                            }
                        }
                    }
                    for (Class cls : classes) {
                        if (cls.isEnum()) {
                            configuration.getTypeHandlerRegistry().register(cls, EnumTypeHandler.class);
                        }
                    }
                }
            }

        };
    }

    private Set<Class> scanTypePackage(String typePackage) {
        String pkg = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(typePackage) + "/**/*.class";
        try {
            Set<Class> set = new HashSet<>();
            Resource[] resources = RESOURCE_PATTERN_RESOLVER.getResources(pkg);
            if (resources != null && resources.length > 0) {
                MetadataReader metadataReader;
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        metadataReader = METADATA_READER_FACTORY.getMetadataReader(resource);
                        set.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
                    }
                }
            }
            return set;
        } catch (Exception e) {
            throw ExceptionUtils.mpe("not find scanTypePackage: %s", e, pkg);
        }
    }
}
