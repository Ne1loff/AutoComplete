package org.example.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Getter
@Builder
public class AirportInfo {
    private final String name;
    @Singular
    private final List<CsvField> fields;

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
