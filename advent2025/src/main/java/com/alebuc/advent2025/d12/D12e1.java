package com.alebuc.advent2025.d12;

import com.alebuc.advent2025.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class D12e1 {

    public static final Pattern SHAPE_INDEX_PATTERN = Pattern.compile( "^\\s*\\d+\\s*:\\s*$");

    public static void main(String[] args) {
        String fileName = "/ex12.txt";
//        String fileName = "/data12.txt";
        List<String> contentLines = new ArrayList<>(Utils.readResourceFileAsLines(fileName));
        System.out.println("Content lines: " + contentLines);

        Map<Integer, Shape> shapeByIndex = new HashMap<>();
        List<Region> regions = new ArrayList<>();
        for (int i = 0; i < contentLines.size(); i++) {
            String line = contentLines.get(i);
            if (SHAPE_INDEX_PATTERN.matcher(line).matches()) {
                List<String> instructions = contentLines.subList(i, i + 4);
                Shape shape = new Shape(instructions);
                shapeByIndex.put(shape.getIndex(), shape);
                i += 3;
            } else if (StringUtils.isBlank(line)) {
                continue;
            } else {
                Region region = new Region(line);
                regions.add(region);
            }
        }
        AtomicInteger processedRegions = new AtomicInteger(0);
        System.out.println("Shapes: ");
        shapeByIndex.forEach((index, shape) -> System.out.println(index + ": " + shape));
        System.out.println("Regions: ");
        regions.forEach(System.out::println);
        regions.forEach(region -> {
            region.setAllShapesFittable(shapeByIndex);
            System.out.println("Processed region " + processedRegions.incrementAndGet() + ".");
        });
        List<Region> validatedRegions = regions.stream()
                .filter(Region::isAllShapesFittable)
                .toList();
        System.out.println("Validated regions: " + validatedRegions.size());
    }



}
