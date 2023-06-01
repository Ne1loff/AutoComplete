package org.example.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class AirportInfo {
    String name;
    @Singular
    List<CsvField> fields;

    public CsvField getField(int index) {
        return fields.get(index);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append('[');
        for (int i = 0, fieldsSize = fields.size(); i < fieldsSize; i++) {
            var field = fields.get(i);
            sb.append(field.getValueAsString());

            if (i < fieldsSize - 1) {
                sb.append(", ");
            }
        }
        sb.append(']');
        return sb.toString();
    }
}
