package org.example.messager;

import org.example.model.Message;
import org.example.model.SearchResult;

import java.util.Scanner;

public class ConsoleMessenger implements Messenger {
    private static final String EXIT_COMMAND = "!quit";

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public Message requestFilter() {
        System.out.print("\nВведите фильтр: ");
        System.out.flush();
        var text = scanner.nextLine();
        return new Message(text, text.equals(EXIT_COMMAND));
    }

    @Override
    public Message requestNamePrefix() {
        System.out.print("\nВведите начало имени аэропорта: ");
        System.out.flush();
        var text = scanner.nextLine();
        return  new Message(text, text.equals(EXIT_COMMAND));
    }

    @Override
    public void invalidFilterMessage() {
        System.out.print("\nНедопустимый фильтр");
        System.out.flush();
    }

    @Override
    public void responseResults(SearchResult result) {
        result.getResult().forEach(System.out::println);
        System.out.printf(
                "Количество найденных строк: %d, время, затраченное на поиск: %d мс%n",
                result.getResultCount(),
                result.getSpentTimeMills()
        );
    }
}
