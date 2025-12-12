package com.alebuc.advent2025.d12;

import java.util.List;

public record Orientation(
        List<Point2D> points, //normalized: minX=minY=0
        int width,
        int height,
        int mask //mask 3x3 for deduplication
) {
}
