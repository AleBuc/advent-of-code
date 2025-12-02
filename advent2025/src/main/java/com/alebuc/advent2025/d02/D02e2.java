package com.alebuc.advent2025.d02;

import com.alebuc.advent2025.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class D02e2 {
    public static void main(String[] args) {
        String fileName = "/ex02.txt";
//        String fileName = "/data02.txt";
        List<String> contentLines = new ArrayList<>(Utils.readResourceFileAsLines(fileName));
        System.out.println("Content lines: " + contentLines);
        contentLines = contentLines.stream()
                .flatMap(line -> Stream.of(line.split(",")))
                .filter(StringUtils::isNotBlank)
                .toList();
        List<Combination> combinations = contentLines.stream()
                .map(s -> {
                    String[] parts = s.split("-");
                    Bound b1 = new Bound(parts[0]);
                    Bound b2 = new Bound(parts[1]);
                    return new Combination(b1, b2);
                })
                .toList();
        System.out.println("Combinations: " + combinations);

        Map<Combination, Set<String>> refListByCombination = new HashMap<>();
        System.out.println("RefLists :");
        for (Combination combination : combinations) {
            findMatchingValues(combination, refListByCombination);
            System.out.println(combination + " : " + refListByCombination.get(combination));
        }
        long totalCount = refListByCombination.values().stream()
                .flatMapToLong(strings -> strings.stream().mapToLong(Long::parseLong))
                .sum();

        System.out.println("Total count: " + totalCount);
    }

    private static void findMatchingValues(Combination combination, Map<Combination, Set<String>> refListByCombination) {
        long start = Long.parseLong(combination.b1().value());
        long end = Long.parseLong(combination.b2().value());
        Set<String> refList = new LinkedHashSet<>();
        for (long value = start; value <= end; value++) {
            String valueString = Long.toString(value);
            for (int patternLength = 1; patternLength <= valueString.length() / 2; patternLength++) {
                //avoid partial patterns
                if (valueString.length() % patternLength != 0) {
                    continue;
                }
                if (isRepeatingPattern(valueString, patternLength)) {
                    refList.add(valueString);
                    break;
                }
            }
        }
        refListByCombination.put(combination, refList);
    }

    private static boolean isRepeatingPattern(String value, int patternLength) {
        String pattern = value.substring(0, patternLength);
        for (int i = patternLength; i < value.length(); i += patternLength) {
            int end = Math.min(i + patternLength, value.length());
            if (!value.substring(i, end).equals(pattern.substring(0, end - i))) {
                return false;
            }
        }
        return true;
    }
}
