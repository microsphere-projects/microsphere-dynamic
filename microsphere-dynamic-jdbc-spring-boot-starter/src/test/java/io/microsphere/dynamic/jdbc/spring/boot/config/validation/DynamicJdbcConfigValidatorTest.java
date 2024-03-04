package io.microsphere.dynamic.jdbc.spring.boot.config.validation;

import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import io.microsphere.dynamic.jdbc.spring.boot.config.DynamicJdbcConfig;

/**
 * {@link DynamicJdbcConfigValidator} Test
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class DynamicJdbcConfigValidatorTest {

    private DynamicJdbcConfigValidator validator;

    private ValidationErrors errors;

    @Before
    public void before() {
        validator = new DynamicJdbcConfigValidator();
        errors = new ValidationErrors("test");
    }

    @Test
    public void testName(){
        DynamicJdbcConfig config = new DynamicJdbcConfig();
        validator.validate(config, "test", errors);
        assertFalse(errors.isValid());
    }
    @Test
    public void testModules() {
        DynamicJdbcConfig config = new DynamicJdbcConfig();
        config.setName("test");
        validator.validate(config, "test", errors);
        assertFalse(errors.isValid());
    }

}
