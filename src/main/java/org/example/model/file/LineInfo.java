package org.example.model.file;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LineInfo {
    private final long startPosition;
    private final int length;
}
