package com.alebuc.advent2025.d10;

import com.alebuc.advent2025.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class D10e1 {
    public static void main(String[] args) {
        String fileName = "/ex10.txt";
//        String fileName = "/data10.txt";

        List<String> contentLines = new ArrayList<>(Utils.readResourceFileAsLines(fileName));
        System.out.println("Content lines: " + contentLines);

        Set<Machine> machines = contentLines.stream()
                .map(Machine::createFromInstructions)
                .collect(Collectors.toSet());

        Map<Machine, Integer> bestScoreByMachine = new HashMap<>();
        for (Machine machine : machines) {
            bestScoreByMachine.put(machine, findBestScore(machine));
        }
        System.out.println("Best score by machine: ");
        bestScoreByMachine.forEach((machine, score) -> System.out.println(machine + " : " + score));
        System.out.println("Sum of best scores: " + bestScoreByMachine.values().stream().mapToInt(Integer::intValue).sum());
    }

    /**
     * Solves A * x = b (mod 2) by Gaussian elimination mod 2, then
     * searches among all solutions for the one with the fewest bits set to 1.
     * <p>
     * Returns Integer.MAX_VALUE if no solution exists.
     */
    private static int findBestScore(Machine machine) {
        Map<Integer, Boolean> targetState = machine.lightPattern().stateByIndex();
        int lightCount = targetState.size();

        // Put the buttons in a list to have a stable order (matrix columns)
        List<Button> buttons = new ArrayList<>(machine.buttons());
        int buttonCount = buttons.size();

        // Augmented matrix [A | b] of size lightCount x (buttonCount + 1), in {0,1}
        List<List<Integer>> mat = new ArrayList<>();
        for (int i = 0; i < lightCount; i++) {
            mat.add(new ArrayList<>());
            for (int j = 0; j <= buttonCount; j++) {
                mat.get(i).add(0);
            }
        }

        for (int i = 0; i < lightCount; i++) {
            // b[i]: target state (1 for on, 0 for off)
            boolean targetOn = targetState.getOrDefault(i, false);
            mat.get(i).set(buttonCount, targetOn ? 1 : 0);

            for (int j = 0; j < buttonCount; j++) {
                Button button = buttons.get(j);
                // A[i][j] = 1 if button j toggles light i
                mat.get(i).set(j, button.lightPositions().contains(i) ? 1 : 0);
            }
        }

        // Gauss-Jordan elimination mod 2
        List<Integer> pivotRowForCol = new ArrayList<>();
        for (int i = 0; i < buttonCount; i++) {
            pivotRowForCol.add(-1);
        }

        int row = 0;
        for (int col = 0; col < buttonCount && row < lightCount; col++) {
            // Search for a row with a 1 in this column
            int sel = -1;
            for (int i = row; i < lightCount; i++) {
                if (mat.get(i).get(col) == 1) {
                    sel = i;
                    break;
                }
            }
            if (sel == -1) {
                // No pivot in this column
                continue;
            }

            // Swap the current row with row sel
            if (sel != row) {
                List<Integer> tmp = mat.get(row);
                mat.set(row, mat.get(sel));
                mat.set(sel, tmp);
            }

            pivotRowForCol.set(col, row);

            // Cancel all 1s in this column on other rows (Gauss-Jordan)
            for (int i = 0; i < lightCount; i++) {
                if (i != row && mat.get(i).get(col) == 1) {
                    xorRows(mat.get(i), mat.get(row));
                }
            }
            row++;
        }

        // Check for contradictions: row of the form [0 0 ... 0 | 1]
        for (int i = 0; i < lightCount; i++) {
            boolean allZero = true;
            for (int j = 0; j < buttonCount; j++) {
                if (mat.get(i).get(j) != 0) {
                    allZero = false;
                    break;
                }
            }
            if (allZero && mat.get(i).get(buttonCount) == 1) {
                // 0 = 1 (mod 2) -> no solution
                return Integer.MAX_VALUE;
            }
        }

        // Free columns (without pivot)
        List<Integer> freeCols = new ArrayList<>();
        for (int col = 0; col < buttonCount; col++) {
            if (pivotRowForCol.get(col) == -1) {
                freeCols.add(col);
            }
        }

        int freeCount = freeCols.size();

        // If too many free variables, exhaustive search would become huge.
        // We set a small safety limit (for example 22 => 2^22 ~ 4 million).
        if (freeCount > 22) {
            throw new IllegalStateException("Too many free variables for exhaustive search: " + freeCount);
        }

        int bestWeight = Integer.MAX_VALUE;

        // Enumerate all possible assignments of free variables.
        // For each mask, we reconstruct the complete solution x.
        int totalAssignments = 1 << freeCount;
        for (int mask = 0; mask < totalAssignments; mask++) {
            List<Integer> x = new ArrayList<>();
            for (int i = 0; i < buttonCount; i++) {
                x.add(0);
            }

            // Assign free variables
            for (int k = 0; k < freeCount; k++) {
                int col = freeCols.get(k);
                int bit = (mask >> k) & 1;
                x.set(col, bit);
            }

            // Calculate pivot variables by back substitution
            for (int col = 0; col < buttonCount; col++) {
                int pivotRow = pivotRowForCol.get(col);
                if (pivotRow == -1) {
                    // free variable, already defined
                    continue;
                }
                int value = mat.get(pivotRow).get(buttonCount); // constant term
                // Subtract (XOR) the contribution of free variables
                for (int k = 0; k < freeCount; k++) {
                    int freeCol = freeCols.get(k);
                    if (mat.get(pivotRow).get(freeCol) == 1 && x.get(freeCol) == 1) {
                        value ^= 1;
                    }
                }
                x.set(col, value);
            }

            // Hamming weight of solution x = number of presses
            int weight = 0;
            for (int j = 0; j < buttonCount; j++) {
                weight += x.get(j);
            }

            if (weight < bestWeight) {
                bestWeight = weight;
            }
        }

        return bestWeight;
    }

    /**
     * XOR (mod 2) of two rows of the augmented matrix: a = a ⊕ b.
     */
    private static void xorRows(List<Integer> a, List<Integer> b) {
        for (int i = 0; i < a.size(); i++) {
            a.set(i, a.get(i) ^ b.get(i));
        }
    }

}