package com.alebuc.advent2025.d09;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigInteger;
import java.util.Objects;

@Getter
@ToString
public final class Tile {
    private final BigInteger x;
    private final BigInteger y;
    @Setter
    private Status status;

    public Tile(BigInteger x, BigInteger y) {
        this.x = x;
        this.y = y;
    }

    public Tile(BigInteger x, BigInteger y, Status status) {
        this.x = x;
        this.y = y;
        this.status = status;
    }

    public enum Status {
        INSIDE,
        PERIMETER,
        OUTSIDE
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tile tile)) return false;
        return Objects.equals(x, tile.x) && Objects.equals(y, tile.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
