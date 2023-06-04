package org.example.parser;

import org.example.command.FilterCommand;
import org.example.parser.rpn.LexerImpl;
import org.example.parser.rpn.PostfixConverterImpl;
import org.example.parser.rpn.StackMachineImpl;
import org.example.parser.rpn.token.Token;

import java.util.List;

public class FilterParserImpl implements FilterParser {

    private final LexerImpl lexer = new LexerImpl();
    private final PostfixConverterImpl converter = new PostfixConverterImpl();
    private final StackMachineImpl stackMachine = new StackMachineImpl();

    @Override
    public FilterCommand parse(String value) {
        if (value.isEmpty() || value.isBlank())
            return new FilterCommand((info) -> true);

        List<Token> tokens = lexer.getTokens(value);
        List<Token> postfixExpression = converter.convertToPostfix(tokens);
        var predicate = stackMachine.evaluate(postfixExpression);

        return new FilterCommand(predicate);
    }

    @Override
    public boolean isValidFilter(String value) {
        if (value.isEmpty() || value.isBlank()) return true;
        return lexer.isValidForTokenize(value);
    }
}
