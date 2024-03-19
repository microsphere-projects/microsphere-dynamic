package io.microsphere.dynamic.jdbc.spring.boot.config.validation;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.dynamic.jdbc.spring.boot.constants.DynamicJdbcConstants;
import org.springframework.util.StringUtils;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * {@link ConfigValidator} for {@link DynamicJdbcConfig}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class DynamicJdbcConfigValidator extends AbstractConfigValidator {

    private static final List<String> modules = asList(
            DynamicJdbcConstants.DATASOURCE_MODULE,
            DynamicJdbcConstants.HIGH_AVAILABILITY_DATASOURCE_MODULE,
            DynamicJdbcConstants.TRANSACTION_MODULE,
            DynamicJdbcConstants.SHARDING_SPHERE_MODULE,
            DynamicJdbcConstants.MYBATIS_MODULE,
            DynamicJdbcConstants.MYBATIS_PLUS_MODULE
    );

    @Override
    public void validate(DynamicJdbcConfig config, String dynamicJdbcConfigPropertyName, ValidationErrors errors) {
        validateName(config, errors);
        validateModules(config, errors);
    }

    private void validateName(DynamicJdbcConfig name, ValidationErrors errors) {
        if (!StringUtils.hasText(name.getName())) {
            errors.addError("DynamicJdbcConfig must contain 'name' attribute");
        }
    }

    private void validateModules(DynamicJdbcConfig config, ValidationErrors errors) {
        if (!config.hasDataSource() && !config.hasHighAvailabilityDataSource() && !config.hasTransaction() && !config.hasShardingDataSource()
                && !config.hasMybatis() && !config.hasMybatisPlus()) {
            errors.addError("DynamicJdbcConfig[name : '{}'] must contain one of modules {}", config.getName(), modules);
        }
    }
}
