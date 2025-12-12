package com.alebuc.advent2025.d12;

import lombok.Value;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A 3×3-constrained polyomino shape with all unique orientations.
 * <p>
 * Parses a 4-line block: an index line followed by three rows of a 3×3 grid.
 * Builds rotations (0/90/180/270) and a horizontal flip variants, then deduplicates
 * by a 3×3 bitmask computed from the normalized coordinates.
 */
@Value
public class Shape {
    int index;

    List<Point2D> originalShape;
    List<Point2D> shape90;
    List<Point2D> shape180;
    List<Point2D> shape270;

    List<Orientation> orientations;

    /**
     * Constructs a shape from a 4-line instruction block.
     *
     * @param instructions list of 4 lines: "<index>:" then 3 rows of a 3×3 grid with '#'
     */
    public Shape(List<String> instructions) {
        assert instructions.size() == 4;
        this.index = Integer.parseInt(instructions.getFirst().split(":")[0]);

        List<Point2D> shape = new ArrayList<>();
        for (int y = 0; y < instructions.size() - 1; y++) {
            String line = instructions.get(y + 1);
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == '#') {
                    shape.add(new Point2D(x, y));
                }
            }
        }

        this.originalShape = shape;
        this.shape90 = rotate90(this.originalShape);
        this.shape180 = rotate90(this.shape90);
        this.shape270 = rotate90(this.shape180);
        // Build all 8 raw orientations (4 rotations × original/flipped)
        List<Point2D> flipped = flipHorizontal(this.originalShape);
        List<Point2D> flipped90 = rotate90(flipped);
        List<Point2D> flipped180 = rotate90(flipped90);
        List<Point2D> flipped270 = rotate90(flipped180);

        // Deduplicate orientations by 3×3 mask after normalization
        this.orientations = computeUniqueOrientations(List.of(
                this.originalShape,
                this.shape90,
                this.shape180,
                this.shape270,
                flipped,
                flipped90,
                flipped180,
                flipped270
        ));
    }

    /**
     * @return number of filled cells in the shape
     */
    public int size() {
        return originalShape.size();
    }

    /**
     * Minimal bounding box dimension across all unique orientations.
     * Used as a quick fit filter against region's smallest side.
     *
     * @return minimal width or height among orientations; 0 if none
     */
    public int minDimension() {
        if (orientations == null || orientations.isEmpty()) return 0;
        int min = Integer.MAX_VALUE;
        for (Orientation o : orientations) {
            min = Math.min(min, Math.min(o.width(), o.height()));
        }
        return (min == Integer.MAX_VALUE) ? 0 : min;
    }

    private static List<Orientation> computeUniqueOrientations(List<List<Point2D>> candidates) {
        Map<Integer, Orientation> unique = new LinkedHashMap<>();

        for (List<Point2D> pts : candidates) {
            Orientation o = normalizeAndMeasure(pts);
            unique.putIfAbsent(o.mask(), o);
        }
        return List.copyOf(unique.values());
    }

    /**
     * Normalize points so the min x/y becomes (0,0), compute bounding size and a 3×3 mask.
     */
    private static Orientation normalizeAndMeasure(List<Point2D> pts) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        for (Point2D p : pts) {
            int x = p.x();
            int y = p.y();
            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
        }
        int width = maxX - minX + 1;
        int height = maxY - minY + 1;
        List<Point2D> normalized = new ArrayList<>(pts.size());
        int mask = 0;
        for (Point2D p : pts) {
            int nx = p.x() - minX;
            int ny = p.y() - minY;
            normalized.add(new Point2D(nx, ny));
            int bitIndex = ny * 3 + nx; // 3×3 mask index
            mask |= (1 << bitIndex);
        }
        return new Orientation(List.copyOf(normalized), width, height, mask);
    }

    /**
     * Rotate 90° clockwise within a 3×3 grid.
     */
    private static List<Point2D> rotate90(List<Point2D> pts) {
        List<Point2D> rotated = new ArrayList<>(pts.size());
        for (Point2D p : pts) {
            int x = p.x();
            int y = p.y();
            rotated.add(new Point2D(2 - y, x));
        }
        return rotated;
    }

    /**
     * Flip horizontally within a 3×3 grid.
     */
    private static List<Point2D> flipHorizontal(List<Point2D> pts) {
        List<Point2D> flipped = new ArrayList<>(pts.size());
        for (Point2D p : pts) {
            flipped.add(new Point2D(2 - p.x(), p.y()));
        }
        return flipped;
    }
}
