package org.example.parser;

import org.example.model.AirportInfo;
import org.example.model.csv.CsvField;
import org.example.model.csv.CsvFieldDataType;
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
                new AirportInfo("Goroka Airport", new CsvField[]{
                        new CsvField(1, CsvFieldDataType.INTEGER),
                        new CsvField("Goroka Airport", CsvFieldDataType.STRING),
                        new CsvField("Goroka", CsvFieldDataType.STRING),
                        new CsvField("Papua New Guinea", CsvFieldDataType.STRING),
                        new CsvField("GKA", CsvFieldDataType.STRING),
                        new CsvField("AYGA", CsvFieldDataType.STRING),
                        new CsvField(-6.081689834590001, CsvFieldDataType.DOUBLE),
                        new CsvField(145.391998291, CsvFieldDataType.DOUBLE),
                        new CsvField(5282, CsvFieldDataType.INTEGER),
                        new CsvField(10.0, CsvFieldDataType.NULLABLE_DOUBLE),
                        new CsvField("U", CsvFieldDataType.STRING),
                        new CsvField("Pacific/Port_Moresby", CsvFieldDataType.STRING),
                        new CsvField("airport", CsvFieldDataType.STRING),
                        new CsvField("OurAirports", CsvFieldDataType.STRING)
                }),
                new AirportInfo("Godthaab / Nuuk Airport", new CsvField[]{
                        new CsvField(8, CsvFieldDataType.INTEGER),
                        new CsvField("Godthaab / Nuuk Airport", CsvFieldDataType.STRING),
                        new CsvField("Godthaab", CsvFieldDataType.STRING),
                        new CsvField("Greenland", CsvFieldDataType.STRING),
                        new CsvField("GOH", CsvFieldDataType.STRING),
                        new CsvField("BGGH", CsvFieldDataType.STRING),
                        new CsvField(64.19090271, CsvFieldDataType.DOUBLE),
                        new CsvField(-51.6781005859, CsvFieldDataType.DOUBLE),
                        new CsvField(283, CsvFieldDataType.INTEGER),
                        new CsvField(-3.0, CsvFieldDataType.NULLABLE_DOUBLE),
                        new CsvField("E", CsvFieldDataType.STRING),
                        new CsvField("America/Godthab", CsvFieldDataType.STRING),
                        new CsvField("airport", CsvFieldDataType.STRING),
                        new CsvField("OurAirports", CsvFieldDataType.STRING)
                })

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
                new AirportInfo("Sandefjord Airport, Torp", new CsvField[]{
                        new CsvField(664, CsvFieldDataType.INTEGER),
                        new CsvField("Sandefjord Airport, Torp", CsvFieldDataType.STRING),
                        new CsvField("Sandefjord", CsvFieldDataType.STRING),
                        new CsvField("Norway", CsvFieldDataType.STRING),
                        new CsvField("TRF", CsvFieldDataType.STRING),
                        new CsvField("ENTO", CsvFieldDataType.STRING),
                        new CsvField(59.1866989136, CsvFieldDataType.DOUBLE),
                        new CsvField(10.258600235, CsvFieldDataType.DOUBLE),
                        new CsvField(286, CsvFieldDataType.INTEGER),
                        new CsvField(1.0, CsvFieldDataType.NULLABLE_DOUBLE),
                        new CsvField("E", CsvFieldDataType.STRING),
                        new CsvField("Europe/Oslo", CsvFieldDataType.STRING),
                        new CsvField("airport", CsvFieldDataType.STRING),
                        new CsvField("OurAirports", CsvFieldDataType.STRING)
                }),

                new AirportInfo("Mo i Rana Airport, Røssvoll", new CsvField[]{
                        new CsvField(5582, CsvFieldDataType.INTEGER),
                        new CsvField("Mo i Rana Airport, Røssvoll", CsvFieldDataType.STRING),
                        new CsvField("Mo i Rana", CsvFieldDataType.STRING),
                        new CsvField("Norway", CsvFieldDataType.STRING),
                        new CsvField("MQN", CsvFieldDataType.STRING),
                        new CsvField("ENRA", CsvFieldDataType.STRING),
                        new CsvField(66.363899230957, CsvFieldDataType.DOUBLE),
                        new CsvField(14.301400184631, CsvFieldDataType.DOUBLE),
                        new CsvField(229, CsvFieldDataType.INTEGER),
                        new CsvField(1.0, CsvFieldDataType.NULLABLE_DOUBLE),
                        new CsvField("E", CsvFieldDataType.STRING),
                        new CsvField("Europe/Oslo", CsvFieldDataType.STRING),
                        new CsvField("airport", CsvFieldDataType.STRING),
                        new CsvField("OurAirports", CsvFieldDataType.STRING)
                })
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
                new AirportInfo("Foggia \"Gino Lisa\" Airport", new CsvField[]{
                        new CsvField(1502, CsvFieldDataType.INTEGER),
                        new CsvField("Foggia \"Gino Lisa\" Airport", CsvFieldDataType.STRING),
                        new CsvField("Foggia", CsvFieldDataType.STRING),
                        new CsvField("Italy", CsvFieldDataType.STRING),
                        new CsvField("FOG", CsvFieldDataType.STRING),
                        new CsvField("LIBF", CsvFieldDataType.STRING),
                        new CsvField(41.432899, CsvFieldDataType.DOUBLE),
                        new CsvField(15.535, CsvFieldDataType.DOUBLE),
                        new CsvField(265, CsvFieldDataType.INTEGER),
                        new CsvField(1.0, CsvFieldDataType.NULLABLE_DOUBLE),
                        new CsvField("E", CsvFieldDataType.STRING),
                        new CsvField("Europe/Rome", CsvFieldDataType.STRING),
                        new CsvField("airport", CsvFieldDataType.STRING),
                        new CsvField("OurAirports", CsvFieldDataType.STRING)
                }),

                new AirportInfo("Taranto-Grottaglie \"Marcello Arlotta\" Airport", new CsvField[]{
                        new CsvField(1503, CsvFieldDataType.INTEGER),
                        new CsvField("Taranto-Grottaglie \"Marcello Arlotta\" Airport", CsvFieldDataType.STRING),
                        new CsvField("Grottaglie", CsvFieldDataType.STRING),
                        new CsvField("Italy", CsvFieldDataType.STRING),
                        new CsvField("TAR", CsvFieldDataType.STRING),
                        new CsvField("LIBG", CsvFieldDataType.STRING),
                        new CsvField(40.517502, CsvFieldDataType.DOUBLE),
                        new CsvField(17.4032, CsvFieldDataType.DOUBLE),
                        new CsvField(215, CsvFieldDataType.INTEGER),
                        new CsvField(1.0, CsvFieldDataType.NULLABLE_DOUBLE),
                        new CsvField("E", CsvFieldDataType.STRING),
                        new CsvField("Europe/Rome", CsvFieldDataType.STRING),
                        new CsvField("airport", CsvFieldDataType.STRING),
                        new CsvField("OurAirports", CsvFieldDataType.STRING)
                })
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
