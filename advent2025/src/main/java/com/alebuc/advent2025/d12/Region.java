package com.alebuc.advent2025.d12;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@EqualsAndHashCode
@ToString
public class Region {
    private final int width;
    private final int length;
    private final Map<Integer, Integer> shapeNumberByIndex;
    @Setter
    private boolean allShapesFittable = false;

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

    public void setAllShapesFittable(Map<Integer, Shape> shapeByIndex) {
        DlxSolver solver = new DlxSolver();
        this.allShapesFittable = solver.isFittable(this, shapeByIndex);
    }
}
