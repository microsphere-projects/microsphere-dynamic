package io.microsphere.dynamic.jdbc.spring.boot.config;

import org.springframework.util.Assert;

/**
 * Abstract {@link ConfigPostProcessor} for Module
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class AbstractModuleConfigPostProcessor extends AbstractConfigPostProcessor implements ModuleCapable {

    @Override
    public final void postProcess(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName) {

        String module = getModule();

        Assert.notNull(module, () -> "getModule() method must not return null!");

        if (!supports(dynamicJdbcConfig, dynamicJdbcConfigPropertyName, module)) {
            logger.info("{} is not supported for DynamicJdbcConfig[name :{}] module[name : {}] ", getClass().getSimpleName(),
                    dynamicJdbcConfig.getName(), module);
            return;
        }

        postProcess(dynamicJdbcConfig, dynamicJdbcConfigPropertyName, module);
    }

    /**
     * Supports to post-process or not
     *
     * @param dynamicJdbcConfig {@link DynamicJdbcConfig}
     * @param dynamicJdbcConfigPropertyName the property name of {@link DynamicJdbcConfig}
     * @param module the module, e.g "datasource" , "transaction" and so on.
     * @return if supports, return <code>true</code> as default, or <code>false</code>
     */
    protected boolean supports(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName, String module) {
        return true;
    }

    /**
     * Post-Process {@link DynamicJdbcConfig} that may be modified in the specified implementation
     *
     * @param dynamicJdbcConfig {@link DynamicJdbcConfig}
     * @param dynamicJdbcConfigPropertyName the property name of {@link DynamicJdbcConfig}
     * @param module the module, e.g "datasource" , "transaction" and so on.
     */
    protected abstract void postProcess(DynamicJdbcConfig dynamicJdbcConfig, String dynamicJdbcConfigPropertyName, String module);


}
