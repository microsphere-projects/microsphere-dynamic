package io.microsphere.dynamic.jdbc.spring.boot.config.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

import static io.microsphere.text.FormatUtils.format;


/**
 * Validation Errors encapsulation
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class ValidationErrors {

    private static final Logger logger  = LoggerFactory.getLogger(ValidationErrors.class);

    private final List<String> errorMessages = new LinkedList<>();

    private final String dynamicJdbcConfigName;

    public ValidationErrors(String dynamicJdbcConfigName) {
        this.dynamicJdbcConfigName = dynamicJdbcConfigName;
    }

    public ValidationErrors addError(String messagePattern, Object... args) {
        String errorMessage = format(messagePattern, args);
        errorMessages.add(errorMessage);
        logger.debug(errorMessage);
        return this;
    }

    public boolean isValid() {
        return errorMessages.isEmpty();
    }

    @Override
    public String toString() {
        if (isValid()) {
            return "No Validation Error";
        }

        String prefix = format("DynamicJdbcConfig[name : {}] Validation Errors : [", dynamicJdbcConfigName);
        String suffix = "]";

        StringJoiner messageBuilder = new StringJoiner(System.lineSeparator(), prefix, suffix);
        errorMessages.forEach(messageBuilder::add);
        return messageBuilder.toString();
    }
}
