package fs;

public class Utilities {
    public static String padRight(String s, int n) {
        return String.format("%0$-"+n+"s", s);
    }
}
