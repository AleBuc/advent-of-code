package com.alebuc.advent2025.d04;

import com.alebuc.advent2025.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class D04e1 {
    public static void main(String[] args) {
//        String fileName = "/ex04.txt";
        String fileName = "/data04.txt";
        List<String> contentLines = new ArrayList<>(Utils.readResourceFileAsLines(fileName));
        System.out.println("Content lines: " + contentLines);

        int validCharacters = 0;
        for (int line = 0; line < contentLines.size(); line++) {
            String lineContent = contentLines.get(line);
            for (int column = 0; column < lineContent.length(); column++) {
                char selectedChar = lineContent.charAt(column);
                if (selectedChar == '@') {
                    int count = 0;
                    for (int y = line - 1; y <= line + 1; y++) {
                        for (int x = column - 1; x <= column + 1; x++) {
                            if (count > 4) {
                                break;
                            }
                            try {
                                String currentLineContent = contentLines.get(y);
                                char currentChar = currentLineContent.charAt(x);
                                if (currentChar == '@') {
                                    count++;
                                }
                            } catch (Exception e) {
                                continue;
                            }
                        }
                        if (count > 4) {
                            break;
                        }
                    }
                    if (count <= 4) {
                        validCharacters++;
                }
            }
        }
    }

        System.out.println("Valid characters: " + validCharacters);
    }
}
