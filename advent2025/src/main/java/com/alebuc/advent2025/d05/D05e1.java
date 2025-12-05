package com.alebuc.advent2025.d05;

import com.alebuc.advent2025.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class D05e1 {
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
                .collect(ArrayList::new, List::add, List::addAll);

        List<Long> availableIds = contentLines.stream()
                .dropWhile(StringUtils::isNotBlank)
                .skip(1)
                .map(Long::parseLong)
                .collect(ArrayList::new, List::add, List::addAll);

        Set<Long> availableFreshIds = new HashSet<>();
        for (long availableId : availableIds) {
            for (Range range : ranges) {
                if (range.isValueInBounds(availableId)) {
                    availableFreshIds.add(availableId);
                    break;
                }
            }
        }
        System.out.println("Available fresh ids: " + availableFreshIds);
        System.out.println("Count: " + availableFreshIds.size());
    }
}
