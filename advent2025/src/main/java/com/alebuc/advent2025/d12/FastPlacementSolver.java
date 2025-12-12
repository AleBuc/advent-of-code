package com.alebuc.advent2025.d12;

import java.util.Map;

/**
 * Fast, conservative feasibility checks before running the exact solver.
 * <p>
 * Verifies two necessary conditions:
 * - Total required area fits into the region.
 * - For each shape, its minimal bounding dimension does not exceed the region's minimal dimension.
 */
final class FastPlacementSolver {

    private final Map<Integer, Shape> shapesByIndex;

    /**
     * @param shapesByIndex mapping of shape index to parsed {@link Shape}
     */
    FastPlacementSolver(Map<Integer, Shape> shapesByIndex) {
        this.shapesByIndex = shapesByIndex;
    }

    /**
     * Quick feasibility filter based on area and minimal dimension.
     *
     * @param region region to check
     * @return true if the region passes the cheap necessary conditions
     */
    boolean canFit(Region region) {
        int regionArea = region.area();
        int regionMinDim = region.minDimension();

        int requiredArea = 0;

        // Accumulate required area; check shapes existence and min dimension
        for (Map.Entry<Integer, Integer> e : region.getShapeNumberByIndex().entrySet()) {
            int shapeIndex = e.getKey();
            int count = e.getValue();
            if (count <= 0) {
                continue; // skip unused shapes
            }

            Shape shape = shapesByIndex.get(shapeIndex);
            if (shape == null) {
                return false; // missing shape definition
            }

            requiredArea += count * shape.size();
            if (requiredArea > regionArea) {
                return false; // early fail: area overflow
            }

            if (shape.minDimension() > regionMinDim) {
                return false; // a shape cannot fit along the smallest side
            }
        }

        return requiredArea <= regionArea;
    }
}
