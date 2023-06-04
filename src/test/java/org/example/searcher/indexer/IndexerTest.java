package org.example.searcher.indexer;

import lombok.SneakyThrows;
import org.example.model.FileLine;
import org.example.parser.CsvRowParser;
import org.example.parser.SimpleCsvRowParser;
import org.example.reader.CsvReader;
import org.example.reader.Reader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

class IndexerTest {

    private static Indexer indexer;

    @SneakyThrows
    private static Reader setUpReader() {
        Reader reader = Mockito.mock(CsvReader.class);

        var lines = List.of(
                "1,\"Goroka Airport\",\"Goroka\",\"Papua New Guinea\",\"GKA\",\"AYGA\",-6.081689834590001,145.391998291,5282,10,\"U\",\"Pacific/Port_Moresby\",\"airport\",\"OurAirports\"",
                "8,\"Godthaab / Nuuk Airport\",\"Godthaab\",\"Greenland\",\"GOH\",\"BGGH\",64.19090271,-51.6781005859,283,-3,\"E\",\"America/Godthab\",\"airport\",\"OurAirports\"",
                "664,\"Sandefjord Airport, Torp\",\"Sandefjord\",\"Norway\",\"TRF\",\"ENTO\",59.1866989136,10.258600235,286,1,\"E\",\"Europe/Oslo\",\"airport\",\"OurAirports\"",
                "5582,\"Mo i Rana Airport, Røssvoll\",\"Mo i Rana\",\"Norway\",\"MQN\",\"ENRA\",66.363899230957,14.301400184631,229,1,\"E\",\"Europe/Oslo\",\"airport\",\"OurAirports\"",
                "63,\"Gaspé (Michel-Pouliot) Airport\",\"Gaspe\",\"Canada\",\"YGP\",\"CYGP\",48.7752990723,-64.4785995483,112,-5,\"A\",\"America/Toronto\",\"airport\",\"OurAirports\"",
                "64,\"Geraldton Greenstone Regional Airport\",\"Geraldton\",\"Canada\",\"YGQ\",\"CYGQ\",49.77830123901367,-86.93939971923828,1144,-5,\"A\",\"America/Toronto\",\"airport\",\"OurAirports\"",
                "69,\"Gjoa Haven Airport\",\"Gjoa Haven\",\"Canada\",\"YHK\",\"CYHK\",68.635597229,-95.84970092770001,152,-7,\"A\",\"America/Edmonton\",\"airport\",\"OurAirports\"",
                "117,\"Greater Moncton International Airport\",\"Moncton\",\"Canada\",\"YQM\",\"CYQM\",46.11220169067383,-64.67859649658203,232,-4,\"A\",\"America/Halifax\",\"airport\",\"OurAirports\"",
                "5555,\"Guemar Airport\",\"Guemar\",\"Algeria\",\"ELU\",\"DAUO\",33.5113983154,6.77679014206,203,1,\"N\",\"Africa/Algiers\",\"airport\",\"OurAirports\"",
                "1502,\"Foggia \\\"Gino Lisa\\\" Airport\",\"Foggia\",\"Italy\",\"FOG\",\"LIBF\",41.432899,15.535,265,1,\"E\",\"Europe/Rome\",\"airport\",\"OurAirports\"",
                "1503,\"Taranto-Grottaglie \\\"Marcello Arlotta\\\" Airport\",\"Grottaglie\",\"Italy\",\"TAR\",\"LIBG\",40.517502,17.4032,215,1,\"E\",\"Europe/Rome\",\"airport\",\"OurAirports\"",
                "12047,\"Guangzhou MR Air Base\",\"Guanghua\",\"China\",\"LHK\",\"ZHGH\",32.389400482177734,111.69499969482422,0,\\N,\\N,\\N,\"airport\",\"OurAirports\""
        );

        Mockito.when(reader.getFileLinesFormBuffer())
                .thenReturn(List.of(
                        new FileLine(lines.get(0), 10, 0),
                        new FileLine(lines.get(1), 20, 0),
                        new FileLine(lines.get(2), 30, 0),
                        new FileLine(lines.get(3), 40, 0),
                        new FileLine(lines.get(4), 50, 0),
                        new FileLine(lines.get(5), 60, 0),
                        new FileLine(lines.get(6), 70, 0),
                        new FileLine(lines.get(7), 80, 0),
                        new FileLine(lines.get(8), 90, 0),
                        new FileLine(lines.get(9), 100, 0),
                        new FileLine(lines.get(10), 110, 0),
                        new FileLine(lines.get(11), 120, 0)
                )).thenReturn(null);

        return reader;
    }

    @BeforeAll
    public static void init() {
        Reader reader = setUpReader();
        CsvRowParser parser = new SimpleCsvRowParser();
        indexer = new SimpleIndexer(reader, parser);
        indexer.indexFile("");
    }

    @Test
    void testPrefixMo() {
        List<Long> indexes = indexer.getIndexes("Mo");
        Collections.sort(indexes);

        List<Long> expected = List.of(40L);

        Assertions.assertEquals(expected.size(), indexes.size());

        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i), indexes.get(i));
        }
    }

    @Test
    void testPrefixFoggiaBQ() {
        List<Long> indexes = indexer.getIndexes("Foggia \"");
        Collections.sort(indexes);

        List<Long> expected = List.of(100L);

        Assertions.assertEquals(expected.size(), indexes.size());

        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i), indexes.get(i));
        }
    }

    @Test
    void testPrefixGo() {
        List<Long> indexes = indexer.getIndexes("Go");
        Collections.sort(indexes);

        List<Long> expected = List.of(10L, 20L);

        Assertions.assertEquals(expected.size(), indexes.size());

        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i), indexes.get(i));
        }
    }

    @Test
    void testPrefixG() {
        List<Long> indexes = indexer.getIndexes("G");
        Collections.sort(indexes);

        List<Long> expected = List.of(10L, 20L, 50L, 60L, 70L, 80L, 90L, 120L);

        Assertions.assertEquals(expected.size(), indexes.size());

        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(expected.get(i), indexes.get(i));
        }
    }

}