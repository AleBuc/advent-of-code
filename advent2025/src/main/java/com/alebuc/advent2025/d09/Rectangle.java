package com.alebuc.advent2025.d09;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public record Rectangle(
        Tile tileA,
        Tile tileB,
        BigInteger surface,
        BigInteger minX,
        BigInteger maxX,
        BigInteger minY,
        BigInteger maxY,
        Set<Tile> perimeterTiles
) implements Comparable<Rectangle> {
    public Rectangle(Tile tileA, Tile tileB, boolean perimeterTilesComputed) {
        this(
                tileA,
                tileB,
                computeSurface(tileA, tileB),
                tileA.getX().min(tileB.getX()),
                tileA.getX().max(tileB.getX()),
                tileA.getY().min(tileB.getY()),
                tileA.getY().max(tileB.getY()),
                computePerimeterTiles(tileA, tileB, perimeterTilesComputed));
    }

    private static BigInteger computeSurface(Tile tileA, Tile tileB) {
        BigInteger distanceX = tileB.getX().subtract(tileA.getX()).abs().add(BigInteger.ONE);
        BigInteger distanceY = tileB.getY().subtract(tileA.getY()).abs().add(BigInteger.ONE);
        return distanceX.multiply(distanceY);
    }

    private static Set<Tile> computePerimeterTiles(Tile tileA, Tile tileB, boolean perimeterTilesComputed) {
        if (!perimeterTilesComputed) {
            return Set.of();
        }
        BigInteger minX = tileA.getX().min(tileB.getX());
        BigInteger maxX = tileA.getX().max(tileB.getX());
        BigInteger minY = tileA.getY().min(tileB.getY());
        BigInteger maxY = tileA.getY().max(tileB.getY());
        Set<Tile> perimeterTiles = new HashSet<>();
        for (BigInteger x = minX; x.compareTo(maxX) <= 0; x = x.add(BigInteger.ONE)) {
            perimeterTiles.add(new Tile(x, minY));
            perimeterTiles.add(new Tile(x, maxY));
        }
        for (BigInteger y = minY.add(BigInteger.ONE); y.compareTo(maxY) < 0; y = y.add(BigInteger.ONE)) {
            perimeterTiles.add(new Tile(minX, y));
            perimeterTiles.add(new Tile(maxX, y));
        }
        return perimeterTiles;
    }

    @Override
    public int compareTo(Rectangle o) {
        return this.surface.compareTo(o.surface);
    }
}
