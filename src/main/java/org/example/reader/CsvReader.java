package org.example.reader;

import org.example.model.FileLine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
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
    public List<String> getLines(Collection<Long> positions) throws IOException {
        ensureOpen();

        List<String> result = new ArrayList<>();
        try {
            var channel = reader.getChannel();

            for (Long position : positions) {
                if (channel.read(buffer, position) > 0) {
                    buffer.rewind();
                    CharBuffer charBuffer = getCharBuffer(buffer);
                    var line = getFirstLineFromBuffer(charBuffer);
                    result.add(line);
                    buffer.flip();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public List<FileLine> getFileLinesFormBuffer(String fileName) throws IOException {
        ensureOpen();

        var channel = reader.getChannel();

        try {
            if (channel.read(buffer) > 0) {
                buffer.rewind();
                CharBuffer charBuffer = getCharBuffer(buffer);
                var lastSepPos = updateBufferAndGetLastLineSeparatorPos(buffer, charBuffer);
                var result = getLinesFromBuffer(charBuffer, lastSepPos, cursorOffset);
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

    private String getFirstLineFromBuffer(CharBuffer charBuffer) {
        var start = 0;
        for (int i = 0; i < charBuffer.length(); i++) {
            if (charBuffer.get(i) == '\n') {
                if (start != 0) {
                    return charBuffer.subSequence(start, i).toString();
                } else {
                    var subBuff = charBuffer.subSequence(start, i);
                    var commaCounter = 0;
                    boolean wasOpenDoubleQuotes = false;
                    for (int j = subBuff.position(); j < subBuff.length(); j++) {
                        char ch = subBuff.get(j);
                        if (ch == '"') {
                            wasOpenDoubleQuotes = !wasOpenDoubleQuotes;
                        }
                        if (ch == ',' && !wasOpenDoubleQuotes) commaCounter++;
                    }
                    if (commaCounter == 13) {
                        return subBuff.toString();
                    }
                    start = i + 1;
                }
            }
        }
        return "";
    }

    private List<FileLine> getLinesFromBuffer(CharBuffer charBuffer, int lastSepIndex, long offset) {
        List<FileLine> result = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        var charBuffLastPos = 0;
        var fileOffset = offset;
        for (int i = 0; i < lastSepIndex + 1; i++) {
            if (charBuffer.get(i) == '\n') {
                builder.append(charBuffer, charBuffLastPos, i);
                replaceAll(builder, "\"", "");

                var byteBuff = getByteBuffer(charBuffer.subSequence(charBuffLastPos, i + 1));
                int lineLength = byteBuff.limit() - byteBuff.position();
                fileOffset += lineLength;

                result.add(new FileLine(builder.toString(), fileOffset, lineLength));
                charBuffLastPos = i + 1;
                builder.delete(0, builder.length());
            }
        }

        return result;
    }

    private void replaceAll(StringBuilder builder, String from, String to) {
        int index = 0;
        while ((index = builder.indexOf(from, index)) != -1) {
            builder.replace(index, index + from.length(), to);
            index += to.length();
        }
    }
}
