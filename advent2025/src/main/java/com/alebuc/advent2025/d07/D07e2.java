package com.alebuc.advent2025.d07;

import com.alebuc.advent2025.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class D07e2 {
    private static final String START = "S";
    private static final String SPLITTER = "^";

    public static void main(String[] args) {
        String fileName = "/ex07.txt";
//        String fileName = "/data07.txt";
        List<String> contentLines = new ArrayList<>(Utils.readResourceFileAsLines(fileName));
        System.out.println("Content lines: " + contentLines);

        Map<Integer, Long> beamCountByPosition = new HashMap<>();
        beamCountByPosition.put(contentLines.getFirst().indexOf(START), 1L);

        for (int i = 1; i < contentLines.size(); i++) {
            String line = contentLines.get(i);
            System.out.println("Beam positions: " + beamCountByPosition);
            updateBeamPositions(beamCountByPosition, line);
        }
        System.out.println("Potential beam positions: " + beamCountByPosition);
        long count = beamCountByPosition.values().stream().mapToLong(Long::longValue).sum();
        System.out.println("Count: " + count);
    }

    private static void updateBeamPositions(Map<Integer, Long> beamCountByPosition, String line) {
        Map<Integer, Long> updatedMap = new HashMap<>(beamCountByPosition);
        for (Map.Entry<Integer, Long> entry : beamCountByPosition.entrySet()) {
            if (entry.getValue() == 0) {
                continue;
            }
            char charac = line.charAt(entry.getKey());
            if (charac == SPLITTER.charAt(0)) {
                updatedMap.put(entry.getKey(), 0L);
                updatedMap.merge(entry.getKey() - 1, entry.getValue(), Long::sum);
                updatedMap.merge(entry.getKey() + 1, entry.getValue(), Long::sum);
            }
        }
        beamCountByPosition.clear();
        beamCountByPosition.putAll(updatedMap);
    }
}
