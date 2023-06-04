package org.example.parser.rpn;

import org.example.parser.rpn.token.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LexerImpl implements Lexer {

    @Override
    public List<Token> getTokens(String source) {
        Pattern pattern = Pattern.compile("(column\\[\\d{1,2}] ?[><=]{1,2} ?[^ &|()]+)|([&|]{1,2})|([()])");
        Matcher matcher = pattern.matcher(source);

        List<Token> tokens = new ArrayList<>();
        while (matcher.find()) {
            var token = matcher.group();

            if (token.isBlank()) {
                continue;
            } else if (isExpression(token)) {
                tokens.add(generateExpretionToken(token));
                continue;
            }

            switch (token) {
                case "&":
                case "&&":
                    tokens.add(new BooleanOperationToken(BooleanOperationType.AND));
                    break;
                case "|":
                case "||":
                    tokens.add(new BooleanOperationToken(BooleanOperationType.OR));
                    break;
                case "(":
                    tokens.add(new OtherToken(TokenType.OPEN_BRACKET));
                    break;
                case ")":
                    tokens.add(new OtherToken(TokenType.CLOSE_BRACKET));
                    break;
                default:
                    throw new RuntimeException("Неожиданный токен: " + token);
            }
        }
        return tokens;
    }

    @Override
    public boolean isValidForTokenize(String source) {
        Pattern pattern = Pattern.compile("(column\\[\\d{1,2}] ?[><=]{1,2} ?[^ &|()]+)|([&|]{1,2})|([()])");
        Matcher matcher = pattern.matcher(source);
        return matcher.find();
    }

    private boolean isExpression(String token) {
        Pattern pattern = Pattern.compile("(column\\[\\d{1,2}] ?[<>=]{1,2} ?.*)");
        Matcher matcher = pattern.matcher(token);
        return matcher.matches();
    }

    private Token generateExpretionToken(String expression) {
        expression = expression.trim();
        Pattern pattern = Pattern.compile("((?<=\\[)\\d{1,2}(?=]))|([<>=]{1,2})|((?<=['<>= ])[^' ]+)");
        Matcher matcher = pattern.matcher(expression);

        ExpressionToken token = new ExpressionToken();

        if (matcher.groupCount() != 3) {
            throw new IllegalStateException(
                    "Фильтр сравнения должен состоять из 3-х элементов: (номер колонки, оператор сравнения, значение)"
            );
        }

        for (int i = 0; i < 3 && matcher.find(); i++) {
            var group = matcher.group();
            if (i == 0) {
                token.setFieldNumber(Integer.parseInt(group));
            } else if (i == 1) {
                switch (group) {
                    case ">":
                        token.setComparativeOperationTypeType(ComparativeOperationType.GREATER_THAN);
                        break;
                    case "<":
                        token.setComparativeOperationTypeType(ComparativeOperationType.LESS_THAN);
                        break;
                    case "<>":
                        token.setComparativeOperationTypeType(ComparativeOperationType.NOT_EQUAL);
                        break;
                    case "=":
                        token.setComparativeOperationTypeType(ComparativeOperationType.EQUAL);
                        break;
                }
            } else {
                token.setValue(group);
            }
        }

        return token;
    }
}
