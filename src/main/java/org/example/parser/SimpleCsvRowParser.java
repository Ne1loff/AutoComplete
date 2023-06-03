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
        var fields = parseCsvRow(str);
        return AirportInfo.builder()
                .name(fields[1])
                .field(new CsvField(tryGetInt(fields[0]), CsvFieldType.INTEGER))
                .field(new CsvField(fields[1], CsvFieldType.STRING))
                .field(new CsvField(fields[2], CsvFieldType.STRING))
                .field(new CsvField(fields[3], CsvFieldType.STRING))
                .field(new CsvField(fields[4], CsvFieldType.STRING))
                .field(new CsvField(fields[5], CsvFieldType.STRING))
                .field(new CsvField(getDouble(fields[6]), CsvFieldType.DOUBLE))
                .field(new CsvField(getDouble(fields[7]), CsvFieldType.DOUBLE))
                .field(new CsvField(getInt(fields[8]), CsvFieldType.INTEGER))
                .field(new CsvField(tryGetDouble(fields[9]), CsvFieldType.NULLABLE_DOUBLE))
                .field(new CsvField(fields[10], CsvFieldType.STRING))
                .field(new CsvField(fields[11], CsvFieldType.STRING))
                .field(new CsvField(fields[12], CsvFieldType.STRING))
                .field(new CsvField(fields[13], CsvFieldType.STRING))
                .build();
    }

    private Integer tryGetInt(String field) {
        if (field.equals("\\N")) return null;
        return Integer.parseInt(field);
    }

    private Integer getInt(String field) {
        return Integer.parseInt(field);
    }

    private Double getDouble(String field) {
        return Double.parseDouble(field);
    }

    private Double tryGetDouble(String field) {
        if (field.equals("\\N")) return null;
        return Double.parseDouble(field);

    }

    private String[] parseCsvRow(String csvRow) {
        String[] result = new String[14];
        StringBuilder builder = new StringBuilder();

        int resultCount = 0;
        boolean wasOpenDoubleQuotes = false;

        char[] charArray = csvRow.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char ch = charArray[i];

            if (ch == '\\' && charArray[(i + 1) % charArray.length] == '"') {
                builder.append('"');
                continue;
            }

            if (ch == '"') {
                wasOpenDoubleQuotes = !wasOpenDoubleQuotes;
                continue;
            }

            if (ch == ',' && !wasOpenDoubleQuotes) {
                result[resultCount++] = builder.toString();
                builder.delete(0, builder.length());
                continue;
            }

            builder.append(ch);
        }

        result[resultCount] = builder.toString();

        return result;
    }
}
