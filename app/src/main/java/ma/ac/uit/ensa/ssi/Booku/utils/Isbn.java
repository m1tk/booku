package ma.ac.uit.ensa.ssi.Booku.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Isbn {
    static final Pattern isbn10_pattern = Pattern.compile("^(?:ISBN(?:-10)?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$)[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$");
    static final Pattern isbn13_pattern = Pattern.compile("^(?:ISBN(?:-13)?:? )?(?=[0-9]{13}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)97[89][- ]?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9]$");

    public static boolean is_valid(String match) {
        Matcher matcher10 = isbn10_pattern.matcher(match);
        Matcher matcher13 = isbn13_pattern.matcher(match);

        return matcher10.matches() || matcher13.matches();
    }
}
