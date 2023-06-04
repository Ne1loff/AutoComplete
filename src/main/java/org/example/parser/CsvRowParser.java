package org.example.parser;

import org.example.model.AirportInfo;

import java.util.Collection;
import java.util.List;

public interface CsvRowParser {

    List<AirportInfo> parseRows(Collection<String> rows);

    String parseField(String row, int fieldNum);
}
