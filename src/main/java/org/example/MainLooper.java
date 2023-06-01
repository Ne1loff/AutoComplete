package org.example;

import lombok.RequiredArgsConstructor;
import org.example.command.FilterCommand;
import org.example.command.SearchCommand;
import org.example.messager.Messenger;
import org.example.model.SearchResult;
import org.example.parser.FilterParser;
import org.example.searcher.Searcher;

@RequiredArgsConstructor
public class MainLooper {

    private final FilterParser filterParser;
    private final Searcher searcher;
    private final Messenger messenger;

    public void start(String fileName) {
        searcher.init(fileName);

        while (true) {
            var message = messenger.requestFilter();
            if (message.isQuitRequest()) break;

            FilterCommand filterCommand = filterParser.parse(message.getContent());

            message = messenger.requestNamePrefix();
            if (message.isQuitRequest()) break;

            SearchResult result = searcher.search(new SearchCommand(message.getContent(), filterCommand));
            messenger.responseResults(result);
        }
    }
}
