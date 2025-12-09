package com.alebuc.advent2025.d09;

import com.alebuc.advent2025.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class D09e1 {
    public static void main(String[] args) {
//         String fileName = "/ex09.txt";
        String fileName = "/data09.txt";

        List<String> contentLines = new ArrayList<>(Utils.readResourceFileAsLines(fileName));
        System.out.println("Content lines: " + contentLines);

        List<Tile> tiles = contentLines.stream()
                .map(s -> {
                    String[] parts = StringUtils.split(s, ",");
                    return new Tile(new BigInteger(parts[0]), new BigInteger(parts[1]));
                })
                .toList();
        System.out.println("Tiles: " + tiles);

        List<Rectangle> rectangles = new ArrayList<>();
        for (int indexA = 0; indexA < tiles.size(); indexA++) {
            for (int indexB = indexA + 1; indexB < tiles.size(); indexB++) {
                Tile tileA = tiles.get(indexA);
                Tile tileB = tiles.get(indexB);
                rectangles.add(new Rectangle(tileA, tileB));
            }
        }
        rectangles.sort(Comparator.reverseOrder());
        System.out.println("Rectangles: " + rectangles);
        System.out.println("Result: " + rectangles.getFirst().surface());
    }

}
