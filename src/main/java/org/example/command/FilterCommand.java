package org.example.command;

import lombok.AllArgsConstructor;
import org.example.model.AirportInfoLine;

import java.util.function.Predicate;

@AllArgsConstructor
public class FilterCommand {
    private Predicate<AirportInfoLine> filter;
}
