package io.microsphere.dynamic.jdbc.spring.boot.context.error;

public class DynamicJdbcInitializeException extends RuntimeException {

    public DynamicJdbcInitializeException(String message) {
        super(message);
    }

    public DynamicJdbcInitializeException(String message, Throwable cause) {
        super(message, cause);
    }
}
