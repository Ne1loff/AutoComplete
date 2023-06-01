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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private CharBuffer getCharBuffer(ByteBuffer byteBuffer) {
        return StandardCharsets.UTF_8.decode(byteBuffer);
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

    private int updateBufferAndGetLastLineSeparatorPos(
            ByteBuffer buffer,
            CharBuffer charBuffer
    ) {
        for (int i = charBuffer.length() - 1; i > 0; i--) {
            if (charBuffer.get(i) == '\n') {
                buffer.flip();
                var subCharBuff = charBuffer.subSequence(i + 1, charBuffer.length());
                buffer.put(StandardCharsets.UTF_8.encode(subCharBuff));
                return i;
            }
        }
        return 0;
    }

    private String getFirstLineFromBuffer(CharBuffer charBuffer) {

        Pattern pattern = Pattern.compile("([^\\n$]*(?:,[^\\r\\n]*){13})");
        Matcher matcher = pattern.matcher(charBuffer);

        if (!matcher.find()) return "";

        return matcher.groupCount() > 1 ?
                matcher.group(1)
                : matcher.groupCount() == 0 ?
                "" : matcher.group();
    }

    private List<FileLine> getLinesFromBuffer(CharBuffer charBuffer, int lastSepIndex, long offset) {
        List<FileLine> result = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        var lastPos = 0;
        for (int i = 0; i < lastSepIndex + 1; i++) {
            if (charBuffer.get(i) == '\n') {
                builder.append(charBuffer, lastPos, i);
                replaceAll(builder, "\"", "");
                result.add(new FileLine(builder.toString(), offset + lastPos));

                lastPos = i + 1;
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
