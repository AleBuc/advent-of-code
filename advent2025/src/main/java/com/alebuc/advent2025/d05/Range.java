package com.alebuc.advent2025.d05;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class Range implements Comparable<Range> {
    private long a;
    private long b;

    public boolean isValueInBounds(long value) {
        return value >= a && value <= b;
    }

    @Override
    public int compareTo(Range value) {
        long compareA = Long.compare(a, value.getA());
        if (compareA != 0) {
            return Math.toIntExact(compareA);
        }
        return Long.compare(b, value.getB());
    }

    public long getSize() {
        return b - a + 1;
    }

}
