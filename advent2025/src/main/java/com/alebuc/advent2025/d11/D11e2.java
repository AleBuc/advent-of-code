package com.alebuc.advent2025.d11;

import com.alebuc.advent2025.utils.Utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class D11e2 {
    private static final String OUT = "out";
    private static final String SVR = "svr";
    private static final String DAC = "dac";
    private static final String FFT = "fft";

    public static void main(String[] args) {
//        String fileName = "/ex11-2.txt";
        String fileName = "/data11.txt";
        List<String> contentLines = new ArrayList<>(Utils.readResourceFileAsLines(fileName));
        System.out.println("Content lines: " + contentLines);

        Map<String, List<String>> mapping = new HashMap<>();
        for (String line : contentLines) {
            String[] parts = line.split(": ");
            mapping.put(parts[0], List.of(parts[1].trim().split(" ")));
        }
        Map<String, List<String>> reversedMapping = new HashMap<>();
        mapping.forEach((key, value) -> {
            for (String choice : value) {
                reversedMapping.putIfAbsent(choice, new ArrayList<>());
                reversedMapping.get(choice).add(key);
            }
        });
        System.out.println("Mapping: " + mapping);
        System.out.println("Reversed mapping: " + reversedMapping);
        BigInteger count = getCountRoutingsByDacAndFft(mapping, reversedMapping);
        System.out.println("Count: " + count);

    }

    private static BigInteger getCountRoutingsByDacAndFft(Map<String, List<String>> mapping, Map<String, List<String>> reversedMapping) {
        AtomicInteger countBetweenDacAndFft = new AtomicInteger(0);
        AtomicInteger countBetweenFftAndDac = new AtomicInteger(0);
        AtomicInteger countDacToOut = new AtomicInteger(0);
        AtomicInteger countFftToOut = new AtomicInteger(0);
        AtomicInteger countDacToSvr = new AtomicInteger(0);
        AtomicInteger countFftToSvr = new AtomicInteger(0);
        tryToFindTarget(OUT, DAC, mapping, mapping.get(DAC), DAC, new ArrayList<>(), countBetweenDacAndFft, countDacToOut);
        tryToFindTarget(OUT, FFT, mapping, mapping.get(FFT), FFT, new ArrayList<>(), countBetweenFftAndDac, countFftToOut);
        tryToFindTarget(SVR, DAC, reversedMapping, reversedMapping.get(DAC), DAC, new ArrayList<>(), countBetweenDacAndFft, countDacToSvr);
        tryToFindTarget(SVR, FFT, reversedMapping, reversedMapping.get(FFT), FFT, new ArrayList<>(), countBetweenFftAndDac, countFftToSvr);
        BigInteger svrToDacToFftToOutCount = BigInteger.valueOf(countBetweenDacAndFft.get()).multiply(BigInteger.valueOf(countDacToSvr.get())).multiply(BigInteger.valueOf(countFftToOut.get()));
        BigInteger svrToFftToDacToOutCount = BigInteger.valueOf(countBetweenFftAndDac.get()).multiply(BigInteger.valueOf(countFftToSvr.get())).multiply(BigInteger.valueOf(countDacToOut.get()));
        return svrToDacToFftToOutCount.add(svrToFftToDacToOutCount);
    }
    //TODO tests de rencontre

    private static void tryToFindTarget(String target,
                                        String init,
                                        Map<String, List<String>> mapping,
                                        List<String> choices,
                                        String currentPosition,
                                        List<String> visitedPositions,
                                        AtomicInteger countBetweenDacAndFft,
                                        AtomicInteger countToTarget) {
        if (choices.contains(target)) {
            countToTarget.incrementAndGet();
            List<String> route = new ArrayList<>(visitedPositions);
            route.add(currentPosition);
            route.add(target);
            System.out.println(route);
            return;
        }
        if (target.equals(OUT)) {
            if (currentPosition.equals(DAC) && init.equals(FFT)) {
                countBetweenDacAndFft.incrementAndGet();
                return;
            } else if (currentPosition.equals(FFT) && init.equals(DAC)) {
                countBetweenDacAndFft.incrementAndGet();
                return;
            }
        }
        if (target.equals(SVR)) {
            if (currentPosition.equals(DAC) && init.equals(FFT)) {
                return;
            } else if (currentPosition.equals(FFT) && init.equals(DAC)) {
                return;
            }
        }

        for (String choice : choices) {
            if (visitedPositions.contains(choice)) {
                continue;
            }
            List<String> newVisitedPositions = new ArrayList<>(visitedPositions);
            newVisitedPositions.add(currentPosition);
            tryToFindTarget(target,
                    init,
                    mapping,
                    mapping.get(choice),
                    choice,
                    newVisitedPositions,
                    countBetweenDacAndFft,
                    countToTarget
            );
        }
    }

}
