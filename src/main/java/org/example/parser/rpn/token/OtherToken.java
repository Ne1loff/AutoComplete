package org.example.parser.rpn.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OtherToken implements Token {
    private final TokenType type;

    @Override
    public TokenType getToken() {
        return type;
    }
}
