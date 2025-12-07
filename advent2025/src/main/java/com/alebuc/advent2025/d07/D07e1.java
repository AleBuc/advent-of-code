package com.alebuc.advent2025.d07;

import com.alebuc.advent2025.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class D07e1 {
    private static final String START = "S";
    private static final String SPLITTER = "^";

    public static void main(String[] args) {
        String fileName = "/ex07.txt";
//        String fileName = "/data07.txt";
        List<String> contentLines = new ArrayList<>(Utils.readResourceFileAsLines(fileName));
        System.out.println("Content lines: " + contentLines);

        Set<Integer> beamPositions = new HashSet<>();
        beamPositions.add(contentLines.getFirst().indexOf(START));

        int effectiveSplitterCount = 0;
        for (int i = 1; i < contentLines.size(); i++) {
            String line = contentLines.get(i);
            for (int j = 0; j < line.length(); j++) {
                char charac = line.charAt(j);
                if (beamPositions.contains(j) && charac == SPLITTER.charAt(0)) {
                    effectiveSplitterCount++;
                    beamPositions.remove(j);
                    beamPositions.add(j - 1);
                    beamPositions.add(j + 1);
                }
            }
        }
        System.out.println("Effective splitter count: " + effectiveSplitterCount);
    }
}
