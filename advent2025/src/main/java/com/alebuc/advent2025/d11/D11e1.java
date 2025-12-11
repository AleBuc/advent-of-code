package com.alebuc.advent2025.d11;

import com.alebuc.advent2025.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class D11e1 {
    private static final String YOU = "you";
    private static final String OUT = "out";

    public static void main(String[] args) {
        String fileName = "/ex11.txt";
//        String fileName = "/data11.txt";
        List<String> contentLines = new ArrayList<>(Utils.readResourceFileAsLines(fileName));
        System.out.println("Content lines: " + contentLines);

        Map<String, List<String>> mapping = new HashMap<>();
        for (String line : contentLines) {
            String[] parts = line.split(": ");
            mapping.put(parts[0], List.of(parts[1].trim().split(" ")));
        }
        System.out.println("Mapping: " + mapping);
        Map<List<String>, Integer> distanceByRouting = getDistanceByRouting(mapping);
        System.out.println("Distance by routing: ");
        distanceByRouting.forEach((route, distance) -> System.out.println(route + " : " + distance));
        System.out.println("Count: " + distanceByRouting.size());
    }

    private static Map<List<String>, Integer> getDistanceByRouting(Map<String, List<String>> mapping) {
        Map<List<String>, Integer> distanceByRouting = new HashMap<>();
        tryToFindOut(mapping, mapping.get(YOU), YOU, new ArrayList<>(), distanceByRouting, 0);
        return distanceByRouting;
    }

    private static void tryToFindOut(Map<String, List<String>> mapping,
                                     List<String> choices,
                                     String currentPosition,
                                     List<String> visitedPositions,
                                     Map<List<String>, Integer> distanceByRouting,
                                     int distance) {
        if (choices.contains(OUT)) {
            List<String> route = new ArrayList<>(visitedPositions);
            route.add(currentPosition);
            route.add(OUT);
            int newDistance = distance + 1;
            distanceByRouting.put(route, newDistance);
            return;
        }
        for (String choice : choices) {
            if (visitedPositions.contains(choice)) {
                continue;
            }
            List<String> newVisitedPositions = new ArrayList<>(visitedPositions);
            newVisitedPositions.add(currentPosition);
            int newDistance = distance + 1;
            tryToFindOut(mapping, mapping.get(choice), choice, newVisitedPositions, distanceByRouting, newDistance);
        }
    }

}
