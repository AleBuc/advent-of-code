package com.alebuc.advent2025.d12;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * Rectangular region to be tiled and the multiset of required shapes.
 * <p>
 * Parses a single line of the form: {@code WxH: c0 c1 c2 ...}
 * where {@code W} and {@code H} are width and height, and {@code cK} is the count
 * for shape index {@code K}.
 */
@Getter
@EqualsAndHashCode
@ToString
public class Region {
    private final int width;
    private final int length;
    private final Map<Integer, Integer> shapeNumberByIndex;
    @Setter
    private boolean allShapesFittable = false;

    /**
     * Construct a region from a single instruction line.
     *
     * @param instruction e.g. {@code "5x4: 2 0 1"}
     */
    public Region(String instruction) {
        String[] instructions = instruction.split(":");
        String[] dimensions = instructions[0].split("x");
        this.width = Integer.parseInt(dimensions[0]);
        this.length = Integer.parseInt(dimensions[1]);
        String[] shapeListStrings = instructions[1].trim().split(" ");
        Map<Integer, Integer> shapes = new HashMap<>();
        for (int index = 0; index < shapeListStrings.length; index++) {
            shapes.put(index, Integer.parseInt(shapeListStrings[index]));
        }
        this.shapeNumberByIndex = shapes;
    }

    /**
     * @return total number of cells in the region (W×H)
     */
    public int area() {
        return width * length;
    }

    /**
     * @return the smaller of width and height (useful for quick fit checks)
     */
    public int minDimension() {
        return Math.min(width, length);
    }
}
