package org.example.searcher;

import org.example.command.SearchCommand;
import org.example.model.message.SearchResult;

public interface Searcher {
    SearchResult search(SearchCommand command);

    void init(String fileName);
}
