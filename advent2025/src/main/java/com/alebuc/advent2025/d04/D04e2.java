package com.alebuc.advent2025.d04;

import com.alebuc.advent2025.utils.Utils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class D04e2 {
    public static void main(String[] args) {
        String fileName = "/ex04.txt";
//        String fileName = "/data04.txt";
        List<String> contentLines = new ArrayList<>(Utils.readResourceFileAsLines(fileName));
        System.out.println("Content lines: " + contentLines);

        List<List<Roll>> rollsByLine = new ArrayList<>();
        for (String line : contentLines) {
            List<Roll> rolls = new ArrayList<>();
            for (char c : line.toCharArray()) {
                rolls.add(new Roll(c));
            }
            rollsByLine.add(rolls);
        }
        int row = 1;
        boolean removedRoll = true;
        while (removedRoll) {
            removedRoll = false;
            for (int line = 0; line < contentLines.size(); line++) {
                List<Roll> rolls = rollsByLine.get(line);
                for (int column = 0; column < rolls.size(); column++) {
                    Roll roll = rolls.get(column);
                    if (roll.character == '@' && List.of(0, row).contains(roll.getRemovingRow())) {
                        int count = 0;
                        for (int y = line - 1; y <= line + 1; y++) {
                            for (int x = column - 1; x <= column + 1; x++) {
                                if (count > 4) {
                                    break;
                                }
                                try {
                                    List<Roll> currentRolls = rollsByLine.get(y);
                                    Roll currentRoll = currentRolls.get(x);
                                    if (currentRoll.character == '@' && List.of(0, row).contains(currentRoll.getRemovingRow())) {
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
                            roll.setRemovingRow(row);
                            removedRoll = true;
                        }
                    }
                }
            }
            System.out.println("After row " + row + " removal:");
            for (List<Roll> rolls : rollsByLine) {
                StringBuilder sb = new StringBuilder();
                for (Roll roll : rolls) {
                    if (roll.getRemovingRow() > 0) {
                        sb.append('x');
                    } else {
                        sb.append(roll.character);
                    }
                }
                System.out.println(sb);
            }
            row++;
        }
        long removedRollsCount = rollsByLine.stream()
                .flatMap(List::stream)
                .filter(roll -> roll.getRemovingRow() > 0)
                .count();

        System.out.println("Removed rolls: " + removedRollsCount);
    }

    @Data
    private static final class Roll {
        private final char character;
        private int removingRow;

        private Roll(char character) {
            this.character = character;
        }


    }
}
