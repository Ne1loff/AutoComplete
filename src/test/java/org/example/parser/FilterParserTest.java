package org.example.parser;

import org.example.model.AirportInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class FilterParserTest {
    private static final List<AirportInfo> source = new ArrayList<>();
    private static final CsvRowParser csvRowParser = new SimpleCsvRowParser();

    private final FilterParser filterParser = new FilterParserImpl();


    @BeforeAll
    public static void init() {
        source.addAll(csvRowParser.parseRows(List.of(
                "1,\"Goroka Airport\",\"Goroka\",\"Papua New Guinea\",\"GKA\",\"AYGA\",-6.081689834590001,145.391998291,5282,10,\"U\",\"Pacific/Port_Moresby\",\"airport\",\"OurAirports\"\n",
                "8,\"Godthaab / Nuuk Airport\",\"Godthaab\",\"Greenland\",\"GOH\",\"BGGH\",64.19090271,-51.6781005859,283,-3,\"E\",\"America/Godthab\",\"airport\",\"OurAirports\"\n",
                "63,\"Gaspé (Michel-Pouliot) Airport\",\"Gaspe\",\"Canada\",\"YGP\",\"CYGP\",48.7752990723,-64.4785995483,112,-5,\"A\",\"America/Toronto\",\"airport\",\"OurAirports\"\n",
                "64,\"Geraldton Greenstone Regional Airport\",\"Geraldton\",\"Canada\",\"YGQ\",\"CYGQ\",49.77830123901367,-86.93939971923828,1144,-5,\"A\",\"America/Toronto\",\"airport\",\"OurAirports\"\n",
                "69,\"Gjoa Haven Airport\",\"Gjoa Haven\",\"Canada\",\"YHK\",\"CYHK\",68.635597229,-95.84970092770001,152,-7,\"A\",\"America/Edmonton\",\"airport\",\"OurAirports\"",
                "117,\"Greater Moncton International Airport\",\"Moncton\",\"Canada\",\"YQM\",\"CYQM\",46.11220169067383,-64.67859649658203,232,-4,\"A\",\"America/Halifax\",\"airport\",\"OurAirports\"\n",
                "5555,\"Guemar Airport\",\"Guemar\",\"Algeria\",\"ELU\",\"DAUO\",33.5113983154,6.77679014206,203,1,\"N\",\"Africa/Algiers\",\"airport\",\"OurAirports\"\n",
                "12047,\"Guangzhou MR Air Base\",\"Guanghua\",\"China\",\"LHK\",\"ZHGH\",32.389400482177734,111.69499969482422,0,\\N,\\N,\\N,\"airport\",\"OurAirports\"\n"
        )));
    }

    @Test
    void testCol1LessThan70AndCol7GreaterThan50() {
        var filterCommand = filterParser.parse("column[1] < 70 & column[7] > 50.0");
        var names = source.stream()
                .filter(filterCommand.getFilter())
                .map(AirportInfo::getName)
                .toArray(String[]::new);
        var expected = new String[]{"Godthaab / Nuuk Airport", "Gjoa Haven Airport"};
        Assertions.assertArrayEquals(expected, names);
    }

    @Test
    void testCol1LessThan70OrCol7GreaterThan50() {
        var filterCommand = filterParser.parse("column[1] < 70 || column[7] > 50.0");
        var names = source.stream()
                .filter(filterCommand.getFilter())
                .map(AirportInfo::getName)
                .toArray(String[]::new);
        var expected = new String[]{
                "Goroka Airport",
                "Godthaab / Nuuk Airport",
                "Gaspé (Michel-Pouliot) Airport",
                "Geraldton Greenstone Regional Airport",
                "Gjoa Haven Airport"
        };
        Assertions.assertArrayEquals(expected, names);
    }

    @Test
    void testCol1LessThan70OrCol7LessThan50() {
        var filterCommand = filterParser.parse("column[1] < 70 || column[7] < 50.0");
        var names = source.stream()
                .filter(filterCommand.getFilter())
                .map(AirportInfo::getName)
                .toArray(String[]::new);
        var expected = new String[]{
                "Goroka Airport",
                "Godthaab / Nuuk Airport",
                "Gaspé (Michel-Pouliot) Airport",
                "Geraldton Greenstone Regional Airport",
                "Gjoa Haven Airport",
                "Greater Moncton International Airport",
                "Guemar Airport",
                "Guangzhou MR Air Base",
        };
        Assertions.assertArrayEquals(expected, names);
    }

    @Test
    void testCol4EqCanada() {
        var filterCommand = filterParser.parse("column[4] = 'Canada'");
        var names = source.stream()
                .filter(filterCommand.getFilter())
                .map(AirportInfo::getName)
                .toArray(String[]::new);
        var expected = new String[]{
                "Gaspé (Michel-Pouliot) Airport",
                "Geraldton Greenstone Regional Airport",
                "Gjoa Haven Airport",
                "Greater Moncton International Airport",
        };
        Assertions.assertArrayEquals(expected, names);
    }

    @Test
    void testCol4EqCanadaOrCol4EqChinaAndCol7GreaterThan30AdnCol7LessThan50() {
        var filterCommand = filterParser.parse("column[4] = 'Canada' || column[4] = 'China' & column[7] > 30.0 & column[7] < 50.0");
        var names = source.stream()
                .filter(filterCommand.getFilter())
                .map(AirportInfo::getName)
                .toArray(String[]::new);
        var expected = new String[]{
                "Gaspé (Michel-Pouliot) Airport",
                "Geraldton Greenstone Regional Airport",
                "Gjoa Haven Airport",
                "Greater Moncton International Airport",
                "Guangzhou MR Air Base",
        };
        Assertions.assertArrayEquals(expected, names);
    }

    @Test
    void testOpBrCol4EqCanadaOrCol4EqChinaClBrAndCol7GreaterThan30AdnCol7LessThan50() {
        var filterCommand = filterParser.parse("(column[4] = 'Canada' || column[4] = 'China') & column[7] > 30.0 & column[7] < 50.0");
        var names = source.stream()
                .filter(filterCommand.getFilter())
                .map(AirportInfo::getName)
                .toArray(String[]::new);
        var expected = new String[]{
                "Gaspé (Michel-Pouliot) Airport",
                "Geraldton Greenstone Regional Airport",
                "Greater Moncton International Airport",
                "Guangzhou MR Air Base",
        };
        Assertions.assertArrayEquals(expected, names);
    }

    @Test
    void testOpBrCol4EqCanadaOrCol4EqChinaClBrAndCol7GreaterThan30AdnCol7LessThan49() {
        var filterCommand = filterParser.parse("(column[4] = 'Canada' || column[4] = 'China') & column[7] > 30.0 & column[7] < 49.0");
        var names = source.stream()
                .filter(filterCommand.getFilter())
                .map(AirportInfo::getName)
                .toArray(String[]::new);
        var expected = new String[]{
                "Gaspé (Michel-Pouliot) Airport",
                "Greater Moncton International Airport",
                "Guangzhou MR Air Base",
        };
        Assertions.assertArrayEquals(expected, names);
    }
}