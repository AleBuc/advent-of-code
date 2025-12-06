package com.alebuc.advent2025.d06;

import com.alebuc.advent2025.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class D06e1 {
    public static void main(String[] args) {
//        String fileName = "/ex06.txt";
        String fileName = "/data06.txt";
        List<String> contentLines = new ArrayList<>(Utils.readResourceFileAsLines(fileName));
        System.out.println("Content lines: " + contentLines);

        List<List<String>> homeworkContent = contentLines.stream()
                .map(line -> Stream.of(line.split(" "))
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

    private static BigInteger computeColumn(List<String> column){
        List<BigInteger> columnValues = new ArrayList<>();
        for (int i = 0; i < column.size() - 1; i++) {
            columnValues.add(new BigInteger(column.get(i)));
        }
        if (column.getLast().equals("+")){
            return columnValues.stream().reduce(BigInteger::add).get();
        }
        if (column.getLast().equals("*")){
            return columnValues.stream().reduce(BigInteger::multiply).get();
        }
        throw new IllegalArgumentException("Invalid operator: " + column.getLast());
    }
}
