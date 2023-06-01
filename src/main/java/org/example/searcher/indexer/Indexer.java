package org.example.searcher.indexer;

import java.util.List;

public interface Indexer {
    void indexFile(String fileName);

    long getIndex(String value);

    List<Long> getIndexes(String value);
}
