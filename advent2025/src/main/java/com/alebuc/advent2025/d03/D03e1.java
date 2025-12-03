package com.alebuc.advent2025.d03;

import com.alebuc.advent2025.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class D03e1 {
    public static void main(String[] args) {
        String fileName = "/ex03.txt";
//        String fileName = "/data03.txt";
        List<String> contentLines = new ArrayList<>(Utils.readResourceFileAsLines(fileName));
        System.out.println("Content lines: " + contentLines);

        Map<String, List<String>> banksById = contentLines.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        line -> List.of(line.split(""))
                ));
        System.out.println("Banks: ");
        banksById.forEach((id, bank) -> System.out.println(id + " : " + bank));

        Map<String, Integer> joltageByBankId = banksById.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> calculateJoltage(entry.getValue())
                ));
        System.out.println("Joltage by Bank ID: ");
        joltageByBankId.forEach((id, joltage) -> System.out.println(id + " : " + joltage));
        Long totalJoltage = joltageByBankId.values().stream()
                .mapToLong(Integer::longValue)
                .sum();
        System.out.println("Total Joltage: " + totalJoltage);
    }

    private static int calculateJoltage(List<String> bank) {
        int bankLength = bank.size();

        int decadeMaxIndex = 0;
        int decadeInt = 0;
        for (int decadeIndex = 0; decadeIndex < bankLength - 1; decadeIndex++) {
            String value = bank.get(decadeIndex);
            int intValue = Integer.parseInt(value);
            if (intValue > decadeInt) {
                decadeInt = intValue;
                decadeMaxIndex = decadeIndex;
            }
        }
        String decadeValue = Integer.toString(decadeInt);
        int unitInt = 0;
        for (int unitIndex = decadeMaxIndex + 1; unitIndex < bankLength; unitIndex++) {
            String value = bank.get(unitIndex);
            int intValue = Integer.parseInt(value);
            if (intValue > unitInt) {
                unitInt = intValue;
            }
        }
        String unitValue = Integer.toString(unitInt);
        return Integer.parseInt(decadeValue + unitValue);
    }
}
