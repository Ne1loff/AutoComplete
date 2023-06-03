package org.example.model;

import java.util.Optional;

public final class CsvFieldDataType<T> {

    public static final CsvFieldDataType<Integer> INTEGER = new CsvFieldDataType<>(Integer.class);
    public static final CsvFieldDataType<Integer> NULLABLE_INTEGER = new CsvFieldDataType<>(Integer.class);
    public static final CsvFieldDataType<Double> DOUBLE = new CsvFieldDataType<>(Double.class);
    public static final CsvFieldDataType<Double> NULLABLE_DOUBLE = new CsvFieldDataType<>(Double.class);
    public static final CsvFieldDataType<String> STRING = new CsvFieldDataType<>(String.class);

    private final Class<T> clazz;

    private CsvFieldDataType(Class<T> clazz) {
        this.clazz = clazz;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
