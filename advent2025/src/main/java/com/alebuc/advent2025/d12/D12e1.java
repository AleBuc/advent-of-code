package com.alebuc.advent2025.d12;

import com.alebuc.advent2025.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * Advent of Code 2025 — Day 12, entry point.
 * <p>
 * Reads shapes and regions from a resource file, runs a fast feasibility pass
 * followed by an exact tiling check, and prints summary counts.
 */
public class D12e1 {

    public static final Pattern SHAPE_INDEX_PATTERN = Pattern.compile( "^\\s*\\d+\\s*:\\s*$");

    /**
     * Program entry point.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        String fileName = "/ex12.txt";
//        String fileName = "/data12.txt";
        List<String> contentLines = new ArrayList<>(Utils.readResourceFileAsLines(fileName));
        System.out.println("Content lines: " + contentLines);

        Map<Integer, Shape> shapeByIndex = new HashMap<>();
        List<Region> regions = new ArrayList<>();
        // Parse input lines into shapes and regions
        for (int i = 0; i < contentLines.size(); i++) {
            String line = contentLines.get(i);
            if (SHAPE_INDEX_PATTERN.matcher(line).matches()) {
                // 1) Shape block: index line + 3 shape rows (3x3 grid)
                List<String> instructions = contentLines.subList(i, i + 4);
                Shape shape = new Shape(instructions);
                shapeByIndex.put(shape.getIndex(), shape);
                i += 3;
            } else if (StringUtils.isBlank(line)) {
                continue;
            } else {
                // 2) Region line
                Region region = new Region(line);
                regions.add(region);
            }
        }

        FastPlacementSolver fastSolver = new FastPlacementSolver(shapeByIndex);
        ExactTilingSolver exactSolver = new ExactTilingSolver(shapeByIndex);

        AtomicInteger processedFast = new AtomicInteger(0);
        List<Region> fastValidatedRegions = new ArrayList<>();
        // First pass: quick feasibility filter
        for (Region region : regions) {
            boolean fastOk = fastSolver.canFit(region);
            if (fastOk) {
                fastValidatedRegions.add(region);
            }
            System.out.println("Processed region (FAST) " + processedFast.incrementAndGet() + ".");
        }

        int validatedFastCount = fastValidatedRegions.size();
        System.out.println("Validated regions (FAST): " + validatedFastCount);

        AtomicInteger processedExact = new AtomicInteger(0);
        int validatedExactCount = 0;
        // Second pass: exact tiling (backtracking) on fast-validated regions
        for (Region region : fastValidatedRegions) {
            boolean exactOk = exactSolver.canTile(region);
            if (exactOk) {
                validatedExactCount++;
            }
            System.out.println("Processed region (EXACT) " + processedExact.incrementAndGet() + ".");
        }

        System.out.println("Validated regions (EXACT): " + validatedExactCount);
    }
}
