package org.example.model;

import lombok.Data;

@Data
public class Message {
    private final String content;
    private final boolean quitRequest;
}
