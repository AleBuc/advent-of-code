package com.alebuc.advent2025.d11;

import com.alebuc.advent2025.utils.Utils;

import java.math.BigInteger;
import java.util.*; // pour Map, HashMap, Set, HashSet
import java.util.concurrent.atomic.AtomicInteger;

public class D11e2 {
    private static final String OUT = "out";
    private static final String SVR = "svr";
    private static final String DAC = "dac";
    private static final String FFT = "fft";

    public static void main(String[] args) {
        String fileName = "/ex11-2.txt";
//        String fileName = "/data11.txt";
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

        Map<MemoKey, MemoValue> memoWaysToOut = new HashMap<>();
        Map<MemoKey, MemoValue> memoWaysToSvr = new HashMap<>();

        tryToFindTarget(
                OUT,
                DAC,
                mapping,
                DAC,
                new HashSet<>(),
                countBetweenDacAndFft,
                countDacToOut,
                memoWaysToOut
        );
        tryToFindTarget(
                OUT,
                FFT,
                mapping,
                FFT,
                new HashSet<>(),
                countBetweenFftAndDac,
                countFftToOut,
                memoWaysToOut
        );

        tryToFindTarget(
                SVR,
                DAC,
                reversedMapping,
                DAC,
                new HashSet<>(),
                countBetweenDacAndFft,
                countDacToSvr,
                memoWaysToSvr
        );
        tryToFindTarget(
                SVR,
                FFT,
                reversedMapping,
                FFT,
                new HashSet<>(),
                countBetweenFftAndDac,
                countFftToSvr,
                memoWaysToSvr
        );

        BigInteger svrToDacToFftToOutCount = BigInteger
                .valueOf(countBetweenDacAndFft.get())
                .multiply(BigInteger.valueOf(countDacToSvr.get()))
                .multiply(BigInteger.valueOf(countFftToOut.get()));

        BigInteger svrToFftToDacToOutCount = BigInteger
                .valueOf(countBetweenFftAndDac.get())
                .multiply(BigInteger.valueOf(countFftToSvr.get()))
                .multiply(BigInteger.valueOf(countDacToOut.get()));

        return svrToDacToFftToOutCount.add(svrToFftToDacToOutCount);
    }

    private static void tryToFindTarget(String target,
                                        String init,
                                        Map<String, List<String>> mapping,
                                        String currentPosition,
                                        Set<String> visitedPositions,
                                        AtomicInteger countBetweenDacAndFft,
                                        AtomicInteger countToTarget,
                                        Map<MemoKey, MemoValue> memo) {

        MemoKey key = new MemoKey(target, init, currentPosition);
        MemoValue cached = memo.get(key);
        if (cached != null) {
            countToTarget.addAndGet(cached.countToTarget());
            countBetweenDacAndFft.addAndGet(cached.countBetweenDacAndFft());
            return;
        }

        List<String> choices = mapping.get(currentPosition);
        if (choices == null || choices.isEmpty()) {
            memo.put(key, new MemoValue(0, 0));
            return;
        }

        int localCountToTarget = 0;
        int localCountBetweenDacAndFft = 0;
        if (choices.contains(target)) {
            localCountToTarget++;
            if (OUT.equals(target)) {
                if (currentPosition.equals(DAC) && init.equals(FFT)) {
                    localCountBetweenDacAndFft++;
                } else if (currentPosition.equals(FFT) && init.equals(DAC)) {
                    localCountBetweenDacAndFft++;
                }
            }
            countToTarget.incrementAndGet();
            countBetweenDacAndFft.addAndGet(localCountBetweenDacAndFft);

            memo.put(key, new MemoValue(localCountToTarget, localCountBetweenDacAndFft));
            return;
        }

        if (OUT.equals(target)) {
            if (currentPosition.equals(DAC) && init.equals(FFT)) {
                localCountBetweenDacAndFft++;
                countBetweenDacAndFft.incrementAndGet();
                memo.put(key, new MemoValue(0, localCountBetweenDacAndFft));
                return;
            } else if (currentPosition.equals(FFT) && init.equals(DAC)) {
                localCountBetweenDacAndFft++;
                countBetweenDacAndFft.incrementAndGet();
                memo.put(key, new MemoValue(0, localCountBetweenDacAndFft));
                return;
            }
        }
        if (SVR.equals(target)) {
            if ((currentPosition.equals(DAC) && init.equals(FFT))
                || (currentPosition.equals(FFT) && init.equals(DAC))) {
                memo.put(key, new MemoValue(0, 0));
                return;
            }
        }

        for (String choice : choices) {
            if (!visitedPositions.add(choice)) {
                continue;
            }
            int beforeCountToTarget = countToTarget.get();
            int beforeCountBetween = countBetweenDacAndFft.get();

            tryToFindTarget(
                    target,
                    init,
                    mapping,
                    choice,
                    visitedPositions,
                    countBetweenDacAndFft,
                    countToTarget,
                    memo
            );
            int deltaToTarget = countToTarget.get() - beforeCountToTarget;
            int deltaBetween = countBetweenDacAndFft.get() - beforeCountBetween;
            localCountToTarget += deltaToTarget;
            localCountBetweenDacAndFft += deltaBetween;

            visitedPositions.remove(choice);
        }
        memo.put(key, new MemoValue(localCountToTarget, localCountBetweenDacAndFft));
    }

}
