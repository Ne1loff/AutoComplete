package org.example.parser.rpn.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BooleanOperationToken implements Token {
    private final BooleanOperationType type;

    @Override
    public TokenType getToken() {
        return TokenType.BOOLEAN_OPERATION;
    }
}
