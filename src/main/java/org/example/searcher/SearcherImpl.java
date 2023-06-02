package org.example.searcher;

import lombok.RequiredArgsConstructor;
import org.example.command.SearchCommand;
import org.example.model.AirportInfo;
import org.example.model.SearchResult;
import org.example.parser.CsvRowParser;
import org.example.reader.Reader;
import org.example.searcher.indexer.Indexer;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SearcherImpl implements Searcher {

    private final Indexer indexer;
    private final Reader reader;
    private final CsvRowParser csvRowParser;
    private String searchFileName;

    @Override
    public void init(String fileName) {
        indexer.indexFile(fileName);
        searchFileName = fileName;
    }

    @Override
    public SearchResult search(SearchCommand command) {
        List<AirportInfo> result;

        long start = System.nanoTime();
        long end;

        var indexes = indexer.getIndexes(command.getAirportNamePrefix());

        try (reader) {
            reader.open(searchFileName, 256);
            var lines = reader.getLines(indexes);
            var airportInfos = csvRowParser.parseRows(lines);
            result = airportInfos.parallelStream()
                    .filter(command.getFilterCommand().getFilter())
                    .collect(Collectors.toList());
            end = System.nanoTime();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new SearchResult(result, result.size(), (end - start) / 1_000_000);
    }


}
