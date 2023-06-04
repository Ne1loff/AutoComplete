package org.example.reader;

import org.example.model.file.FileLine;
import org.example.model.file.LineInfo;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface Reader extends Closeable {
    void open(String filename, int bufferSize) throws FileNotFoundException;

    List<String> getLines(List<LineInfo> lines) throws IOException;

    List<FileLine> getFileLinesFormBuffer() throws IOException;
}
