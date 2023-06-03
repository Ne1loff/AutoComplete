package org.example.searcher.indexer;

import lombok.RequiredArgsConstructor;
import org.example.model.FileLine;
import org.example.reader.Reader;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
public class SimpleIndexer implements Indexer {
    static class SimpleIndex<T> {
        private static class SimpleIndexNode<T> {
            private int arrayCapacity = 256;
            private String[] indices = new String[arrayCapacity];
            private final Map<Integer, T> values = new HashMap<>();
            private int indicesCount = 0;
            boolean isReady = false;

            public void putValue(String index, T value) {
                if (isReady) return;

                index = index.toLowerCase();
                if (indicesCount == arrayCapacity) {
                    var oldArray = indices;
                    indices = new String[(arrayCapacity = (int) (arrayCapacity * 1.5))];
                    System.arraycopy(oldArray, 0, indices, 0, oldArray.length);
                }
                indices[indicesCount++] = index;
                values.put(index.hashCode(), value);
            }

            public void prepareIndex() {
                Arrays.parallelSort(indices, 0, indicesCount, String::compareTo);
                isReady = true;
            }

            public List<T> getValuesByIndexPrefix(String prefix) {
                if (!isReady) throw new RuntimeException("Indexer was not prepared");
                if (prefix.length() == 1) {
                    List<T> result = new ArrayList<>();
                    for (int i = 0; i < indicesCount; i++) {
                        result.add(values.get(indices[i].hashCode()));
                    }
                    return result;
                }

                prefix = prefix.toLowerCase();
                List<T> result = new ArrayList<>();
                int i = Arrays.binarySearch(
                        indices,
                        0,
                        indicesCount,
                        prefix,
                        (str, pref) -> str.startsWith(pref) ? 0 : str.compareTo(pref)
                );

                if (i < 0) return result;

                var start = -1;
                var end = -1;

                var prefixLastCharNumber = prefix.length() - 1;
                var prefixLastChar = prefix.charAt(prefixLastCharNumber);
                int j = i, k = i;
                while (start < 0 || end < 0) {
                    if (k == indicesCount - 1) {
                        end = k;
                    } else if (end < 0) {
                        k++;
                    }

                    if (j == 0) {
                        start = j;
                    } else if (start < 0) {
                        j--;
                    }

                    var left = indices[j];
                    var right = indices[k];

                    if (left.length() <= prefixLastCharNumber) {
                        start = j + 1;
                    }

                    if (left.charAt(prefixLastCharNumber) != prefixLastChar) {
                        start = j + 1;
                    }
                    if (right.charAt(prefixLastCharNumber) != prefixLastChar) {
                        end = k - 1;
                    }
                }

                for (int l = start; l <= end; l++) {
                    result.add(values.get(indices[l].hashCode()));
                }

                return result;
            }

        }

        private final Map<Character, SimpleIndexNode<T>> index = new HashMap<>();

        public void putValue(String key, T value) {
            key = key.toLowerCase();
            var node = index.computeIfAbsent(key.charAt(0), c -> new SimpleIndexNode<>());
            node.putValue(key, value);
        }

        public List<T> getValuesByIndexPrefix(String prefix) {
            prefix = prefix.toLowerCase();
            var node = index.get(prefix.charAt(0));
            if (node == null) return Collections.emptyList();
            return node.getValuesByIndexPrefix(prefix);
        }

        public void setReadyToWork() {
            index.values().forEach(SimpleIndexNode::prepareIndex);
        }
    }

    private final SimpleIndex<Long> index = new SimpleIndex<>();

    private final Reader reader;

    @Override
    public void indexFile(String fileName) {
        List<FileLine> lines;
        try (reader) {
            reader.open(fileName, 2048);
            while ((lines = reader.getFileLinesFormBuffer(fileName)) != null) {
                for (FileLine line : lines) {
                    var content = line.getContent();
                    var airportName = getName(content);
                    index.putValue(airportName, line.getStartPosition());
                }
            }

            index.setReadyToWork();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Long> getIndexes(String value) {
        return index.getValuesByIndexPrefix(value);
    }

    private String getName(String content) {
        int firstCommaIndex = content.indexOf(",");
        int secondCommaIndex = content.indexOf(",", firstCommaIndex + 1);

        return content.substring(firstCommaIndex + 1, secondCommaIndex)
                .trim();
    }
}
