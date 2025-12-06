import java.util.ArrayList;
import java.util.List;

class Advent0102 {
    public static void main(String[] args) {
        String text = "";

        String[] table = text.split(System.lineSeparator());
        List<String> integerStringList = new ArrayList<>();
        for (String line:table) {
            int first = getFirstDigit(line);
            int last = getLastDigit(line);
            String sum = new StringBuilder().append(first).append(last).toString();
            integerStringList.add(sum);
            System.out.println(sum);
        }

        System.out.println(integerStringList + ", " + integerStringList.stream().map(Integer::valueOf).reduce(0, Integer::sum));





    }

    private static int getFirstDigit(String string) {
        for (int i = 0; i<string.length();i++) {
            char charac = string.charAt(i);
            if (Character.isDigit(charac)) {
                return Integer.parseInt(String.valueOf(charac));
            } else {
                if ((i+2 <string.length()) && string.substring(i, i+3).equals("one")){
                    return Numbers.one.number;
                }
                if ((i+2 <string.length()) && string.substring(i, i+3).equals("two")){
                    return Numbers.two.number;
                }
                if ((i+4 <string.length()) && string.substring(i, i+5).equals("three")){
                    return Numbers.three.number;
                }
                if ((i+3 <string.length()) && string.substring(i, i+4).equals("four")){
                    return Numbers.four.number;
                }
                if ((i+3 <string.length()) && string.substring(i, i+4).equals("five")){
                    return Numbers.five.number;
                }
                if ((i+2 <string.length()) && string.substring(i, i+3).equals("six")){
                    return Numbers.six.number;
                }
                if ((i+4 <string.length()) && string.substring(i, i+5).equals("seven")){
                    return Numbers.seven.number;
                }
                if ((i+4 <string.length()) && string.substring(i, i+5).equals("eight")){
                    return Numbers.eight.number;
                }
                if ((i+3 <string.length()) && string.substring(i, i+4).equals("nine")){
                    return Numbers.nine.number;
                }
            }

        }
        return 0;
    }
    private static int getLastDigit(String string) {
        for (int i = string.length()-1; i>=0;i--) {
            char charac = string.charAt(i);
            if (Character.isDigit(charac)) {
                return Integer.parseInt(String.valueOf(charac));
            } else {
                if ((i-2 >= 0) && string.substring(i-2, i+1).equals("one")){
                    return Numbers.one.number;
                }
                if ((i-2 >= 0) && string.substring(i-2, i+1).equals("two")){
                    return Numbers.two.number;
                }
                if ((i-4 >= 0) && string.substring(i-4, i+1).equals("three")){
                    return Numbers.three.number;
                }
                if ((i-3 >= 0) && string.substring(i-3, i+1).equals("four")){
                    return Numbers.four.number;
                }
                if ((i-3 >= 0) && string.substring(i-3, i+1).equals("five")){
                    return Numbers.five.number;
                }
                if ((i-2 >= 0) && string.substring(i-2, i+1).equals("six")){
                    return Numbers.six.number;
                }
                if ((i-4 >= 0) && string.substring(i-4, i+1).equals("seven")){
                    return Numbers.seven.number;
                }
                if ((i-4 >= 0) && string.substring(i-4, i+1).equals("eight")){
                    return Numbers.eight.number;
                }
                if ((i-3 >= 0) && string.substring(i-3, i+1).equals("nine")){
                    return Numbers.nine.number;
                }
            }
        }
        return 0;
    }

    enum Numbers {
        one(1),
        two(2),
        three(3),
        four(4),
        five(5),
        six(6),
        seven(7),
        eight(8),
        nine(9);
        final int number;
        Numbers(int number){
            this.number = number;
        }

    }
}