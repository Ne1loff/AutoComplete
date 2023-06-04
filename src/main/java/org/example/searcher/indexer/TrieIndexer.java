package org.example.searcher.indexer;

import com.arun.trie.MapTrie;
import lombok.RequiredArgsConstructor;
import org.example.model.file.FileLine;
import org.example.model.file.LineInfo;
import org.example.parser.CsvRowParser;
import org.example.reader.Reader;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class TrieIndexer implements Indexer {
    private final MapTrie<LineInfo> trie = new MapTrie<>();

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
                    trie.insert(airportName, line.getLineInfo());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<LineInfo> findValuesByPrefix(String value) {
        List<LineInfo> result = trie.getValueSuggestions(value);
        result.sort(Comparator.comparing(LineInfo::getStartPosition));
        return result;
    }
}
