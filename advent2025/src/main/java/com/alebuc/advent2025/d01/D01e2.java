package com.alebuc.advent2025.d01;

import com.alebuc.advent2025.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class D01e2 {
    public static void main(String[] args) {
        List<String> contentLines = Utils.readResourceFileAsLines("/data01-1.txt");
        System.out.println("Content lines: " + contentLines);
        List<Move> moves = new ArrayList<>(contentLines.stream()
                .map(s -> {
                    char direction = s.charAt(0);
                    BigInteger value = new BigInteger(s.substring(1));
                    return new Move(direction, value);
                })
                .toList());

        BigInteger initialPosition = new BigInteger("50");
        final BigInteger MIN_VALUE = BigInteger.ZERO;
        final BigInteger MAX_VALUE = new BigInteger("99");
        final BigInteger MODULO = MAX_VALUE.subtract(MIN_VALUE).add(BigInteger.ONE);
        List<BigInteger> positions = new ArrayList<>();
        positions.add(initialPosition);
        AtomicReference<BigInteger> throughZeroCount = new AtomicReference<>(BigInteger.ZERO);
        moves.forEach(move -> {
            char direction = move.direction;
            BigInteger value = move.getValue();
            BigInteger position = positions.getLast();

            if (value.compareTo(MODULO) > 0) {
                throughZeroCount.accumulateAndGet(value.divide(MODULO), BigInteger::add);
                value = value.remainder(MODULO);
            }

            BigInteger newPosition;
            if (direction == 'R') {
                newPosition = position.add(value);
                if (newPosition.compareTo(MAX_VALUE) > 0) {
                    throughZeroCount.accumulateAndGet(BigInteger.ONE, BigInteger::add);
                    newPosition = newPosition.remainder(MAX_VALUE.add(BigInteger.ONE));
                }
            } else {
                boolean isNotZero = !position.equals(MIN_VALUE);
                newPosition = position.subtract(value);
                if (isNotZero && newPosition.compareTo(MIN_VALUE) <= 0) {
                    throughZeroCount.accumulateAndGet(BigInteger.ONE, BigInteger::add);
                }
                if (newPosition.compareTo(MIN_VALUE) < 0) {
                    newPosition = newPosition.add(MODULO);
                }
            }
            positions.add(newPosition);
        });

        System.out.println("Positions: " + positions);
        System.out.println("Answer: " + throughZeroCount.get());
    }

    @Data
    @AllArgsConstructor
    private static class Move {
        private char direction;
        private BigInteger value;
    }
}
