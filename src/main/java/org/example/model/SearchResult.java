package org.example.model;


import lombok.Data;

import java.util.List;

@Data
public class SearchResult {
    private final List<AirportInfo> result;
    private final int resultCount;
    private final long spentTimeMills;
}
