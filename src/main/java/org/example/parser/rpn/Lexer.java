package org.example.parser.rpn;

import org.example.parser.rpn.token.Token;

import java.util.List;

public interface Lexer {
    List<Token> getTokens(String source);
    boolean isValidForTokenize(String source);
}
