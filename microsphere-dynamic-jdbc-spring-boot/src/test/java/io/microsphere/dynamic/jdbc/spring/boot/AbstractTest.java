package io.microsphere.dynamic.jdbc.spring.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.BeforeClass;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static io.microsphere.dynamic.jdbc.spring.boot.util.DynamicJdbcConfigUtils.readResourceContent;
import static org.assertj.core.util.Arrays.asList;

/**
 * Abstract TestCase
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class AbstractTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeClass
    public static void beforeClass() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    protected static <T> T fromJsonResource(String resourceLocation, Class<T> valueType) {
        String jsonContent = getContent(resourceLocation);
        return fromJson(jsonContent, valueType);
    }

    protected static <T> T fromJson(String jsonContent, Class<T> valueType) {
        T value = null;
        try {
            value = objectMapper.readValue(jsonContent, valueType);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    protected static String getContent(String resourceLocation) {
        return readResourceContent(resourceLocation);
    }

    public static <T> Set<T> ofSet(T... values) {
        return new LinkedHashSet<T>((List<T>) asList(values));
    }

    public static <T> T[] of(T... values) {
        return values;
    }

}
