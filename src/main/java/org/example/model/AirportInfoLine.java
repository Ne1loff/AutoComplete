package org.example.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AirportInfoLine {
    String name;
    List<CsvField> fields;

    public CsvField getField(int index) {
        return fields.get(index);
    }

    public void addField(CsvField field) {
        fields.add(field);
    }
}
