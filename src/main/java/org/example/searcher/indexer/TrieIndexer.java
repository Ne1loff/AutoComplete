package org.example.searcher.indexer;

import com.arun.trie.MapTrie;
import lombok.RequiredArgsConstructor;
import org.example.model.FileLine;
import org.example.parser.CsvRowParser;
import org.example.reader.Reader;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class TrieIndexer implements Indexer {
    private final MapTrie<Long> trie = new MapTrie<>();

    private final Reader reader;

    private final CsvRowParser csvParser;

    @Override
    public void indexFile(String fileName) {
        List<FileLine> lines;
        try (reader) {
            reader.open(fileName, 2048);
            while ((lines = reader.getFileLinesFormBuffer()) != null) {
                for (FileLine line : lines) {
                    var content = line.getContent();
                    var airportName = csvParser.parseField(content, 1);
                    trie.insert(airportName, line.getStartPosition());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Long> getIndexes(String value) {
        List<Long> result = trie.getValueSuggestions(value);
        Collections.sort(result);
        return result;
    }
}
