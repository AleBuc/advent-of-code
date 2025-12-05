package com.alebuc.advent2025.d05;

import com.alebuc.advent2025.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class D05e2 {
    public static void main(String[] args) {
        String fileName = "/ex05.txt";
//        String fileName = "/data05.txt";
        List<String> contentLines = new ArrayList<>(Utils.readResourceFileAsLines(fileName));
        System.out.println("Content lines: " + contentLines);

        List<Range> ranges = contentLines.stream()
                .takeWhile(StringUtils::isNotBlank)
                .map(s -> {
                    String[] split = s.split("-");
                    return new Range(Long.parseLong(split[0]), Long.parseLong(split[1]));
                })
                .sorted()
                .collect(ArrayList::new, List::add, List::addAll);

        System.out.println("Ranges: " + ranges);

        List<Range> availableFreshRanges = mergeRanges(ranges);
        System.out.println("Available fresh ranges: " + availableFreshRanges);
        long count = availableFreshRanges.stream()
                .map(Range::getSize)
                .reduce(0L, Long::sum);
        System.out.println("Count: " + count);
    }

    private static List<Range> mergeRanges(List<Range> ranges) {
        List<Range> result = new ArrayList<>();
        Range current = new Range(ranges.getFirst().getA(), ranges.getFirst().getB());
        for (int i = 1; i < ranges.size(); i++) {
            Range next = ranges.get(i);
            if (next.getA() <= current.getB() + 1) {
                long newB = Math.max(next.getB(), current.getB());
                current.setB(newB);
            } else {
                result.add(current);
                current = new Range(next.getA(), next.getB());
            }
        }
        result.add(current);
        return result;
    }
}
