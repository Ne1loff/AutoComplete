package org.example.parser;

import org.example.command.FilterCommand;
import org.example.parser.rpn.StackMachine;
import org.example.parser.rpn.Lexer;
import org.example.parser.rpn.PostfixConverter;
import org.example.parser.rpn.token.Token;

import java.util.List;

public class FilterParserImpl implements FilterParser {

    private final Lexer lexer = new Lexer();
    private final PostfixConverter converter = new PostfixConverter();
    private final StackMachine stackMachine = new StackMachine();

    @Override
    public FilterCommand parse(String value) {
        List<Token> tokens = lexer.getTokens(value);
        List<Token> postfixExpression = converter.convertToPostfix(tokens);
        var predicate = stackMachine.evaluate(postfixExpression);

        return new FilterCommand(predicate);
    }
}
