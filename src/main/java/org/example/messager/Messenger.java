package org.example.messager;

import org.example.model.Message;
import org.example.model.SearchResult;

public interface Messenger {
    Message requestFilter();

    Message requestNamePrefix();

    void responseResults(SearchResult result);
}
