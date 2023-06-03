package org.example.parser;

import org.example.model.AirportInfo;
import org.example.model.CsvField;
import org.example.model.CsvFieldType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class CsvRowParserTest {

    private final CsvRowParser parser = new SimpleCsvRowParser();

    @Test
    void test2RowsParse() {
        var rows = List.of(
                "1,\"Goroka Airport\",\"Goroka\",\"Papua New Guinea\",\"GKA\",\"AYGA\",-6.081689834590001,145.391998291,5282,10,\"U\",\"Pacific/Port_Moresby\",\"airport\",\"OurAirports\"",
                "8,\"Godthaab / Nuuk Airport\",\"Godthaab\",\"Greenland\",\"GOH\",\"BGGH\",64.19090271,-51.6781005859,283,-3,\"E\",\"America/Godthab\",\"airport\",\"OurAirports\""
        );
        List<AirportInfo> infos = parser.parseRows(rows);

        var expected = List.of(
                AirportInfo.builder().name("Goroka Airport")
                        .field(new CsvField(1, CsvFieldType.INTEGER))
                        .field(new CsvField("Goroka Airport", CsvFieldType.STRING))
                        .field(new CsvField("Goroka", CsvFieldType.STRING))
                        .field(new CsvField("Papua New Guinea", CsvFieldType.STRING))
                        .field(new CsvField("GKA", CsvFieldType.STRING))
                        .field(new CsvField("AYGA", CsvFieldType.STRING))
                        .field(new CsvField(-6.081689834590001, CsvFieldType.DOUBLE))
                        .field(new CsvField(145.391998291, CsvFieldType.DOUBLE))
                        .field(new CsvField(5282, CsvFieldType.INTEGER))
                        .field(new CsvField(10.0, CsvFieldType.NULLABLE_DOUBLE))
                        .field(new CsvField("U", CsvFieldType.STRING))
                        .field(new CsvField("Pacific/Port_Moresby", CsvFieldType.STRING))
                        .field(new CsvField("airport", CsvFieldType.STRING))
                        .field(new CsvField("OurAirports", CsvFieldType.STRING))
                        .build(),
                AirportInfo.builder().name("Godthaab / Nuuk Airport")
                        .field(new CsvField(8, CsvFieldType.INTEGER))
                        .field(new CsvField("Godthaab / Nuuk Airport", CsvFieldType.STRING))
                        .field(new CsvField("Godthaab", CsvFieldType.STRING))
                        .field(new CsvField("Greenland", CsvFieldType.STRING))
                        .field(new CsvField("GOH", CsvFieldType.STRING))
                        .field(new CsvField("BGGH", CsvFieldType.STRING))
                        .field(new CsvField(64.19090271, CsvFieldType.DOUBLE))
                        .field(new CsvField(-51.6781005859, CsvFieldType.DOUBLE))
                        .field(new CsvField(283, CsvFieldType.INTEGER))
                        .field(new CsvField(-3.0, CsvFieldType.NULLABLE_DOUBLE))
                        .field(new CsvField("E", CsvFieldType.STRING))
                        .field(new CsvField("America/Godthab", CsvFieldType.STRING))
                        .field(new CsvField("airport", CsvFieldType.STRING))
                        .field(new CsvField("OurAirports", CsvFieldType.STRING))
                        .build()
        );

        for (int i = 0; i < infos.size(); i++) {
            var arr = expected.get(i);
            var info = infos.get(i);

            Assertions.assertEquals(arr.getName(), info.getName());
            for (int j = 0; j < 14; j++) {
                var infoField = info.getField(j);
                var expectedField = arr.getField(j);

                switch (expectedField.getFieldType()) {
                    case NULLABLE_INTEGER:
                    case INTEGER:
                        Assertions.assertEquals(expectedField.getIntValue(), infoField.getIntValue());
                        break;
                    case NULLABLE_DOUBLE:
                    case DOUBLE:
                        Assertions.assertEquals(expectedField.getDoubleValue(), infoField.getDoubleValue());
                        break;
                    case STRING:
                        Assertions.assertEquals(expectedField.getStringValue(), infoField.getStringValue());
                        break;
                }
            }
        }
    }

    @Test
    void test2RowsWithCommasInNameParse() {
        var rows = List.of(
                "664,\"Sandefjord Airport, Torp\",\"Sandefjord\",\"Norway\",\"TRF\",\"ENTO\",59.1866989136,10.258600235,286,1,\"E\",\"Europe/Oslo\",\"airport\",\"OurAirports\"",
                "5582,\"Mo i Rana Airport, Røssvoll\",\"Mo i Rana\",\"Norway\",\"MQN\",\"ENRA\",66.363899230957,14.301400184631,229,1,\"E\",\"Europe/Oslo\",\"airport\",\"OurAirports\""
        );
        List<AirportInfo> infos = parser.parseRows(rows);
        var expected = List.of(
                AirportInfo.builder().name("Sandefjord Airport, Torp")
                        .field(new CsvField(664, CsvFieldType.INTEGER))
                        .field(new CsvField("Sandefjord Airport, Torp", CsvFieldType.STRING))
                        .field(new CsvField("Sandefjord", CsvFieldType.STRING))
                        .field(new CsvField("Norway", CsvFieldType.STRING))
                        .field(new CsvField("TRF", CsvFieldType.STRING))
                        .field(new CsvField("ENTO", CsvFieldType.STRING))
                        .field(new CsvField(59.1866989136, CsvFieldType.DOUBLE))
                        .field(new CsvField(10.258600235, CsvFieldType.DOUBLE))
                        .field(new CsvField(286, CsvFieldType.INTEGER))
                        .field(new CsvField(1.0, CsvFieldType.NULLABLE_DOUBLE))
                        .field(new CsvField("E", CsvFieldType.STRING))
                        .field(new CsvField("Europe/Oslo", CsvFieldType.STRING))
                        .field(new CsvField("airport", CsvFieldType.STRING))
                        .field(new CsvField("OurAirports", CsvFieldType.STRING))
                        .build(),
                AirportInfo.builder().name("Mo i Rana Airport, Røssvoll")
                        .field(new CsvField(5582, CsvFieldType.INTEGER))
                        .field(new CsvField("Mo i Rana Airport, Røssvoll", CsvFieldType.STRING))
                        .field(new CsvField("Mo i Rana", CsvFieldType.STRING))
                        .field(new CsvField("Norway", CsvFieldType.STRING))
                        .field(new CsvField("MQN", CsvFieldType.STRING))
                        .field(new CsvField("ENRA", CsvFieldType.STRING))
                        .field(new CsvField(66.363899230957, CsvFieldType.DOUBLE))
                        .field(new CsvField(14.301400184631, CsvFieldType.DOUBLE))
                        .field(new CsvField(229, CsvFieldType.INTEGER))
                        .field(new CsvField(1.0, CsvFieldType.NULLABLE_DOUBLE))
                        .field(new CsvField("E", CsvFieldType.STRING))
                        .field(new CsvField("Europe/Oslo", CsvFieldType.STRING))
                        .field(new CsvField("airport", CsvFieldType.STRING))
                        .field(new CsvField("OurAirports", CsvFieldType.STRING))
                        .build()
        );

        for (int i = 0; i < infos.size(); i++) {
            var arr = expected.get(i);
            var info = infos.get(i);

            Assertions.assertEquals(arr.getName(), info.getName());
            for (int j = 0; j < 14; j++) {
                var infoField = info.getField(j);
                var expectedField = arr.getField(j);

                switch (expectedField.getFieldType()) {
                    case NULLABLE_INTEGER:
                    case INTEGER:
                        Assertions.assertEquals(expectedField.getIntValue(), infoField.getIntValue());
                        break;
                    case NULLABLE_DOUBLE:
                    case DOUBLE:
                        Assertions.assertEquals(expectedField.getDoubleValue(), infoField.getDoubleValue());
                        break;
                    case STRING:
                        Assertions.assertEquals(expectedField.getStringValue(), infoField.getStringValue());
                        break;
                }
            }
        }
    }

    @Test
    void test2RowsWithManyDoubleQuotesParse() {
        var rows = List.of(
                "1502,\"Foggia \\\"Gino Lisa\\\" Airport\",\"Foggia\",\"Italy\",\"FOG\",\"LIBF\",41.432899,15.535,265,1,\"E\",\"Europe/Rome\",\"airport\",\"OurAirports\"",
                "1503,\"Taranto-Grottaglie \\\"Marcello Arlotta\\\" Airport\",\"Grottaglie\",\"Italy\",\"TAR\",\"LIBG\",40.517502,17.4032,215,1,\"E\",\"Europe/Rome\",\"airport\",\"OurAirports\""
        );
        List<AirportInfo> infos = parser.parseRows(rows);
        var expected = List.of(
                AirportInfo.builder().name("Foggia \"Gino Lisa\" Airport")
                        .field(new CsvField(1502, CsvFieldType.INTEGER))
                        .field(new CsvField("Foggia \"Gino Lisa\" Airport", CsvFieldType.STRING))
                        .field(new CsvField("Foggia", CsvFieldType.STRING))
                        .field(new CsvField("Italy", CsvFieldType.STRING))
                        .field(new CsvField("FOG", CsvFieldType.STRING))
                        .field(new CsvField("LIBF", CsvFieldType.STRING))
                        .field(new CsvField(41.432899, CsvFieldType.DOUBLE))
                        .field(new CsvField(15.535, CsvFieldType.DOUBLE))
                        .field(new CsvField(265, CsvFieldType.INTEGER))
                        .field(new CsvField(1.0, CsvFieldType.NULLABLE_DOUBLE))
                        .field(new CsvField("E", CsvFieldType.STRING))
                        .field(new CsvField("Europe/Rome", CsvFieldType.STRING))
                        .field(new CsvField("airport", CsvFieldType.STRING))
                        .field(new CsvField("OurAirports", CsvFieldType.STRING))
                        .build(),
                AirportInfo.builder().name("Taranto-Grottaglie \"Marcello Arlotta\" Airport")
                        .field(new CsvField(1503, CsvFieldType.INTEGER))
                        .field(new CsvField("Taranto-Grottaglie \"Marcello Arlotta\" Airport", CsvFieldType.STRING))
                        .field(new CsvField("Grottaglie", CsvFieldType.STRING))
                        .field(new CsvField("Italy", CsvFieldType.STRING))
                        .field(new CsvField("TAR", CsvFieldType.STRING))
                        .field(new CsvField("LIBG", CsvFieldType.STRING))
                        .field(new CsvField(40.517502, CsvFieldType.DOUBLE))
                        .field(new CsvField(17.4032, CsvFieldType.DOUBLE))
                        .field(new CsvField(215, CsvFieldType.INTEGER))
                        .field(new CsvField(1.0, CsvFieldType.NULLABLE_DOUBLE))
                        .field(new CsvField("E", CsvFieldType.STRING))
                        .field(new CsvField("Europe/Rome", CsvFieldType.STRING))
                        .field(new CsvField("airport", CsvFieldType.STRING))
                        .field(new CsvField("OurAirports", CsvFieldType.STRING))
                        .build()
        );

        for (int i = 0; i < infos.size(); i++) {
            var arr = expected.get(i);
            var info = infos.get(i);

            Assertions.assertEquals(arr.getName(), info.getName());
            for (int j = 0; j < 14; j++) {
                var infoField = info.getField(j);
                var expectedField = arr.getField(j);

                switch (expectedField.getFieldType()) {
                    case NULLABLE_INTEGER:
                    case INTEGER:
                        Assertions.assertEquals(expectedField.getIntValue(), infoField.getIntValue());
                        break;
                    case NULLABLE_DOUBLE:
                    case DOUBLE:
                        Assertions.assertEquals(expectedField.getDoubleValue(), infoField.getDoubleValue());
                        break;
                    case STRING:
                        Assertions.assertEquals(expectedField.getStringValue(), infoField.getStringValue());
                        break;
                }
            }
        }
    }

}