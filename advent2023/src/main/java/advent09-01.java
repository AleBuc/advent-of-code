import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Advent0901 {
    public static void main(String[] args) {
        /*String input = "";*/
        String input = "";
        String[] parts = input.split("\n");

        List<List<Long>> entriesLines = new ArrayList<>();
        for (String part : parts) {
            List<Long> lineContent = Arrays.stream(part.split(" ")).map(Long::parseLong).toList();
            entriesLines.add(lineContent);
        }

        List<Long> extraValues = new ArrayList<>();
        for (List<Long> entries : entriesLines) {
            Long value = analyseData(entries);
            extraValues.add(value);
            System.out.println(entries + " : " + value);
        }
        Long sum = extraValues.stream().reduce(0L, Long::sum);
        System.out.println("Sum: " + sum);


    }

    private static Long analyseData(List<Long> entries) {
        List<Long> substracts = new ArrayList<>();
        for (int i = 1; i < entries.size(); i++) {
            substracts.add(entries.get(i) - entries.get(i - 1));
        }
        Long result;
        if (substracts.stream().distinct().count() > 1) {
            result = analyseData(substracts);
        } else {
            result = substracts.getFirst();
        }
        return entries.getLast() + result;

    }

}