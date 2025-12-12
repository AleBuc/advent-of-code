package com.alebuc.advent2025.d12;

import lombok.Value;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Value
public class Shape {
    int index;

    List<Point2D> originalShape;
    List<Point2D> shape90;
    List<Point2D> shape180;
    List<Point2D> shape270;

    List<Orientation> orientations;

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
        List<Point2D> flipped = flipHorizontal(this.originalShape);
        List<Point2D> flipped90 = rotate90(flipped);
        List<Point2D> flipped180 = rotate90(flipped90);
        List<Point2D> flipped270 = rotate90(flipped180);

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

    private static List<Orientation> computeUniqueOrientations(List<List<Point2D>> candidates) {
        Map<Integer, Orientation> unique = new LinkedHashMap<>();

        for (List<Point2D> pts : candidates) {
            Orientation o = normalizeAndMeasure(pts);
            unique.putIfAbsent(o.mask(), o);
        }
        return List.copyOf(unique.values());
    }

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
            int bitIndex = ny * 3 + nx;
            mask |= (1 << bitIndex);
        }
        return new Orientation(List.copyOf(normalized), width, height, mask);
    }

    private static List<Point2D> rotate90(List<Point2D> pts) {
        List<Point2D> rotated = new ArrayList<>(pts.size());
        for (Point2D p : pts) {
            int x = p.x();
            int y = p.y();
            rotated.add(new Point2D(2 - y, x));
        }
        return rotated;
    }

    private static List<Point2D> flipHorizontal(List<Point2D> pts) {
        List<Point2D> flipped = new ArrayList<>(pts.size());
        for (Point2D p : pts) {
            flipped.add(new Point2D(2 - p.x(), p.y()));
        }
        return flipped;
    }
}
