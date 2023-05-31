package org.example.parser.rpn.token;

import lombok.Data;

@Data
public class OtherToken implements Token {
    private final TokenType type;

    @Override
    public TokenType getToken() {
        return type;
    }
}
