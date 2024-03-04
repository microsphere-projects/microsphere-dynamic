package io.microsphere.dynamic.jdbc.spring.boot.transaction.context;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;
import io.microsphere.dynamic.jdbc.spring.boot.context.ParentContextBeanNameGenerator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * {@link ParentContextBeanNameGenerator} for {@link PlatformTransactionManager}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class PlatformTransactionManagerBeanNameGenerator implements ParentContextBeanNameGenerator<PlatformTransactionManager> {

    @Override
    public String generate(String childBeanName, PlatformTransactionManager platformTransactionManager, DynamicJdbcConfig dynamicJdbcConfig,
            ConfigurableApplicationContext childContext) {
        DynamicJdbcConfig.Transaction transaction = dynamicJdbcConfig.getTransaction();
        return transaction == null ? null : transaction.getName();
    }
}
