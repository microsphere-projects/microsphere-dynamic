package io.microsphere.dynamic.jdbc.spring.boot.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * The utilities class for {@link URL}
 * 
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class URLUtils {

    public final static String QUERY_STRING_SEPARATOR = "?";

    public final static String QUERY_PARAM_SEPARATOR = "&";

    public final static String PARAM_NAME_VALUE_SEPARATOR = "=";

    private URLUtils() {}

    public static MultiValueMap<String, String> parseQueryParams(URI uri) {
        return parseQueryParamsFromQueryString(uri.getQuery());
    }

    public static MultiValueMap<String, String> parseQueryParams(String url) {
        return parseQueryParams(URI.create(url));
    }

    private static MultiValueMap<String, String> parseQueryParamsFromQueryString(String queryString) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        if (StringUtils.isNotBlank(queryString)) {
            String[] params = StringUtils.split(queryString, QUERY_PARAM_SEPARATOR);
            for (String param : params) {
                String[] nameAndValue = StringUtils.split(param, PARAM_NAME_VALUE_SEPARATOR);
                if (nameAndValue.length == 2) {
                    String name = nameAndValue[0];
                    String value = nameAndValue[1];
                    queryParams.add(name, value);
                }
            }
        }
        return queryParams;
    }

    public static String buildQueryString(MultiValueMap<String, String> queryParams) {
        StringJoiner queryStringJoiner = new StringJoiner(QUERY_PARAM_SEPARATOR);
        for (Map.Entry<String, List<String>> queryParamEntity : queryParams.entrySet()) {
            String name = queryParamEntity.getKey();
            List<String> values = queryParamEntity.getValue();
            for (String value : values) {
                queryStringJoiner.add(name + PARAM_NAME_VALUE_SEPARATOR + value);
            }
        }
        return queryStringJoiner.toString();
    }
}
