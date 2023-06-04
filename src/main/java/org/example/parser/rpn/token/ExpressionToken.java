package org.example.parser.rpn.token;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExpressionToken implements Token {
    private int fieldNumber;
    private ComparativeOperationType comparativeOperationTypeType;
    private String value;

    @Override
    public TokenType getToken() {
        return TokenType.EXPRESSION;
    }
}
