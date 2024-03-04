package io.microsphere.dynamic.jdbc.spring.boot.config;

import io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils;
import io.microsphere.dynamic.jdbc.spring.boot.util.FunctionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;

import static io.microsphere.spring.util.BeanUtils.invokeAwareInterfaces;

/**
 * {@link DynamicJdbcConfig} {@link BeanPostProcessor}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class DynamicJdbcConfigPostProcessor extends AbstractConfigPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DynamicJdbcConfigPostProcessor.class);

    @Override
    public void postProcess(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName) {
        processDynamicJdbcConfig(dynamicJdbcConfig, dynamicJdbcConfigPropertyName);
    }

    private void processDynamicJdbcConfig(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName) {
        processName(dynamicJdbcConfig, dynamicJdbcConfigPropertyName);
        invokeAwareInterfaces(dynamicJdbcConfig, context);
    }

    private void processName(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName) {
        String name = dynamicJdbcConfig.getName();
        logger.debug("The original name of DynamicJdbcConfig : {} , property name : {}", name, dynamicJdbcConfigPropertyName);
        FunctionUtils.setValueIfAbsent(name, () -> DynamicJdbcConfigUtils.getDynamicJdbcConfigPropertyNameSuffix(dynamicJdbcConfigPropertyName), dynamicJdbcConfig::setName);
        logger.debug("The processed name of DynamicJdbcConfig : {}", dynamicJdbcConfig.getName());
    }
}
