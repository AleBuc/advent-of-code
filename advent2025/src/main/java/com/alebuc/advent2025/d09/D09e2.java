package com.alebuc.advent2025.d09;

import com.alebuc.advent2025.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class D09e2 {
    public static void main(String[] args) {
        String fileName = "/ex09.txt";
//        String fileName = "/data09.txt";

        List<String> contentLines = new ArrayList<>(Utils.readResourceFileAsLines(fileName));
        System.out.println("Content lines: " + contentLines);

        List<Tile> tiles = contentLines.stream()
                .map(s -> {
                    String[] parts = StringUtils.split(s, ",");
                    return new Tile(new BigInteger(parts[0]), new BigInteger(parts[1]));
                })
                .toList();
        System.out.println("Tiles: " + tiles);

        Set<Tile> perimeterTiles = getPerimeterTiles(tiles);

        BigInteger minX = tiles.stream().map(Tile::getX).min(Comparator.naturalOrder()).orElse(BigInteger.ZERO);
        BigInteger maxX = tiles.stream().map(Tile::getX).max(Comparator.naturalOrder()).orElse(BigInteger.ZERO);
        BigInteger minY = tiles.stream().map(Tile::getY).min(Comparator.naturalOrder()).orElse(BigInteger.ZERO);
        BigInteger maxY = tiles.stream().map(Tile::getY).max(Comparator.naturalOrder()).orElse(BigInteger.ZERO);
        System.out.println("MinX: " + minX + ", MaxX: " + maxX + ", MinY: " + minY + ", MaxY: " + maxY);

        Map<BigInteger, List<BigInteger>> verticalCrossingsByY = buildVerticalCrossings(tiles);

        Rectangle largestRectangle = new Rectangle(new Tile(BigInteger.ZERO, BigInteger.ZERO), new Tile(BigInteger.ZERO, BigInteger.ZERO), false);
        for (int i = 0; i < tiles.size() - 1; i++) {
            Tile cornerA = tiles.get(i);
            for (int j = i + 1; j < tiles.size(); j++) {
                Tile cornerB = tiles.get(j);
                Rectangle rectangle = new Rectangle(cornerA, cornerB, false);
                BigInteger minRectX = rectangle.minX();
                BigInteger maxRectX = rectangle.maxX();
                BigInteger minRectY = rectangle.minY();
                BigInteger maxRectY = rectangle.maxY();
                boolean isValid = true;
                for (BigInteger x = minRectX; isValid && x.compareTo(maxRectX) <= 0; x = x.add(BigInteger.ONE)) {
                    if (isOutside(x, minRectY, perimeterTiles, verticalCrossingsByY)) {
                        isValid = false;
                        break;
                    }
                    if (isOutside(x, maxRectY, perimeterTiles, verticalCrossingsByY)) {
                        isValid = false;
                        break;
                    }
                }
                for (BigInteger y = minRectY; isValid && y.compareTo(maxRectY) <= 0; y = y.add(BigInteger.ONE)) {
                    if (isOutside(minRectX, y, perimeterTiles, verticalCrossingsByY)) {
                        isValid = false;
                        break;
                    }
                    if (isOutside(maxRectX, y, perimeterTiles, verticalCrossingsByY)) {
                        isValid = false;
                        break;
                    }
                }
                if (isValid && rectangle.surface().compareTo(largestRectangle.surface()) > 0) {
                    largestRectangle = rectangle;
                }
            }
        }

        System.out.println("Largest rectangle surface: " + largestRectangle.surface());
    }

    private static Set<Tile> getPerimeterTiles(List<Tile> tiles) {
        Set<Tile> perimeterTiles = new HashSet<>();
        for (int i = 0; i < tiles.size(); i++) {
            Tile tileA = tiles.get(i);
            Tile tileB;
            if (i == tiles.size() - 1) {
                tileB = tiles.getFirst();
            } else {
                tileB = tiles.get(i + 1);
            }
            perimeterTiles.add(tileA);
            if (tileA.getX().equals(tileB.getX())){
                BigInteger minY = tileA.getY().min(tileB.getY());
                BigInteger maxY = tileA.getY().max(tileB.getY());
                for (BigInteger y = minY.add(BigInteger.ONE); y.compareTo(maxY) < 0; y = y.add(BigInteger.ONE)) {
                    perimeterTiles.add(new Tile(tileA.getX(), y, Tile.Status.PERIMETER));
                }
            } else if (tileA.getY().equals(tileB.getY())) {
                BigInteger minX = tileA.getX().min(tileB.getX());
                BigInteger maxX = tileA.getX().max(tileB.getX());
                for (BigInteger x = minX.add(BigInteger.ONE); x.compareTo(maxX) < 0; x = x.add(BigInteger.ONE)) {
                    perimeterTiles.add(new Tile(x, tileA.getY(), Tile.Status.PERIMETER));
                }
            }
        }
        return perimeterTiles;
    }

    private static Map<BigInteger, List<BigInteger>> buildVerticalCrossings(List<Tile> cornerTiles) {
        Map<BigInteger, List<BigInteger>> crossingsByY = new HashMap<>();

        for (int i = 0; i < cornerTiles.size(); i++) {
            Tile tileA = cornerTiles.get(i);
            Tile tileB = (i == cornerTiles.size() - 1) ? cornerTiles.getFirst() : cornerTiles.get(i + 1);

            if (tileA.getX().equals(tileB.getX())) {
                BigInteger minY = tileA.getY().min(tileB.getY());
                BigInteger maxY = tileA.getY().max(tileB.getY());
                for (BigInteger y = minY.add(BigInteger.ONE); y.compareTo(maxY) <= 0; y = y.add(BigInteger.ONE)) {
                    crossingsByY
                            .computeIfAbsent(y, k -> new ArrayList<>())
                            .add(tileA.getX());
                }
            }
        }
        crossingsByY.replaceAll((y, list) -> list.stream().sorted().collect(Collectors.toList()));
        return crossingsByY;
    }

    private static boolean isOutside(
            BigInteger x,
            BigInteger y,
            Set<Tile> perimeterTiles,
            Map<BigInteger, List<BigInteger>> verticalCrossingsByY
    ) {
        if (perimeterTiles.contains(new Tile(x, y))) {
            return false;
        }

        List<BigInteger> crossings = verticalCrossingsByY.get(y);
        if (crossings == null) {
            return true;
        }

        int count = 0;
        for (BigInteger cx : crossings) {
            if (cx.compareTo(x) > 0) {
                break;
            }
            count++;
        }
        return (count % 2 != 1);
    }


}
