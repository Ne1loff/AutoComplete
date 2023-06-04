package org.example.model.csv;

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
            throw new ClassCastException("Не возможно получить Integer из " + fieldDataTypeType.getClazz());
        return (Integer) value;
    }

    public double getDoubleValue() {
        if (fieldDataTypeType != CsvFieldDataType.DOUBLE && fieldDataTypeType != CsvFieldDataType.NULLABLE_DOUBLE)
            throw new ClassCastException("Не возможно получить Double из " + fieldDataTypeType.getClazz());
        return (Double) value;
    }

    public String getStringValue() {
        if (fieldDataTypeType != CsvFieldDataType.STRING)
            throw new ClassCastException("Не возможно получить String из " + fieldDataTypeType.getClazz());
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
