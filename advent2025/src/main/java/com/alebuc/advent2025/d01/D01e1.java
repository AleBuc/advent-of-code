package com.alebuc.advent2025.d01;

import com.alebuc.advent2025.utils.Utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class D01e1 {
    public static void main(String[] args) {
        List<String> contentLines = Utils.readResourceFileAsLines("/data01-1.txt");
        System.out.println("Content lines: " + contentLines);
        contentLines.replaceAll(line -> line.replace('L','-').replace('R','+'));
        System.out.println("Processed lines: " + contentLines);
        List< BigInteger> numbers = contentLines.stream()
                .map(BigInteger::new)
                .toList();

        BigInteger initialPosition = new BigInteger("50");
        Range range = new Range(new BigInteger("0"), new BigInteger("99"));
        List<BigInteger> positions = new ArrayList<>();
        positions.add(initialPosition);
        numbers.forEach(number -> {
            BigInteger newPosition = positions.getLast().add(number);
            if (range.isOutOfRange(newPosition)) {
                newPosition = range.getRest(newPosition);
                if(range.isOutOfRange(newPosition)){
                    newPosition = newPosition.add(range.getModulo());
                }
                if (range.isOutOfRange(newPosition)){
                    throw new IllegalStateException("Position out of range after adjustment: " + newPosition);
                }
            }
            positions.add(newPosition);
        });
        System.out.println("Positions: " + positions);
        long answer = positions.stream()
                .filter(position -> position.equals(BigInteger.ZERO))
                .count();
        System.out.println("Answer: " + answer);
    }
}
