package org.example.reader;

import org.example.model.file.FileLine;
import org.example.model.file.LineInfo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CsvReader implements Reader {

    private RandomAccessFile reader;
    private ByteBuffer buffer;

    private long cursorOffset = 0;
    private boolean fileIsOpen = false;


    @Override
    public void open(String filename, int bufferSize) throws FileNotFoundException {
        reader = new RandomAccessFile(filename, "r");
        buffer = ByteBuffer.allocate(bufferSize);
        cursorOffset = 0;
        fileIsOpen = true;
    }

    @Override
    public List<String> getLines(List<LineInfo> lines) throws IOException {
        ensureOpen();

        List<String> result = new ArrayList<>();
        try {
            var channel = reader.getChannel();

            for (LineInfo lineInfo : lines) {
                var newLimit = buffer.position() + lineInfo.getLength();
                if (newLimit > buffer.capacity()) {
                    readFromByteBuffer(buffer, result);
                    newLimit = buffer.position() + lineInfo.getLength();
                }
                buffer.limit(newLimit);
                if (channel.read(buffer, lineInfo.getStartPosition()) <= 0) {
                    readFromByteBuffer(buffer, result);
                }
            }

            if (buffer.position() > 0) {
                readFromByteBuffer(buffer, result);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }


    @Override
    public List<FileLine> getFileLinesFormBuffer() throws IOException {
        ensureOpen();

        var channel = reader.getChannel();

        try {
            if (channel.read(buffer) > 0) {
                buffer.rewind();
                CharBuffer charBuffer = getCharBuffer(buffer);
                var lastSepPos = updateBufferAndGetLastLineSeparatorPos(buffer, charBuffer);
                var result = getFileLinesFromCharBuffer(charBuffer, lastSepPos, cursorOffset);
                cursorOffset += buffer.capacity() - buffer.position();

                return result;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void close() throws IOException {
        if (reader != null) reader.close();
        fileIsOpen = false;
    }

    private void ensureOpen() throws IOException {
        if (!fileIsOpen) throw new IOException("File not open");
        if (reader == null) throw new IOException("File not open");
    }

    private CharBuffer getCharBuffer(ByteBuffer byteBuffer) {
        return StandardCharsets.UTF_8.decode(byteBuffer);
    }

    private ByteBuffer getByteBuffer(CharBuffer charBuffer) {
        return StandardCharsets.UTF_8.encode(charBuffer);
    }

    private void readFromByteBuffer(ByteBuffer buffer, List<String> result) {
        buffer.rewind();
        CharBuffer charBuffer = getCharBuffer(buffer);
        var line = getAllLinesFromCharBuffer(charBuffer);
        result.addAll(line);
        buffer.flip();
    }

    private int updateBufferAndGetLastLineSeparatorPos(
            ByteBuffer buffer,
            CharBuffer charBuffer
    ) {
        for (int i = charBuffer.length() - 1; i > 0; i--) {
            if (charBuffer.get(i) == '\n') {
                buffer.flip();
                var subCharBuff = charBuffer.subSequence(i + 1, charBuffer.length());
                buffer.put(getByteBuffer(subCharBuff));
                return i;
            }
        }
        return 0;
    }

    private List<String> getAllLinesFromCharBuffer(CharBuffer charBuffer) {
        List<String> result = new ArrayList<>();
        var start = 0;
        for (int i = 0; i < charBuffer.length(); i++) {
            if (charBuffer.get(i) == '\n') {
                result.add(charBuffer.subSequence(start, i).toString());
                start = i + 1;
            }
        }
        return result;
    }

    private List<FileLine> getFileLinesFromCharBuffer(CharBuffer charBuffer, int lastSepIndex, long offset) {
        List<FileLine> result = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        var charBuffLastPos = 0;
        var fileOffset = offset;
        for (int i = 0; i < lastSepIndex + 1; i++) {
            if (charBuffer.get(i) == '\n') {
                var subSeq = charBuffer.subSequence(charBuffLastPos, i + 1);

                builder.append(subSeq);
                builder.deleteCharAt(builder.length() - 1);

                var byteBuff = getByteBuffer(subSeq);
                int lineLength = byteBuff.limit() - byteBuff.position();

                result.add(new FileLine(builder.toString(), new LineInfo(fileOffset, lineLength)));

                fileOffset += lineLength;

                charBuffLastPos = i + 1;
                builder.delete(0, builder.length());
            }
        }

        return result;
    }
}
