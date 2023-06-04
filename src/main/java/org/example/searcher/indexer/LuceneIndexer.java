package org.example.searcher.indexer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.example.model.file.FileLine;
import org.example.model.file.LineInfo;
import org.example.parser.CsvRowParser;
import org.example.reader.Reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class LuceneIndexer implements Indexer {

    private final static String AIRPORT_FIELD = "airport_name";
    private final static String LINE_POSITION = "line_position";
    private final static String LINE_LENGTH = "line_length";

    private final Directory luceneDirectory;
    private IndexReader luceneReader;

    private final Reader reader;

    private final CsvRowParser csvParser;

    public LuceneIndexer(Reader reader, CsvRowParser csvParser) {
        this.reader = reader;
        this.csvParser = csvParser;
        luceneDirectory = new RAMDirectory();
    }

    @Override
    public void indexFile(String fileName) {
        List<FileLine> lines;
        Analyzer analyzer = new SimpleAnalyzer();
        try (reader) {
            reader.open(fileName, 2048);
            IndexWriter writer = new IndexWriter(luceneDirectory, new IndexWriterConfig(analyzer));

            while ((lines = reader.getFileLinesFormBuffer()) != null) {
                for (FileLine line : lines) {
                    writer.addDocument(createDocument(line));
                }
            }

            writer.close();
            prepareIndexer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<LineInfo> findValuesByPrefix(String value) {

        try {
            IndexSearcher searcher = new IndexSearcher(luceneReader);
            Analyzer analyzer = new StandardAnalyzer();

            Query query = new QueryParser(AIRPORT_FIELD, analyzer).parse(value + "*");

            TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
            List<Document> documents = new ArrayList<>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                documents.add(searcher.doc(scoreDoc.doc));
            }

            return documents.stream()
                    .map(doc -> {
                                var linePos = doc.getField(LINE_POSITION)
                                        .numericValue()
                                        .longValue();
                                var lineLength = doc.getField(LINE_LENGTH)
                                        .numericValue()
                                        .intValue();
                                return new LineInfo(linePos, lineLength);
                            }
                    )
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void prepareIndexer() {
        try {
            luceneReader = DirectoryReader.open(luceneDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Document createDocument(FileLine line) {
        var content = line.getContent();
        var lineInfo = line.getLineInfo();
        var airportName = csvParser.parseField(content, 1);

        Document document = new Document();
        document.add(new StringField(AIRPORT_FIELD, airportName, Field.Store.NO));
        document.add(new StoredField(LINE_POSITION, lineInfo.getStartPosition()));
        document.add(new StoredField(LINE_POSITION, lineInfo.getLength()));

        return document;
    }
}
