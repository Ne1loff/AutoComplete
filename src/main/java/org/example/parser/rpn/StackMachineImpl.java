package org.example.parser.rpn;

import org.example.model.AirportInfo;
import org.example.model.CsvField;
import org.example.parser.rpn.token.*;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class StackMachineImpl implements StackMachine {

    @Override
    public Predicate<AirportInfo> evaluate(List<Token> postfixExpression) {
        Deque<Predicate<AirportInfo>> stack = new LinkedList<>();
        for (Token token : postfixExpression) {
            if (token instanceof ExpressionToken) {
                ExpressionToken expression = (ExpressionToken) token;
                var predicate = generatePredicate(expression);
                stack.push(predicate);
            } else if (token instanceof BooleanOperationToken) {
                BooleanOperationToken operation = (BooleanOperationToken) token;
                var right = stack.pop();
                var left = stack.pop();

                Predicate<AirportInfo> result;
                BooleanOperationType type = operation.getType();

                if (Objects.requireNonNull(type) == BooleanOperationType.AND) {
                    result = left.and(right);
                } else if (type == BooleanOperationType.OR) {
                    result = left.or(right);
                } else {
                    throw new IllegalStateException("Unexpected value: " + type);
                }
                stack.push(result);
            }
        }
        return stack.pop();
    }

    private Predicate<AirportInfo> generatePredicate(ExpressionToken expression) {
        return (info) -> {
            ComparativeOperationType type = expression.getComparativeOperationTypeType();
            var field = info.getField(expression.getFieldNumber() - 1);

            if (Objects.requireNonNull(type) == ComparativeOperationType.EQUAL) {
                return isEqual(field, expression.getValue());
            } else if (type == ComparativeOperationType.NOT_EQUAL) {
                return isNotEqual(field, expression.getValue());
            } else if (type == ComparativeOperationType.GREATER_THAN) {
                return isGreaterThan(field, expression.getValue());
            } else if (type == ComparativeOperationType.LESS_THAN) {
                return isLessThan(field, expression.getValue());
            }
            throw new IllegalStateException("Unexpected operation type");
        };
    }

    private boolean isEqual(CsvField field, String value) {
        var fieldType = field.getFieldType();
        switch (fieldType) {
            case NULLABLE_INTEGER:
                if (field.valueIsNull()) {
                    return false;
                }
            case INTEGER:
                return field.getIntValue() == Integer.parseInt(value);
            case NULLABLE_DOUBLE:
                if (field.valueIsNull()) {
                    return false;
                }
            case DOUBLE:
                var first = field.getDoubleValue();
                var second = Double.parseDouble(value);
                return Double.compare(first, second) == 0;
            case STRING:
                return field.getStringValue().equals(value);

            default:
                throw new IllegalStateException("Unexpected value: " + fieldType);
        }
    }

    private boolean isNotEqual(CsvField field, String value) {
        var fieldType = field.getFieldType();
        switch (fieldType) {
            case NULLABLE_INTEGER:
                if (field.valueIsNull()) {
                    return false;
                }
            case INTEGER:
                return field.getIntValue() != Integer.parseInt(value);
            case NULLABLE_DOUBLE:
                if (field.valueIsNull()) {
                    return false;
                }
            case DOUBLE:
                var first = field.getDoubleValue();
                var second = Double.parseDouble(value);
                return Double.compare(first, second) != 0;
            case STRING:
                return !(field.getStringValue().equals(value));
            default:
                throw new IllegalStateException("Unexpected value: " + fieldType);
        }
    }

    private boolean isGreaterThan(CsvField field, String value) {
        var fieldType = field.getFieldType();
        switch (fieldType) {
            case NULLABLE_INTEGER:
                if (field.valueIsNull()) {
                    return false;
                }
            case INTEGER:
                return field.getIntValue() > Integer.parseInt(value);
            case NULLABLE_DOUBLE:
                if (field.valueIsNull()) {
                    return false;
                }
            case DOUBLE:
                var first = field.getDoubleValue();
                var second = Double.parseDouble(value);
                return Double.compare(first, second) == 1;
            case STRING:
                throw new IllegalStateException("It is impossible to find out which line is bigger");

            default:
                throw new IllegalStateException("Unexpected value: " + fieldType);

        }
    }

    private boolean isLessThan(CsvField field, String value) {
        var fieldType = field.getFieldType();
        switch (fieldType) {
            case NULLABLE_INTEGER:
                if (field.valueIsNull()) {
                    return false;
                }
            case INTEGER:
                return field.getIntValue() < Integer.parseInt(value);
            case NULLABLE_DOUBLE:
                if (field.valueIsNull()) {
                    return false;
                }
            case DOUBLE:
                var first = field.getDoubleValue();
                var second = Double.parseDouble(value);
                return Double.compare(first, second) == -1;
            case STRING:
                throw new IllegalStateException("It is impossible to find out which line is smaller");

            default:
                throw new IllegalStateException("Unexpected value: " + fieldType);
        }
    }
}
