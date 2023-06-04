package org.example.parser;

import org.example.config.CsvConfig;
import org.example.model.AirportInfo;
import org.example.model.CsvField;
import org.example.model.CsvFieldDataType;

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

    @Override
    public String parseField(String row, int fieldNum) {
        return parseCsvRow(row, fieldNum, 1)[0];
    }

    private AirportInfo getAirportInfo(String str) {
        var fields = parseCsvRow(str, 0, CsvConfig.CSV_FIELD_COUNT);
        return AirportInfo.builder()
                .name(fields[1])
                .field(new CsvField(getInt(fields[0]), CsvFieldDataType.INTEGER))
                .field(new CsvField(fields[1], CsvFieldDataType.STRING))
                .field(new CsvField(fields[2], CsvFieldDataType.STRING))
                .field(new CsvField(fields[3], CsvFieldDataType.STRING))
                .field(new CsvField(fields[4], CsvFieldDataType.STRING))
                .field(new CsvField(fields[5], CsvFieldDataType.STRING))
                .field(new CsvField(getDouble(fields[6]), CsvFieldDataType.DOUBLE))
                .field(new CsvField(getDouble(fields[7]), CsvFieldDataType.DOUBLE))
                .field(new CsvField(getInt(fields[8]), CsvFieldDataType.INTEGER))
                .field(new CsvField(tryGetDouble(fields[9]), CsvFieldDataType.NULLABLE_DOUBLE))
                .field(new CsvField(fields[10], CsvFieldDataType.STRING))
                .field(new CsvField(fields[11], CsvFieldDataType.STRING))
                .field(new CsvField(fields[12], CsvFieldDataType.STRING))
                .field(new CsvField(fields[13], CsvFieldDataType.STRING))
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

    private String[] parseCsvRow(String csvRow, int offset, int count) {
        if (count + offset > CsvConfig.CSV_FIELD_COUNT) throw new IllegalArgumentException("To many rows");

        String[] result = new String[count];
        StringBuilder builder = new StringBuilder();

        int resultCount = 0;
        boolean wasOpenDoubleQuotes = false;

        char[] charArray = csvRow.toCharArray();
        for (int i = 0; i < charArray.length && resultCount - offset < count; i++) {
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
                int index = resultCount++ - offset;
                if (index < 0) index = 0;

                result[index] = builder.toString();
                builder.delete(0, builder.length());
                continue;
            }

            builder.append(ch);
        }

        if (count + offset == CsvConfig.CSV_FIELD_COUNT)
            result[resultCount] = builder.toString();

        return result;
    }
}
