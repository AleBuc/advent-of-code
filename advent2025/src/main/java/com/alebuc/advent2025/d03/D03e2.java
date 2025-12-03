package com.alebuc.advent2025.d03;

import com.alebuc.advent2025.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class D03e2 {
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

        Map<String, Long> joltageByBankId = banksById.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> calculateJoltage(entry.getValue())
                ));
        System.out.println("Joltage by Bank ID: ");
        joltageByBankId.forEach((id, joltage) -> System.out.println(id + " : " + joltage));
        long totalJoltage = joltageByBankId.values().stream()
                .mapToLong(Long::longValue)
                .sum();
        System.out.println("Total Joltage: " + totalJoltage);
    }

    private static long calculateJoltage(List<String> bank) {
        List<Value> values = new ArrayList<>();
        for (int position = 0; position < 12; position++) {
            values.add(getFirstMaxValue(
                    bank,
                    values.isEmpty() ? -1 : values.getLast().index,
                    position));
        }
        return Long.parseLong(String.join("", values.stream().map(value -> value.value + "").toList()));
    }

    private static Value getFirstMaxValue(List<String> bank, int previousIndex, int currentPosition) {
        int unitInt = 0;
        int index = 0;
        for (int i = previousIndex + 1; i < bank.size() - 11 + currentPosition; i++) {
            String value = bank.get(i);
            int intValue = Integer.parseInt(value);
            if (intValue > unitInt) {
                unitInt = intValue;
                index = i;
            }
        }
        return new Value(index, unitInt, currentPosition + 1);
    }

    private record Value(int index, int value, int position) {
    }
}
