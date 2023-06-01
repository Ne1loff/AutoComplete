package org.example.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.model.AirportInfo;

import java.util.function.Predicate;

@Data
@AllArgsConstructor
public class FilterCommand {
    private Predicate<AirportInfo> filter;
}
