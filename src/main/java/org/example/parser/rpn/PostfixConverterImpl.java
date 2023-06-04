package org.example.parser.rpn;

import org.example.parser.rpn.token.BooleanOperationToken;
import org.example.parser.rpn.token.BooleanOperationType;
import org.example.parser.rpn.token.Token;
import org.example.parser.rpn.token.TokenType;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class PostfixConverterImpl implements PostfixConverter {

    @Override
    public List<Token> convertToPostfix(List<Token> source) {
        List<Token> postfixExpression = new ArrayList<>();
        Deque<Token> operationStack = new LinkedList<>();
        try {
            for (Token token : source) {
                switch (token.getToken()) {
                    case EXPRESSION:
                        postfixExpression.add(token);
                        break;
                    case OPEN_BRACKET:
                        operationStack.push(token);
                        break;
                    case CLOSE_BRACKET: {
                        while (!operationStack.isEmpty() && operationStack.peek().getToken() != TokenType.OPEN_BRACKET) {
                            postfixExpression.add(operationStack.pop());
                        }
                        operationStack.pop();
                        break;
                    }
                    case BOOLEAN_OPERATION: {
                        while (!operationStack.isEmpty() && getPriority(operationStack.peek()) >= getPriority(token)) {
                            postfixExpression.add(operationStack.pop());
                        }
                        operationStack.push(token);
                        break;
                    }
                }
            }
            while (!operationStack.isEmpty()) {
                postfixExpression.add(operationStack.pop());
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при парсинге фильтра. Проверьте правильность введенного фильтра");
        }
        return postfixExpression;
    }

    private int getPriority(Token token) {
        if (token instanceof BooleanOperationToken) {
            BooleanOperationToken operationToken = (BooleanOperationToken) token;
            if (operationToken.getType() == BooleanOperationType.OR) return 1;
            if (operationToken.getType() == BooleanOperationType.AND) return 2;
        }
        return 0;
    }
}
