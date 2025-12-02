package com.alebuc.advent2025.d02;

import com.alebuc.advent2025.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class D02e1 {
    public static void main(String[] args) {
//        String fileName = "/ex02.txt";
        String fileName = "/data02.txt";
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

        Map<Combination, List<String>> refListByCombination = new HashMap<>();
        for (Combination combination : combinations) {
            List<String> refList = new ArrayList<>();
            Bound b1 = combination.b1();
            Bound b2 = combination.b2();
            if (b1.isAPair()) {
                String ref = b1.ref();
                addToRefList(combination, ref, b2, refList);
            }
            if (!b1.isAPair() && b1.value().length() == b2.value().length()) {
                //NOOP
                continue;
            }
            if (!b1.isAPair()){
                String ref = buildUpperRef(b1);
                addToRefList(combination, ref, b2, refList);
            }
            refListByCombination.put(combination, refList);
        }
        System.out.println("RefLists :");
        refListByCombination.forEach((key, value) -> System.out.println(key + " : " + value));
        long totalCount = refListByCombination.values().stream()
                .flatMapToLong(strings -> strings.stream().mapToLong(Long::parseLong))
                .sum();
        System.out.println("Total count: " + totalCount);
    }

    private static String buildUpperRef(Bound b1) {
        if (b1.ref().equals("0")) {
            return "1";
        }
        return "1" + "0".repeat(b1.ref().length());
    }

    private static void addToRefList(Combination combination, String ref, Bound b2, List<String> refList) {
        String b2ref = b2.isAPair() ? b2.ref() : buildUpperRef(b2);
        for (String refToTest = ref; Long.parseLong(refToTest) <= Long.parseLong(b2ref); refToTest = incrementString(refToTest)) {
            String valueToTest = refToTest + refToTest;
            if (combination.isValueInBounds(valueToTest)) {
                refList.add(valueToTest);
            }
        }
    }

    private static String incrementString(String refToTest) {
        return Long.toString(Long.parseLong(refToTest) + 1);
    }
}
