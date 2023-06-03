package org.example.parser.rpn;

import org.example.model.AirportInfo;
import org.example.parser.rpn.token.Token;

import java.util.List;
import java.util.function.Predicate;

public interface StackMachine {
    Predicate<AirportInfo> evaluate(List<Token> postfixExpression);
}
