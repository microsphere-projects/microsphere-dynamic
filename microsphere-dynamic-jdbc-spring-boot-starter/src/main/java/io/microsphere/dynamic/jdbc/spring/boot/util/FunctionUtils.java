package io.microsphere.dynamic.jdbc.spring.boot.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The Utilities class for Java Function
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class FunctionUtils {

    private FunctionUtils() {}

    public static <T> boolean setIfAbsent(Supplier<T> supplier, Supplier<T> newSupplier, Consumer<T> consumer) {
        return setValueIfAbsent(supplier.get(), newSupplier, consumer);
    }

    public static <T> boolean setValueIfAbsent(T oldValue, Supplier<T> newSupplier, Consumer<T> consumer) {
        if (oldValue == null) {
            consumer.accept(newSupplier.get());
            return true;
        }
        return false;
    }

}
