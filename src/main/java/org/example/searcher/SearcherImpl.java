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
        System.out.println("Indexer time: " + (System.nanoTime() - start) / 1_000_000);
        try (reader) {
            var openStart = System.nanoTime();
            reader.open(searchFileName, 512);
            System.out.println("Open file time: " + (System.nanoTime() - openStart) / 1_000_000);

            var readStart = System.nanoTime();
            var lines = reader.getLines(indexes);
            System.out.println("Read from CSV time: " + (System.nanoTime() - readStart) / 1_000_000);

            var parseStart = System.nanoTime();
            var airportInfos = csvRowParser.parseRows(lines);
            System.out.println("Parse CSV time: " + (System.nanoTime() - parseStart) / 1_000_000);

            var filterStart = System.nanoTime();
            result = airportInfos.stream()
                    .filter(command.getFilterCommand().getFilter())
                    .collect(Collectors.toList());
            System.out.println("Filter result time: " + (System.nanoTime() - filterStart) / 1_000_000);

            end = System.nanoTime();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new SearchResult(result, result.size(), (end - start) / 1_000_000);
    }


}
