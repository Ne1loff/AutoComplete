package org.example.parser;

import org.example.model.AirportInfo;
import org.example.model.CsvField;
import org.example.model.CsvFieldDataType;
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
                        .field(new CsvField(1, CsvFieldDataType.INTEGER))
                        .field(new CsvField("Goroka Airport", CsvFieldDataType.STRING))
                        .field(new CsvField("Goroka", CsvFieldDataType.STRING))
                        .field(new CsvField("Papua New Guinea", CsvFieldDataType.STRING))
                        .field(new CsvField("GKA", CsvFieldDataType.STRING))
                        .field(new CsvField("AYGA", CsvFieldDataType.STRING))
                        .field(new CsvField(-6.081689834590001, CsvFieldDataType.DOUBLE))
                        .field(new CsvField(145.391998291, CsvFieldDataType.DOUBLE))
                        .field(new CsvField(5282, CsvFieldDataType.INTEGER))
                        .field(new CsvField(10.0, CsvFieldDataType.NULLABLE_DOUBLE))
                        .field(new CsvField("U", CsvFieldDataType.STRING))
                        .field(new CsvField("Pacific/Port_Moresby", CsvFieldDataType.STRING))
                        .field(new CsvField("airport", CsvFieldDataType.STRING))
                        .field(new CsvField("OurAirports", CsvFieldDataType.STRING))
                        .build(),
                AirportInfo.builder().name("Godthaab / Nuuk Airport")
                        .field(new CsvField(8, CsvFieldDataType.INTEGER))
                        .field(new CsvField("Godthaab / Nuuk Airport", CsvFieldDataType.STRING))
                        .field(new CsvField("Godthaab", CsvFieldDataType.STRING))
                        .field(new CsvField("Greenland", CsvFieldDataType.STRING))
                        .field(new CsvField("GOH", CsvFieldDataType.STRING))
                        .field(new CsvField("BGGH", CsvFieldDataType.STRING))
                        .field(new CsvField(64.19090271, CsvFieldDataType.DOUBLE))
                        .field(new CsvField(-51.6781005859, CsvFieldDataType.DOUBLE))
                        .field(new CsvField(283, CsvFieldDataType.INTEGER))
                        .field(new CsvField(-3.0, CsvFieldDataType.NULLABLE_DOUBLE))
                        .field(new CsvField("E", CsvFieldDataType.STRING))
                        .field(new CsvField("America/Godthab", CsvFieldDataType.STRING))
                        .field(new CsvField("airport", CsvFieldDataType.STRING))
                        .field(new CsvField("OurAirports", CsvFieldDataType.STRING))
                        .build()
        );

        for (int i = 0; i < infos.size(); i++) {
            var arr = expected.get(i);
            var info = infos.get(i);

            Assertions.assertEquals(arr.getName(), info.getName());
            for (int j = 0; j < 14; j++) {
                var infoField = info.getField(j);
                var expectedField = arr.getField(j);


                boolean isInteger =
                        expectedField.getFieldDataTypeType() == CsvFieldDataType.NULLABLE_INTEGER
                        || expectedField.getFieldDataTypeType() == CsvFieldDataType.INTEGER;

                boolean isBoolean = !isInteger &&
                        (expectedField.getFieldDataTypeType() == CsvFieldDataType.NULLABLE_DOUBLE
                        || expectedField.getFieldDataTypeType() == CsvFieldDataType.DOUBLE);

                boolean isString = !isBoolean &&
                        expectedField.getFieldDataTypeType() == CsvFieldDataType.STRING;

                if (isInteger) {
                    Assertions.assertEquals(expectedField.getIntValue(), infoField.getIntValue());
                } else if (isBoolean) {
                    Assertions.assertEquals(expectedField.getDoubleValue(), infoField.getDoubleValue());
                } else if (isString) {
                    Assertions.assertEquals(expectedField.getStringValue(), infoField.getStringValue());
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
                        .field(new CsvField(664, CsvFieldDataType.INTEGER))
                        .field(new CsvField("Sandefjord Airport, Torp", CsvFieldDataType.STRING))
                        .field(new CsvField("Sandefjord", CsvFieldDataType.STRING))
                        .field(new CsvField("Norway", CsvFieldDataType.STRING))
                        .field(new CsvField("TRF", CsvFieldDataType.STRING))
                        .field(new CsvField("ENTO", CsvFieldDataType.STRING))
                        .field(new CsvField(59.1866989136, CsvFieldDataType.DOUBLE))
                        .field(new CsvField(10.258600235, CsvFieldDataType.DOUBLE))
                        .field(new CsvField(286, CsvFieldDataType.INTEGER))
                        .field(new CsvField(1.0, CsvFieldDataType.NULLABLE_DOUBLE))
                        .field(new CsvField("E", CsvFieldDataType.STRING))
                        .field(new CsvField("Europe/Oslo", CsvFieldDataType.STRING))
                        .field(new CsvField("airport", CsvFieldDataType.STRING))
                        .field(new CsvField("OurAirports", CsvFieldDataType.STRING))
                        .build(),
                AirportInfo.builder().name("Mo i Rana Airport, Røssvoll")
                        .field(new CsvField(5582, CsvFieldDataType.INTEGER))
                        .field(new CsvField("Mo i Rana Airport, Røssvoll", CsvFieldDataType.STRING))
                        .field(new CsvField("Mo i Rana", CsvFieldDataType.STRING))
                        .field(new CsvField("Norway", CsvFieldDataType.STRING))
                        .field(new CsvField("MQN", CsvFieldDataType.STRING))
                        .field(new CsvField("ENRA", CsvFieldDataType.STRING))
                        .field(new CsvField(66.363899230957, CsvFieldDataType.DOUBLE))
                        .field(new CsvField(14.301400184631, CsvFieldDataType.DOUBLE))
                        .field(new CsvField(229, CsvFieldDataType.INTEGER))
                        .field(new CsvField(1.0, CsvFieldDataType.NULLABLE_DOUBLE))
                        .field(new CsvField("E", CsvFieldDataType.STRING))
                        .field(new CsvField("Europe/Oslo", CsvFieldDataType.STRING))
                        .field(new CsvField("airport", CsvFieldDataType.STRING))
                        .field(new CsvField("OurAirports", CsvFieldDataType.STRING))
                        .build()
        );

        for (int i = 0; i < infos.size(); i++) {
            var arr = expected.get(i);
            var info = infos.get(i);

            Assertions.assertEquals(arr.getName(), info.getName());
            for (int j = 0; j < 14; j++) {
                var infoField = info.getField(j);
                var expectedField = arr.getField(j);

                boolean isInteger =
                        expectedField.getFieldDataTypeType() == CsvFieldDataType.NULLABLE_INTEGER
                        || expectedField.getFieldDataTypeType() == CsvFieldDataType.INTEGER;

                boolean isBoolean = !isInteger &&
                        (expectedField.getFieldDataTypeType() == CsvFieldDataType.NULLABLE_DOUBLE
                        || expectedField.getFieldDataTypeType() == CsvFieldDataType.DOUBLE);

                boolean isString = !isBoolean &&
                        expectedField.getFieldDataTypeType() == CsvFieldDataType.STRING;

                if (isInteger) {
                    Assertions.assertEquals(expectedField.getIntValue(), infoField.getIntValue());
                } else if (isBoolean) {
                    Assertions.assertEquals(expectedField.getDoubleValue(), infoField.getDoubleValue());
                } else if (isString) {
                    Assertions.assertEquals(expectedField.getStringValue(), infoField.getStringValue());
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
                        .field(new CsvField(1502, CsvFieldDataType.INTEGER))
                        .field(new CsvField("Foggia \"Gino Lisa\" Airport", CsvFieldDataType.STRING))
                        .field(new CsvField("Foggia", CsvFieldDataType.STRING))
                        .field(new CsvField("Italy", CsvFieldDataType.STRING))
                        .field(new CsvField("FOG", CsvFieldDataType.STRING))
                        .field(new CsvField("LIBF", CsvFieldDataType.STRING))
                        .field(new CsvField(41.432899, CsvFieldDataType.DOUBLE))
                        .field(new CsvField(15.535, CsvFieldDataType.DOUBLE))
                        .field(new CsvField(265, CsvFieldDataType.INTEGER))
                        .field(new CsvField(1.0, CsvFieldDataType.NULLABLE_DOUBLE))
                        .field(new CsvField("E", CsvFieldDataType.STRING))
                        .field(new CsvField("Europe/Rome", CsvFieldDataType.STRING))
                        .field(new CsvField("airport", CsvFieldDataType.STRING))
                        .field(new CsvField("OurAirports", CsvFieldDataType.STRING))
                        .build(),
                AirportInfo.builder().name("Taranto-Grottaglie \"Marcello Arlotta\" Airport")
                        .field(new CsvField(1503, CsvFieldDataType.INTEGER))
                        .field(new CsvField("Taranto-Grottaglie \"Marcello Arlotta\" Airport", CsvFieldDataType.STRING))
                        .field(new CsvField("Grottaglie", CsvFieldDataType.STRING))
                        .field(new CsvField("Italy", CsvFieldDataType.STRING))
                        .field(new CsvField("TAR", CsvFieldDataType.STRING))
                        .field(new CsvField("LIBG", CsvFieldDataType.STRING))
                        .field(new CsvField(40.517502, CsvFieldDataType.DOUBLE))
                        .field(new CsvField(17.4032, CsvFieldDataType.DOUBLE))
                        .field(new CsvField(215, CsvFieldDataType.INTEGER))
                        .field(new CsvField(1.0, CsvFieldDataType.NULLABLE_DOUBLE))
                        .field(new CsvField("E", CsvFieldDataType.STRING))
                        .field(new CsvField("Europe/Rome", CsvFieldDataType.STRING))
                        .field(new CsvField("airport", CsvFieldDataType.STRING))
                        .field(new CsvField("OurAirports", CsvFieldDataType.STRING))
                        .build()
        );

        for (int i = 0; i < infos.size(); i++) {
            var arr = expected.get(i);
            var info = infos.get(i);

            Assertions.assertEquals(arr.getName(), info.getName());
            for (int j = 0; j < 14; j++) {
                var infoField = info.getField(j);
                var expectedField = arr.getField(j);

                boolean isInteger =
                        expectedField.getFieldDataTypeType() == CsvFieldDataType.NULLABLE_INTEGER
                        || expectedField.getFieldDataTypeType() == CsvFieldDataType.INTEGER;

                boolean isBoolean = !isInteger &&
                        (expectedField.getFieldDataTypeType() == CsvFieldDataType.NULLABLE_DOUBLE
                        || expectedField.getFieldDataTypeType() == CsvFieldDataType.DOUBLE);

                boolean isString = !isBoolean &&
                        expectedField.getFieldDataTypeType() == CsvFieldDataType.STRING;

                if (isInteger) {
                    Assertions.assertEquals(expectedField.getIntValue(), infoField.getIntValue());
                } else if (isBoolean) {
                    Assertions.assertEquals(expectedField.getDoubleValue(), infoField.getDoubleValue());
                } else if (isString) {
                    Assertions.assertEquals(expectedField.getStringValue(), infoField.getStringValue());
                }
            }
        }
    }

}