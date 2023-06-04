package org.example.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.model.csv.CsvField;
import org.example.model.csv.CsvFieldDataType;

@Getter
@RequiredArgsConstructor
public class AirportInfo {
    private final String name;
    private final CsvField[] fields;

    public CsvField getField(int index) {
        return fields[index];
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('"').append(name).append('"');
        sb.append('[');
        for (int i = 0, fieldsSize = fields.length; i < fieldsSize; i++) {
            var field = fields[i];
            if (field.getFieldDataTypeType().equals(CsvFieldDataType.STRING)) {
                sb.append('"').append(field.getValueAsString()).append('"');
            } else {
                sb.append(field.getValueAsString());
            }

            if (i < fieldsSize - 1) {
                sb.append(", ");
            }
        }
        sb.append(']');
        return sb.toString();
    }
}
