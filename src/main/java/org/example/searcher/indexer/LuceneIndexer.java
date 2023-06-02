package org.example.searcher.indexer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.example.model.FileLine;
import org.example.reader.Reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class LuceneIndexer implements Indexer {

    private final static String AIRPORT_FIELD = "airport_name";
    private final static String FILE_POSITION = "document_position";

    private final Directory luceneDirectory;
    private IndexReader luceneReader;

    private final Reader reader;

    public LuceneIndexer(Reader reader) {
        this.reader = reader;
        luceneDirectory = new RAMDirectory();
    }

    @Override
    public void indexFile(String fileName) {
        List<FileLine> lines;
        Analyzer analyzer = new SimpleAnalyzer();
        try (reader) {
            reader.open(fileName, 2048);
            IndexWriter writer = new IndexWriter(luceneDirectory, new IndexWriterConfig(analyzer));

            while ((lines = reader.getFileLinesFormBuffer(fileName)) != null) {
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
    public List<Long> getIndexes(String value) {

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
                    .map(doc ->
                            doc.getField(FILE_POSITION)
                                    .numericValue()
                                    .longValue()
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
        int firstCommaIndex = content.indexOf(",");
        int secondCommaIndex = content.indexOf(",", firstCommaIndex + 1);

        var airportName = content.substring(firstCommaIndex + 1, secondCommaIndex)
                .trim()
                .toLowerCase();

        Document document = new Document();
        document.add(new StringField(AIRPORT_FIELD, airportName, Field.Store.NO));
        document.add(new StoredField(FILE_POSITION, line.getStartPosition()));

        return document;
    }
}
