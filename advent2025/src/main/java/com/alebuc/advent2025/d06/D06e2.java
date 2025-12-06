package com.alebuc.advent2025.d06;

import com.alebuc.advent2025.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class D06e2 {
    public static void main(String[] args) {
        String fileName = "/ex06.txt";
//        String fileName = "/data06.txt";
        List<String> contentLines = new ArrayList<>(Utils.readResourceFileAsLines(fileName));
        System.out.println("Content lines: " + contentLines);

        List<Integer> columnSeparatorPositions = new ArrayList<>();
        for (int i = 0; i < contentLines.getFirst().length(); i++) {
            Set<Character> characters = new HashSet<>();
            for (String line : contentLines) {
                characters.add(line.charAt(i));
            }
            if (characters.size() == 1 && characters.contains(' ')) {
                columnSeparatorPositions.add(i);
            }
        }
        contentLines = contentLines.stream()
                .map(s -> {
                    StringBuilder sb = new StringBuilder(s);
                    for (int position : columnSeparatorPositions) {
                        sb.setCharAt(position, ';');
                    }
                    return sb.toString();
                })
                .collect(Collectors.toList());

        List<List<String>> homeworkContent = contentLines.stream()
                .map(line -> Stream.of(line.split(";"))
                        .filter(StringUtils::isNotBlank)
                        .collect(Collectors.toList()))
                .toList();
        System.out.println("Homework content: ");
        homeworkContent.forEach(System.out::println);

        List<BigInteger> operationResults = new ArrayList<>();
        int columnCount = homeworkContent.getFirst().size();
        for (int i = 0; i < columnCount; i++) {
            List<String> column = new ArrayList<>();
            for (List<String> line : homeworkContent) {
                column.add(line.get(i));
            }
            operationResults.add(computeColumn(column));
        }
        System.out.println("Operation results: " + operationResults);
        System.out.println("Sum: " + operationResults.stream().reduce(BigInteger::add).get());
    }

    private static BigInteger computeColumn(List<String> column) {
        List<BigInteger> columnValues = new ArrayList<>();
//        column = formatColumn(column);
        System.out.println(column);
        int columnWidth = column.getFirst().length();
        for (int j = 0; j < columnWidth; j++) {
            StringBuilder value = new StringBuilder();
            for (int i = 0; i < column.size() - 1; i++) {
                value.append(column.get(i).charAt(j));
            }
            columnValues.add(new BigInteger(value.toString().trim()));
        }
        System.out.println(columnValues);
        if (column.getLast().trim().equals("+")) {
            return columnValues.stream().reduce(BigInteger::add).get();
        }
        if (column.getLast().trim().equals("*")) {
            return columnValues.stream().reduce(BigInteger::multiply).get();
        }
        throw new IllegalArgumentException("Invalid operator: " + column.getLast());
    }

//    private static List<String> formatColumn(List<String> column) {
//        List<String> formattedColumn = new ArrayList<>();
//        for (int i = 0; i < column.size() - 1; i++) {
//            String formattedLine = column.get(i).replace(" ", "0");
//            formattedColumn.add(formattedLine);
//        }
//        formattedColumn.add(column.getLast());
//        return formattedColumn;
//    }
}
