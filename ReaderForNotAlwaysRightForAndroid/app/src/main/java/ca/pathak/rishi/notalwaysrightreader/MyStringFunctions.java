package ca.pathak.rishi.notalwaysrightreader;

import org.threeten.bp.LocalDate;

public class MyStringFunctions {

    static public String DateToStringWebsiteVersion (LocalDate date) {
        return date.getMonth() + " " + date.getDayOfMonth() + ", " + date.getYear();
    }

    static public String DateToStringDisplayVersion (LocalDate date) {
        return fix_capitalization(date.getDayOfWeek().toString()) + ", " + fix_capitalization(date.getMonth().toString())  + " " + date.getDayOfMonth() + ", " + date.getYear();
    }

    static public String DateToStringDisplayVersionShort (LocalDate date) {
        return fix_capitalization(date.getMonth().toString())  + " " + date.getDayOfMonth() + ", " + date.getYear();
    }

    static public String DateToStringStorageVersion (LocalDate date) {
        return fix_capitalization(date.getYear() + "-" + date.getMonthValue() + "-" + date.getDayOfMonth());
    }

    static public String fix_capitalization (String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}
