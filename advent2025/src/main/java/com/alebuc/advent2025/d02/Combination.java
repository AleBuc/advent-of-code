package com.alebuc.advent2025.d02;

public record Combination(Bound b1, Bound b2) {
    public boolean isValueInBounds(String value) {
        return Long.parseLong(value) >= Long.parseLong(b1.value()) && Long.parseLong(value) <= Long.parseLong(b2.value());
    }
}
