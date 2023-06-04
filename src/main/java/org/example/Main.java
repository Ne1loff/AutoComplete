package org.example;

import org.example.messager.ConsoleMessenger;
import org.example.messager.Messenger;
import org.example.parser.CsvRowParser;
import org.example.parser.FilterParser;
import org.example.parser.FilterParserImpl;
import org.example.parser.SimpleCsvRowParser;
import org.example.reader.CsvReader;
import org.example.reader.Reader;
import org.example.searcher.Searcher;
import org.example.searcher.SearcherImpl;
import org.example.searcher.indexer.Indexer;
import org.example.searcher.indexer.SimpleIndexer;

public class Main {
    public static void main(String[] args) {
        String fileName = "airports.csv";
        if (args.length != 0) {
            fileName = args[0];
        }

        Reader reader = new CsvReader();
        CsvRowParser csvParser = new SimpleCsvRowParser();
        Indexer indexer = new SimpleIndexer(reader, csvParser);
        Searcher searcher = new SearcherImpl(indexer, reader, csvParser);

        Messenger messenger = new ConsoleMessenger();
        FilterParser filterParser = new FilterParserImpl();
        MainLooper looper = new MainLooper(filterParser, searcher, messenger);
        looper.start(fileName);
    }
}

// command: column[1]<10 & column[5]='GKA' || column[5]='GTA'
// command: column[1]<10 & column[5]='GKA'
// column[1]<>0 & column[2]<>'opa' & column[3]<>'3232' & column[4]<>0 & column[5]<>0 & column[6]<>0 & column[7]<>0 & column[8]<>0 & column[9]<>0 & column[10]<>0 & column[11]<>0 & column[12]<>0 & column[13]<>0 & column[14]<>0
