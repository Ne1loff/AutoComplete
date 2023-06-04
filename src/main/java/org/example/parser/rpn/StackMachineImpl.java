package org.example.parser.rpn;

import org.example.model.AirportInfo;
import org.example.model.CsvField;
import org.example.model.CsvFieldDataType;
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

            if (type == ComparativeOperationType.EQUAL) {
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
        var fieldType = field.getFieldDataTypeType();

        if (fieldType == CsvFieldDataType.NULLABLE_INTEGER) {
            if (field.valueIsNull()) return false;
            return compareInt(field.getIntValue(), Integer.parseInt(value)) == 0;

        } else if (fieldType == CsvFieldDataType.INTEGER) {
            return compareInt(field.getIntValue(), Integer.parseInt(value)) == 0;

        } else if (fieldType == CsvFieldDataType.NULLABLE_DOUBLE) {
            if (field.valueIsNull()) return false;
            return compareDouble(field.getDoubleValue(), Double.parseDouble(value)) == 0;

        } else if (fieldType == CsvFieldDataType.DOUBLE) {
            return compareDouble(field.getDoubleValue(), Double.parseDouble(value)) == 0;

        } else if (fieldType == CsvFieldDataType.STRING) {
            return compareString(field.getStringValue(), value);
        }

        throw new IllegalStateException("Unexpected value: " + fieldType);
    }

    private boolean isNotEqual(CsvField field, String value) {
        var fieldType = field.getFieldDataTypeType();

        if (fieldType == CsvFieldDataType.NULLABLE_INTEGER) {
            if (field.valueIsNull()) return false;
            return compareInt(field.getIntValue(), Integer.parseInt(value)) != 0;

        } else if (fieldType == CsvFieldDataType.INTEGER) {
            return compareInt(field.getIntValue(), Integer.parseInt(value)) != 0;

        } else if (fieldType == CsvFieldDataType.NULLABLE_DOUBLE) {
            if (field.valueIsNull()) return false;
            return compareDouble(field.getDoubleValue(), Double.parseDouble(value)) != 0;

        } else if (fieldType == CsvFieldDataType.DOUBLE) {
            return compareDouble(field.getDoubleValue(), Double.parseDouble(value)) != 0;

        } else if (fieldType == CsvFieldDataType.STRING) {
            return !compareString(field.getStringValue(), value);
        }

        throw new IllegalStateException("Unexpected value: " + fieldType);
    }

    private boolean isGreaterThan(CsvField field, String value) {
        var fieldType = field.getFieldDataTypeType();

        if (fieldType == CsvFieldDataType.NULLABLE_INTEGER) {
            if (field.valueIsNull()) return false;
            return compareInt(field.getIntValue(), Integer.parseInt(value)) == 1;

        } else if (fieldType == CsvFieldDataType.INTEGER) {
            return compareInt(field.getIntValue(), Integer.parseInt(value)) == 1;

        } else if (fieldType == CsvFieldDataType.NULLABLE_DOUBLE) {
            if (field.valueIsNull()) return false;
            return compareDouble(field.getDoubleValue(), Double.parseDouble(value)) == 1;

        } else if (fieldType == CsvFieldDataType.DOUBLE) {
            return compareDouble(field.getDoubleValue(), Double.parseDouble(value)) == 1;

        } else if (fieldType == CsvFieldDataType.STRING) {
            throw new IllegalStateException("It is impossible to find out which line is bigger");
        }

        throw new IllegalStateException("Unexpected value: " + fieldType);
    }

    private boolean isLessThan(CsvField field, String value) {
        var fieldType = field.getFieldDataTypeType();

        if (fieldType == CsvFieldDataType.NULLABLE_INTEGER) {
            if (field.valueIsNull()) return false;
            return compareInt(field.getIntValue(), Integer.parseInt(value)) == -1;

        } else if (fieldType == CsvFieldDataType.INTEGER) {
            return compareInt(field.getIntValue(), Integer.parseInt(value)) == -1;

        } else if (fieldType == CsvFieldDataType.NULLABLE_DOUBLE) {
            if (field.valueIsNull()) return false;
            return compareDouble(field.getDoubleValue(), Double.parseDouble(value)) == -1;

        } else if (fieldType == CsvFieldDataType.DOUBLE) {
            return compareDouble(field.getDoubleValue(), Double.parseDouble(value)) == -1;

        } else if (fieldType == CsvFieldDataType.STRING) {
            throw new IllegalStateException("It is impossible to find out which line is smaller");
        }

        throw new IllegalStateException("Unexpected value: " + fieldType);
    }

    private int compareInt(int first, int second) {
        return Integer.compare(first, second);
    }

    private int compareDouble(double first, double second) {
        return Double.compare(first, second);
    }

    private boolean compareString(String first, String second) {
        return first.equals(second);
    }
}
