package com.alebuc.advent2025.d01;

import java.math.BigInteger;

public record Range(BigInteger start, BigInteger end) {
    public boolean isInRange(BigInteger number) {
        return number.compareTo(start) >= 0 && number.compareTo(end) <= 0;
    }
    public boolean isOutOfRange(BigInteger number) {
        return number.compareTo(start) < 0 || number.compareTo(end) > 0;
    }
    public BigInteger getModulo() {
        return end.subtract(start).add(BigInteger.ONE);
    }
    public BigInteger getRest(BigInteger number) {
        return number.subtract(start).remainder(getModulo());
    }
}
