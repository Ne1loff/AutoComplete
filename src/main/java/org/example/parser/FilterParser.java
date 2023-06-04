package org.example.parser;

import org.example.command.FilterCommand;

public interface FilterParser {
    FilterCommand parse(String value);
    boolean isValidFilter(String value);
}
