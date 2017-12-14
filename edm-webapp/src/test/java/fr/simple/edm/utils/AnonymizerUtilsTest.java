package fr.simple.edm.utils;

import fr.simple.edm.util.AnonymizerUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import static org.fest.assertions.api.Assertions.assertThat;

public class AnonymizerUtilsTest {

    @Test
    public void yearShouldDisappearFromString() {
        String anonymizeMe = "Hello, I'm born in 2089";

        anonymizeMe = AnonymizerUtils.anonymizeYear(anonymizeMe);

        assertThat(anonymizeMe).isEqualTo("Hello, I'm born in " + AnonymizerUtils.MUSTASCHED_YEAR);
    }

    @Test
    public void numericMonthShouldDisappearFromString() {
        String anonymizeMe = "Hello, I'm born in the month 08";

        anonymizeMe = AnonymizerUtils.anonymizeMonth(anonymizeMe);

        assertThat(anonymizeMe).isEqualTo("Hello, I'm born in the month " + AnonymizerUtils.MUSTASCHED_MONTH);
    }

    @Test
    public void alphaMonthShouldDisappearFromString() {
        String anonymizeMe = "Hello, I'm born in the month : août";

        anonymizeMe = AnonymizerUtils.anonymizeMonth(anonymizeMe);

        assertThat(anonymizeMe).isEqualTo("Hello, I'm born in the month : " + AnonymizerUtils.MUSTASCHED_STR_MONTH);
    }

    @Test
    public void yearShouldReAppearInString() {
        String unanonymizeMe = "Hello, I'm born in {year}";

        unanonymizeMe = AnonymizerUtils.unanonymizeYear(unanonymizeMe);

        LocalDate lastMonthDate = LocalDate.now().minusMonths(1);
        assertThat(unanonymizeMe).isEqualTo("Hello, I'm born in " + String.valueOf(lastMonthDate.getYear()));
    }

    @Test
    public void numericMonthShouldReAppearFromString() {
        String unanonymizeMe = "Hello, I'm born in the month " + AnonymizerUtils.MUSTASCHED_MONTH;

        unanonymizeMe = AnonymizerUtils.unanonymizeMonth(unanonymizeMe);

        LocalDate lastMonthDate = LocalDate.now().minusMonths(1);
        assertThat(unanonymizeMe).isEqualTo("Hello, I'm born in the month " + String.format("%02d", lastMonthDate.getMonthValue()));
    }

    @Test
    public void alphaMonthShouldReAppearFromString() {
        String unanonymizeMe = "Hello, I'm born in the month " + AnonymizerUtils.MUSTASCHED_STR_MONTH;

        unanonymizeMe = AnonymizerUtils.unanonymizeMonth(unanonymizeMe);

        LocalDate lastMonthDate = LocalDate.now().minusMonths(1);
        assertThat(unanonymizeMe).isEqualTo("Hello, I'm born in the month " + lastMonthDate.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE));
    }

    @Test
    public void realFullStringAnonymize() {
        String anonymizeMe = "I wrote this class in septembre (09 !) 2017, it sounds a long time ago now...";

        anonymizeMe = AnonymizerUtils.anonymizeYear(anonymizeMe);
        anonymizeMe = AnonymizerUtils.anonymizeMonth(anonymizeMe);

        assertThat(anonymizeMe).isEqualTo("I wrote this class in " + AnonymizerUtils.MUSTASCHED_STR_MONTH + " (" + AnonymizerUtils.MUSTASCHED_MONTH + " !) " + AnonymizerUtils.MUSTASCHED_YEAR + ", it sounds a long time ago now...");
    }

    @Test
    public void realFullStringUnanonymize() {
        String anonymizeMe = "I wrote this class in " + AnonymizerUtils.MUSTASCHED_STR_MONTH + " (" + AnonymizerUtils.MUSTASCHED_MONTH + " !) " + AnonymizerUtils.MUSTASCHED_YEAR + ", it sounds a long time ago now...";

        anonymizeMe = AnonymizerUtils.unanonymizeMonth(anonymizeMe);
        anonymizeMe = AnonymizerUtils.unanonymizeYear(anonymizeMe);

        LocalDate lastMonthDate = LocalDate.now().minusMonths(1);
        assertThat(anonymizeMe).isEqualTo("I wrote this class in " + lastMonthDate.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE) + " (" + String.format("%02d", lastMonthDate.getMonthValue()) + " !) " + String.valueOf(lastMonthDate.getYear()) + ", it sounds a long time ago now...");
    }
}
