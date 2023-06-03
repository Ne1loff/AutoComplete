package org.example.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FileLine {
    private final String content;
    private final long startPosition;
    private final long length;
}
