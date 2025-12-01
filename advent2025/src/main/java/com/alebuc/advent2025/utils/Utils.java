package com.alebuc.advent2025.utils;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static String readResourceFileAsString(String fileName) {
        try (var inputStream = Utils.class.getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found: " + fileName);
            }
            return new String(inputStream.readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException("Error reading file: " + fileName, e);
        }
    }

    public static List<String> readResourceFileAsLines(String fileName) {
        String content = readResourceFileAsString(fileName);
        return new ArrayList<>(List.of(content.split("\n")));
    }
}
