import java.util.ArrayList;
import java.util.List;

class Advent0101 {
    public static void main(String[] args) {
        String text = "";

        String[] table = text.split(System.lineSeparator());
        List<String> integerStringList = new ArrayList<>();
        for (String line:table) {
            char first = getFirstDigit(line);
            char last = getLastDigit(line);
            String sum = new StringBuilder().append(first).append(last).toString();
            integerStringList.add(sum);
        }

        System.out.println(integerStringList + ", " + integerStringList.stream().map(Integer::valueOf).reduce(0, Integer::sum));





    }

    private static char getFirstDigit(String string) {
        for (int i = 0; i<string.length();i++) {
            char charac = string.charAt(i);
            if (Character.isDigit(charac)) {
                return charac;
            }
        }
        return 0;
    }
    private static char getLastDigit(String string) {
        for (int i = string.length()-1; i>=0;i--) {
            char charac = string.charAt(i);
            if (Character.isDigit(charac)) {
                return charac;
            }
        }
        return 0;
    }
}