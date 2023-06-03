package org.example.parser.rpn;

import org.example.parser.rpn.token.Token;

import java.util.List;

public interface PostfixConverter {
    List<Token> convertToPostfix(List<Token> source);
}
