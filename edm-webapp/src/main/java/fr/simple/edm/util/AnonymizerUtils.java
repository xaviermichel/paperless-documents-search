package fr.simple.edm.util;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class AnonymizerUtils {

    public static String MUSTASCHED_YEAR = "{year}";
    public static String MUSTASCHED_MONTH = "{month}";
    public static String MUSTASCHED_STR_MONTH = "{str(month)}";

    /**
     * Replace year (like 2012) in give string with {year}
     */
    public static String anonymizeYear(String stringToAnonymize) {
        for (Integer possibleYear : IntStream.range(2000, 2100).boxed().collect(toList())) {
            stringToAnonymize = stringToAnonymize.replaceAll(Pattern.quote(String.valueOf(possibleYear)), MUSTASCHED_YEAR);
        }
        return stringToAnonymize;
    }

    /**
     * Replace month (like 02 or 'février') in give string with {month} (or {str{month}}
     *
     * TODO : also anonymize month string
     */
    public static String anonymizeMonth(String stringToAnonymize) {
        for (Integer possibleMonth : IntStream.range(1, 13).boxed().collect(toList())) {
            stringToAnonymize = stringToAnonymize.replaceAll(Pattern.quote(String.format("%02d", possibleMonth)), MUSTASCHED_MONTH);
            stringToAnonymize = stringToAnonymize.replaceAll(Pattern.quote(LocalDate.of(2000, possibleMonth, 1).getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE)), MUSTASCHED_STR_MONTH);
        }
        return stringToAnonymize;
    }


    /**
     * Replace {year} mustache with last month year
     */
    public static String unanonymizeYear(String stringToUnanonymize) {
        LocalDate lastMonthDate = LocalDate.now().minusMonths(1);
        return stringToUnanonymize.replaceAll(Pattern.quote(MUSTASCHED_YEAR), String.valueOf(lastMonthDate.getYear()));
    }

    /**
     * Replace {month} mustache with last month year
     */
    public static String unanonymizeMonth(String stringToUnanonymize) {
        LocalDate lastMonthDate = LocalDate.now().minusMonths(1);
        stringToUnanonymize = stringToUnanonymize.replaceAll(Pattern.quote(MUSTASCHED_MONTH), String.format("%02d", lastMonthDate.getMonthValue()));
        return stringToUnanonymize.replaceAll(Pattern.quote(MUSTASCHED_STR_MONTH), lastMonthDate.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE));
    }

}
