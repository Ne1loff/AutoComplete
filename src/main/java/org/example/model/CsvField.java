package org.example.model;

import lombok.Data;

@Data
public class CsvField {
    private final Object value;
    private final CsvFieldType fieldType;

    public int getIntValue() {
        if (fieldType != CsvFieldType.INTEGER) throw new ClassCastException();
        return (int) value;
    }

    public double getDoubleValue() {
        if (fieldType != CsvFieldType.DOUBLE) throw new ClassCastException();
        return (double) value;
    }

    public String getStringValue() {
        if (fieldType != CsvFieldType.STRING) throw new ClassCastException();
        return (String) value;
    }
}
