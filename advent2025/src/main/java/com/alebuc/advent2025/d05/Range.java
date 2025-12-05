package com.alebuc.advent2025.d05;

public record Range(long a, long b) {
    public boolean isValueInBounds(long value) {
        return value >= a && value <= b;
    }
}
