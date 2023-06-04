package org.example.command;

import lombok.Data;

@Data
public class SearchCommand {
    private final String airportNamePrefix;
    private final FilterCommand filterCommand;
}
