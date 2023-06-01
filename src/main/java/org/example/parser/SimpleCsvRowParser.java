package org.example.parser;

import org.example.model.AirportInfo;
import org.example.model.CsvField;
import org.example.model.CsvFieldType;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleCsvRowParser implements CsvRowParser {
    @Override
    public List<AirportInfo> parseRows(Collection<String> rows) {
        return rows.stream()
                .map(this::getAirportInfo)
                .collect(Collectors.toList());
    }

    private AirportInfo getAirportInfo(String str) {
        var fields = str.split(",");
        return AirportInfo.builder()
                .name(fields[1])
                .field(new CsvField(getInt(fields[0]), CsvFieldType.INTEGER))
                .field(new CsvField(fields[1], CsvFieldType.STRING))
                .field(new CsvField(fields[2], CsvFieldType.STRING))
                .field(new CsvField(fields[3], CsvFieldType.STRING))
                .field(new CsvField(fields[4], CsvFieldType.STRING))
                .field(new CsvField(fields[5], CsvFieldType.STRING))
                .field(new CsvField(getDouble(fields[6]), CsvFieldType.DOUBLE))
                .field(new CsvField(getDouble(fields[7]), CsvFieldType.DOUBLE))
                .field(new CsvField(getInt(fields[8]), CsvFieldType.INTEGER))
                .field(new CsvField(getDouble(fields[9]), CsvFieldType.DOUBLE))
                .field(new CsvField(fields[10], CsvFieldType.STRING))
                .field(new CsvField(fields[11], CsvFieldType.STRING))
                .field(new CsvField(fields[12], CsvFieldType.STRING))
                .field(new CsvField(fields[12], CsvFieldType.STRING))
                .build();
    }

    private Integer getInt(String field) {
        try {
            return Integer.parseInt(field);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double getDouble(String field) {
        try {
            return Double.parseDouble(field);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
