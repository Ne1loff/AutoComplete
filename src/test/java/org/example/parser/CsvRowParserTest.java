package org.example.parser;

import org.example.model.AirportInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class CsvRowParserTest {

    private final CsvRowParser parser = new SimpleCsvRowParser();

    @Test
    void test2RowsParse() {
        var rows = List.of(
                "1,\"Goroka Airport\",\"Goroka\",\"Papua New Guinea\",\"GKA\",\"AYGA\",-6.081689834590001,145.391998291,5282,10,\"U\",\"Pacific/Port_Moresby\",\"airport\",\"OurAirports\"\n",
                "8,\"Godthaab / Nuuk Airport\",\"Godthaab\",\"Greenland\",\"GOH\",\"BGGH\",64.19090271,-51.6781005859,283,-3,\"E\",\"America/Godthab\",\"airport\",\"OurAirports\"\n"
        );
        List<AirportInfo> infos = parser.parseRows(rows);

        var expected = List.of(
                new String[]{"1", "Goroka Airport", "Goroka", "Papua New Guinea", "GKA", "AYGA", "-6.081689834590001", "145.391998291", "5282", "10", "U", "Pacific/Port_Moresby", "airport", "OurAirports"},
                new String[]{"8", "Godthaab / Nuuk Airport", "Godthaab", "Greenland", "GOH", "BGGH", "64.19090271", "-51.6781005859", "283", "-3", "E", "America/Godthab", "airport", "OurAirports"}
        );
        for (int i = 0; i < infos.size(); i++) {
            var arr = expected.get(i);
            var info = infos.get(i);
            for (int j = 0; j < 14; j++) {
                Assertions.assertEquals(arr[j], info.getField(j).getValueAsString());
            }
        }
    }

    @Test
    void test2RowsWithCommasInNameParse() {
        var rows = List.of(
                "664,\"Sandefjord Airport, Torp\",\"Sandefjord\",\"Norway\",\"TRF\",\"ENTO\",59.1866989136,10.258600235,286,1,\"E\",\"Europe/Oslo\",\"airport\",\"OurAirports\"\n",
                "5582,\"Mo i Rana Airport, Røssvoll\",\"Mo i Rana\",\"Norway\",\"MQN\",\"ENRA\",66.363899230957,14.301400184631,229,1,\"E\",\"Europe/Oslo\",\"airport\",\"OurAirports\"\n"
        );
        List<AirportInfo> infos = parser.parseRows(rows);
        var expected = List.of(
                new String[]{"664", "Sandefjord Airport, Torp", "Sandefjord", "Norway", "TRF", "ENTO", "59.1866989136", "10.258600235", "286", "1", "E", "Europe/Oslo", "airport", "OurAirports"},
                new String[]{"5582", "Mo i Rana Airport, Røssvoll", "Mo i Rana", "Norway", "MQN", "ENRA", "66.363899230957", "14.301400184631", "229", "1", "E", "Europe/Oslo", "airport", "OurAirports"}
        );
        for (int i = 0; i < infos.size(); i++) {
            var arr = expected.get(i);
            var info = infos.get(i);
            for (int j = 0; j < 14; j++) {
                Assertions.assertEquals(arr[j], info.getField(j).getValueAsString());
            }
        }
    }

    @Test
    void test2RowsWithManyDoubleQuotesParse() {
        var rows = List.of(
                "1502,\"Foggia \\\"Gino Lisa\\\" Airport\",\"Foggia\",\"Italy\",\"FOG\",\"LIBF\",41.432899,15.535,265,1,\"E\",\"Europe/Rome\",\"airport\",\"OurAirports\"\n",
                "1503,\"Taranto-Grottaglie \\\"Marcello Arlotta\\\" Airport\",\"Grottaglie\",\"Italy\",\"TAR\",\"LIBG\",40.517502,17.4032,215,1,\"E\",\"Europe/Rome\",\"airport\",\"OurAirports\"\n"
        );
        List<AirportInfo> infos = parser.parseRows(rows);
        var expected = List.of(
                new String[]{"1502", "Foggia \"Gino Lisa\" Airport", "Foggia", "Italy", "FOG", "LIBF", "41.432899", "15.535", "265", "1", "E", "Europe/Rome", "airport", "OurAirports"},
                new String[]{"1503", "Taranto-Grottaglie \"Marcello Arlotta\" Airport", "Grottaglie", "Italy", "TAR", "LIBG", "40.517502", "17.4032", "215", "1", "E", "Europe/Rome", "airport", "OurAirports"}
        );
        for (int i = 0; i < infos.size(); i++) {
            var arr = expected.get(i);
            var info = infos.get(i);
            for (int j = 0; j < 14; j++) {
                Assertions.assertEquals(arr[j], info.getField(j).getValueAsString());
            }
        }
    }


}