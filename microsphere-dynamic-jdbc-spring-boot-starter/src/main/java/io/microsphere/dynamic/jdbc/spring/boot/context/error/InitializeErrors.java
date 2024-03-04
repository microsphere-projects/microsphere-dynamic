package io.microsphere.dynamic.jdbc.spring.boot.context.error;

import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

import static io.microsphere.text.FormatUtils.format;

public class InitializeErrors {

    private final Map<String, Throwable> errors;

    public InitializeErrors() {
        errors = new ConcurrentHashMap<>();
    }

    public void addError(String dynamicJdbcPropertyName, Throwable error) {
        errors.putIfAbsent(dynamicJdbcPropertyName, error);
    }

    public boolean hasError() {
        return !CollectionUtils.isEmpty(errors);
    }

    @Override
    public String toString() {
        if (!hasError()) {
            return "No Initialize Error";
        }
        String formatMessage = "DynamicJdbcConfig[name : {}] Initialize Error : [{}: {}]";
        StringJoiner messageBuilder = new StringJoiner(System.lineSeparator());
        errors.forEach((key, value) -> messageBuilder.add(format(formatMessage, key, value.getClass().getSimpleName(), value.getMessage())));
        return messageBuilder.toString();
    }
}
