package org.example.model.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Message {
    private final String content;
    private final boolean quitRequest;
}
