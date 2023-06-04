package org.example.searcher.indexer;

import org.example.model.file.LineInfo;

import java.util.List;

public interface Indexer {
    void indexFile(String fileName);

    List<LineInfo> findValuesByPrefix(String value);
}
