package org.example.messager;

import org.example.model.message.Message;
import org.example.model.message.SearchResult;

public interface Messenger {
    Message requestFilter();

    Message requestNamePrefix();

    void invalidFilterMessage();

    void responseResults(SearchResult result);
}
