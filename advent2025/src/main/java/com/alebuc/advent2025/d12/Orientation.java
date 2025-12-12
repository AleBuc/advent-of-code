package com.alebuc.advent2025.d12;

import java.util.List;

/**
 * One unique orientation of a shape.
 * <p>
 * Points are normalized so the minimum x and y are zero. Width/height describe the
 * bounding box, and {@code mask} is a 3×3 bitmask used to deduplicate orientations.
 */
public record Orientation(
        List<Point2D> points, // normalized: minX=minY=0
        int width,
        int height,
        int mask // 3×3 mask for deduplication
) {
}
