package samukadev.coderpg.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;

import static java.util.Optional.ofNullable;

public class Context extends HashMap<String, Object> implements Serializable {
    @Getter
    @AllArgsConstructor
    private enum Parameters {
        DATA("data"), RESULT("result");
        private final String value;
    }

    public Context(final Object data) {
        super.put(Parameters.DATA.value, data);
    }

    public Context() {
        super();
    }

    public Class<?> getDataClass() {
        return ofNullable(get(Parameters.DATA.value))
                .map(Object::getClass)
                .orElse(null);
    }

    public Class<?> getResultClass() {
        return ofNullable(get(Parameters.RESULT.value))
                .map(Object::getClass)
                .orElse(null);
    }

    public <T> T getData(final Class<T> clazz) {
        return getProperty(Parameters.DATA.value, clazz);
    }

    public void setData(final Object data) {
        put(Parameters.DATA.value, data);
    }

    public <T> T getResult(final Class<T> clazz) {
        return getProperty(Parameters.RESULT.value, clazz);
    }

    public void setResult(final Object result) {
        put(Parameters.RESULT.value, result);
    }

    public void putProperty(final String key, final Object value) {
        put(key, value);
    }

    public <R> R getProperty(final String key, final Class<R> clazz) {
        return ofNullable(get(key))
                .map(clazz::cast)
                .orElse(null);
    }
}
