package com.alebuc.advent2025.d02;

public record Bound(String value, boolean isAPair, String ref) {
    public Bound(String value) {
        this(value, checkIsAPair(value), findRef(value));
    }

    private static boolean checkIsAPair(String value) {
        return value.length() % 2 == 0;
    }

    private static String findRef(String value) {
        if (value.length() > 1) {
            return value.substring(0, value.length() / 2);
        } else {
            return "0";
        }
    }
}
