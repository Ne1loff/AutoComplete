package org.example.model;

import lombok.Getter;

@Getter
public class CsvField {
    private final Object value;
    private final CsvFieldDataType<?> fieldDataTypeType;

    public <T> CsvField(T value, CsvFieldDataType<T> fieldDataTypeType) {
        this.value = value;
        this.fieldDataTypeType = fieldDataTypeType;
    }

    public int getIntValue() {
        if (fieldDataTypeType != CsvFieldDataType.INTEGER && fieldDataTypeType != CsvFieldDataType.NULLABLE_INTEGER)
            throw new ClassCastException();
        return (Integer) value;
    }

    public double getDoubleValue() {
        if (fieldDataTypeType != CsvFieldDataType.DOUBLE && fieldDataTypeType != CsvFieldDataType.NULLABLE_DOUBLE)
            throw new ClassCastException();
        return (Double) value;
    }

    public String getStringValue() {
        if (fieldDataTypeType != CsvFieldDataType.STRING)
            throw new ClassCastException();
        return (String) value;
    }

    public boolean valueIsNull() {
        return value == null;
    }

    public String getValueAsString() {
        if (valueIsNull()) return "\\N";
        return value.toString();
    }
}
