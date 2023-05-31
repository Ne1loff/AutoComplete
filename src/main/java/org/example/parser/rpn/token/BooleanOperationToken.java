package org.example.parser.rpn.token;

import lombok.Data;

@Data
public class BooleanOperationToken implements Token {
    private final BooleanOperationType type;

    @Override
    public TokenType getToken() {
        return TokenType.BOOLEAN_OPERATION;
    }
}
