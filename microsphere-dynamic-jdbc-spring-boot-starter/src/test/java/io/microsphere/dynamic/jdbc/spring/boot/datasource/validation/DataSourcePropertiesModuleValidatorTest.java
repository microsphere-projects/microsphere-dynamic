package io.microsphere.dynamic.jdbc.spring.boot.datasource.validation;

import org.junit.Before;
import org.junit.Test;

import io.microsphere.dynamic.jdbc.spring.boot.config.validation.ValidationErrors;

/**
 * {@link DataSourcePropertiesModuleValidator} Test
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class DataSourcePropertiesModuleValidatorTest {

    private DataSourcePropertiesModuleValidator validator = new DataSourcePropertiesModuleValidator();

    private ValidationErrors errors;

    @Before
    public void before() {
        validator = new DataSourcePropertiesModuleValidator();
        errors = new ValidationErrors("test");
    }

    @Test
    public void test() {

    }
}
