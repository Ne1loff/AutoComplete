package org.example.parser.rpn;

import org.example.model.AirportInfo;
import org.example.model.csv.CsvField;
import org.example.model.csv.CsvFieldDataType;
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
        try {
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
                        throw new IllegalStateException("Неожиданная булева операция: " + type);
                    }
                    stack.push(result);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при парсинге фильтра. Проверьте правильность введенного фильтра");
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
            throw new IllegalStateException("Неожиданная операция сравнения: ");
        };
    }

    private boolean isEqual(CsvField field, String value) {
        var fieldType = field.getFieldDataTypeType();

        if (fieldType == CsvFieldDataType.NULLABLE_INTEGER) {
            if (field.valueIsNull()) return false;
            return parseAndCompareInt(field.getIntValue(), value) == 0;

        } else if (fieldType == CsvFieldDataType.INTEGER) {
            return parseAndCompareInt(field.getIntValue(), value) == 0;

        } else if (fieldType == CsvFieldDataType.NULLABLE_DOUBLE) {
            if (field.valueIsNull()) return false;
            return parseAndCompareDouble(field.getDoubleValue(), value) == 0;

        } else if (fieldType == CsvFieldDataType.DOUBLE) {
            return parseAndCompareDouble(field.getDoubleValue(), value) == 0;

        } else if (fieldType == CsvFieldDataType.STRING) {
            return compareString(field.getStringValue(), value);
        }

        throw new IllegalStateException("Неожиданный тип CSV поля: " + fieldType);
    }

    private boolean isNotEqual(CsvField field, String value) {
        var fieldType = field.getFieldDataTypeType();

        if (fieldType == CsvFieldDataType.NULLABLE_INTEGER) {
            if (field.valueIsNull()) return false;
            return parseAndCompareInt(field.getIntValue(), value) != 0;

        } else if (fieldType == CsvFieldDataType.INTEGER) {
            return parseAndCompareInt(field.getIntValue(), value) != 0;

        } else if (fieldType == CsvFieldDataType.NULLABLE_DOUBLE) {
            if (field.valueIsNull()) return false;
            return parseAndCompareDouble(field.getDoubleValue(), value) != 0;

        } else if (fieldType == CsvFieldDataType.DOUBLE) {
            return parseAndCompareDouble(field.getDoubleValue(), value) != 0;

        } else if (fieldType == CsvFieldDataType.STRING) {
            return !compareString(field.getStringValue(), value);
        }

        throw new IllegalStateException("Неожиданный тип CSV поля: " + fieldType);
    }

    private boolean isGreaterThan(CsvField field, String value) {
        var fieldType = field.getFieldDataTypeType();

        if (fieldType == CsvFieldDataType.NULLABLE_INTEGER) {
            if (field.valueIsNull()) return false;
            return parseAndCompareInt(field.getIntValue(), value) == 1;

        } else if (fieldType == CsvFieldDataType.INTEGER) {
            return parseAndCompareInt(field.getIntValue(), value) == 1;

        } else if (fieldType == CsvFieldDataType.NULLABLE_DOUBLE) {
            if (field.valueIsNull()) return false;
            return parseAndCompareDouble(field.getDoubleValue(), value) == 1;

        } else if (fieldType == CsvFieldDataType.DOUBLE) {
            return parseAndCompareDouble(field.getDoubleValue(), value) == 1;

        } else if (fieldType == CsvFieldDataType.STRING) {
            throw new IllegalStateException("Невозможно сравнить строку на больше/меньше");
        }

        throw new IllegalStateException("Неожиданный тип CSV поля: " + fieldType);
    }

    private boolean isLessThan(CsvField field, String value) {
        var fieldType = field.getFieldDataTypeType();

        if (fieldType == CsvFieldDataType.NULLABLE_INTEGER) {
            if (field.valueIsNull()) return false;
            return parseAndCompareInt(field.getIntValue(), value) == -1;

        } else if (fieldType == CsvFieldDataType.INTEGER) {
            return parseAndCompareInt(field.getIntValue(), value) == -1;

        } else if (fieldType == CsvFieldDataType.NULLABLE_DOUBLE) {
            if (field.valueIsNull()) return false;
            return parseAndCompareDouble(field.getDoubleValue(), value) == -1;

        } else if (fieldType == CsvFieldDataType.DOUBLE) {
            return parseAndCompareDouble(field.getDoubleValue(), value) == -1;

        } else if (fieldType == CsvFieldDataType.STRING) {
            throw new IllegalStateException("Невозможно сравнить строку на больше/меньше");
        }

        throw new IllegalStateException("Неожиданный тип CSV поля: " + fieldType);
    }

    private int parseAndCompareInt(int fieldValue, String value) {
        if (isNotNumeric(value))
            throw new IllegalStateException("Не возможно представить строку \"" + value + "\" как число");
        int second = Integer.parseInt(value);
        return Integer.compare(fieldValue, second);
    }

    private int parseAndCompareDouble(double fieldValue, String value) {
        if (isNotNumeric(value))
            throw new IllegalStateException("Не возможно представить строку \"" + value + "\" как число");
        double second = Double.parseDouble(value);
        return Double.compare(fieldValue, second);
    }

    private boolean isNotNumeric(String value) {
        return !isNumeric(value);
    }

    private boolean isNumeric(String value) {
        var chars = value.toCharArray();
        boolean wasDot = false;
        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];

            if (ch == '-') {
                if (i != 0) return false;
            } else if (ch == '.') {
                if (wasDot) return false;
                wasDot = true;
            } else if (!Character.isDigit(ch)) {
                return false;
            }
        }

        return true;
    }

    private boolean compareString(String first, String second) {
        return first.equals(second);
    }
}
