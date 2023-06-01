package org.example.reader;

import org.example.model.FileLine;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface Reader extends Closeable {
    void open(String filename, int bufferSize) throws FileNotFoundException;
    List<String> getLines(Collection<Long> positions) throws IOException;
    List<FileLine> getFileLinesFormBuffer(String fileName) throws IOException;
}
