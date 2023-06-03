package org.example.model;

public class CsvField {
    private final Object value;
    private final CsvFieldDataType<?> fieldDataTypeType;

    public <T> CsvField(T value, CsvFieldDataType<T> fieldDataTypeType) {
        this.value = value;
        this.fieldDataTypeType = fieldDataTypeType;
    }

    public int getIntValue() {
        if (!fieldDataTypeType.equals(CsvFieldDataType.INTEGER)
                && !fieldDataTypeType.equals(CsvFieldDataType.NULLABLE_INTEGER))
            throw new ClassCastException();
        return (int) value;
    }

    public double getDoubleValue() {
        if (!fieldDataTypeType.equals(CsvFieldDataType.DOUBLE)
                && !fieldDataTypeType.equals(CsvFieldDataType.NULLABLE_DOUBLE))
            throw new ClassCastException();
        return (double) value;
    }

    public String getStringValue() {
        if (fieldDataTypeType != CsvFieldDataType.STRING) throw new ClassCastException();
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
