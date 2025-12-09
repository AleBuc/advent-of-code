package com.alebuc.advent2025.d09;

import java.math.BigInteger;

public record Rectangle(
        Tile tileA,
        Tile tileB,
        BigInteger surface
) implements Comparable<Rectangle> {
    public Rectangle(Tile tileA, Tile tileB) {
        this(tileA, tileB, computeSurface(tileA,tileB));
    }

    private static BigInteger computeSurface(Tile tileA, Tile tileB) {
        BigInteger distanceX = tileB.x().subtract(tileA.x()).abs().add(BigInteger.ONE);
        BigInteger distanceY = tileB.y().subtract(tileA.y()).abs().add(BigInteger.ONE);
        return distanceX.multiply(distanceY);
    }

    @Override
    public int compareTo(Rectangle o) {
        return this.surface.compareTo(o.surface);
    }
}
