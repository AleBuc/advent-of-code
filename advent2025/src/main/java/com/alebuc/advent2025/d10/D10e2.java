package com.alebuc.advent2025.d10;

import com.alebuc.advent2025.utils.Utils;
import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class D10e2 {

    static {
        Loader.loadNativeLibraries();
    }
    
    public static void main(String[] args) {
        String fileName = "/ex10.txt";
//        String fileName = "/data10.txt";

        List<String> contentLines = new ArrayList<>(Utils.readResourceFileAsLines(fileName));
        System.out.println("Content lines: " + contentLines);

        Set<Machine> machines = contentLines.stream()
                .map(Machine::createFromInstructions)
                .collect(Collectors.toSet());

        Map<Machine, Long> bestScoreByMachine = new HashMap<>();
        for (Machine machine : machines) {
            bestScoreByMachine.put(machine, findBestScore(machine));
        }
        System.out.println("Best score by machine: ");
        bestScoreByMachine.forEach((machine, score) -> System.out.println(machine + " : " + score));
        System.out.println("Sum of best scores: " + bestScoreByMachine.values().stream().mapToLong(Long::longValue).sum());
    }

    /**
     * Solves an integer programming problem for a machine:
     * minimize sum_j x_j
     * subject to constraints:
     * for each light position i: sum_j x_j (where button j affects light i) = target[i]
     * where x_j is the number of presses on button j (integer >= 0).
     * <p>
     * Returns Integer.MAX_VALUE if the configuration is impossible.
     */
    private static long findBestScore(Machine machine) {
        Map<Integer, Integer> targetMap = machine.joltagePattern().valueByIndex();

        // Determine the number of light positions
        int maxIndex = targetMap.keySet().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(-1);
        int size = maxIndex + 1;

        // Build target array with default value 0 for missing indices
        List<Integer> target = new ArrayList<>();
        int sumTargets = 0;
        for (int i = 0; i < size; i++) {
            int v = targetMap.getOrDefault(i, 0);
            target.add(v);
            sumTargets += v;
        }

        // Early return if all target joltages are 0
        if (sumTargets == 0) {
            return 0;
        }

        List<Button> buttons = new ArrayList<>(machine.buttons());
        int buttonCount = buttons.size();

        CpModel model = new CpModel();

        // Create decision variables: x_j >= 0, upper bound = sumTargets (theoretical worst case)
        List<IntVar> x = new ArrayList<>();
        for (int j = 0; j < buttonCount; j++) {
            x.add(model.newIntVar(0, sumTargets, "x_" + j));
        }

        // Add constraints: for each light position i, sum of button contributions equals target[i]
        for (int i = 0; i < size; i++) {
            List<IntVar> vars = new ArrayList<>();

            // Collect all buttons that affect light position i
            for (int j = 0; j < buttonCount; j++) {
                Button b = buttons.get(j);
                if (b.lightPositions().contains(i)) {
                    vars.add(x.get(j));
                }
            }

            if (vars.isEmpty()) {
                // No button can modify this light position
                // If the target is non-zero, the problem is infeasible
                if (target.get(i) != 0) {
                    return Integer.MAX_VALUE;
                }
                // Otherwise, no constraint needed for this light position
            } else {
                // All coefficients are 1, so sum the button presses
                List<IntVar> array = new ArrayList<>(vars);
                model.addEquality(LinearExpr.sum(array.toArray(new IntVar[0])), target.get(i));
            }
        }

        // Objective: minimize the total number of button presses
        model.minimize(LinearExpr.sum(x.toArray(new IntVar[0])));

        // Solve the model
        CpSolver solver = new CpSolver();
        CpSolverStatus status = solver.solve(model);

        if (status != CpSolverStatus.OPTIMAL && status != CpSolverStatus.FEASIBLE) {
            return Integer.MAX_VALUE;
        }

        // Calculate the total number of button presses from the solution
        long totalPresses = 0;
        for (IntVar var : x) {
            totalPresses += solver.value(var);
        }

        return totalPresses;
    }

}