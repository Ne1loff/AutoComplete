package org.example.model.file;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FileLine {
    private final String content;
    private final LineInfo lineInfo;
}
