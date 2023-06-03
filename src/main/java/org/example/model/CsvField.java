package org.example.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CsvField {
    private final Object value;
    private final CsvFieldType fieldType;

    public int getIntValue() {
        if (fieldType != CsvFieldType.INTEGER && fieldType != CsvFieldType.NULLABLE_INTEGER)
            throw new ClassCastException();
        return (Integer) value;
    }

    public double getDoubleValue() {
        if (fieldType != CsvFieldType.DOUBLE && fieldType != CsvFieldType.NULLABLE_DOUBLE)
            throw new ClassCastException();
        return (Double) value;
    }

    public String getStringValue() {
        if (fieldType != CsvFieldType.STRING) throw new ClassCastException();
        return (String) value;
    }

    public boolean valueIsNull() {
        return value == null;
    }

    public String getValueAsString() {
        if (valueIsNull()) return "N";
        return value.toString();
    }
}
