package org.example.model.message;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.model.AirportInfo;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class SearchResult {
    private final List<AirportInfo> result;
    private final int resultCount;
    private final long spentTimeMills;
}
