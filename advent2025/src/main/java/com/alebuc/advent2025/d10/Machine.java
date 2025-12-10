package com.alebuc.advent2025.d10;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Machine(
        LightPattern lightPattern,
        Set<Button> buttons
) {
    public static Machine createFromInstructions(String instructions) {
        //light pattern between []
        int startBracket = instructions.indexOf('[');
        int endBracket = instructions.indexOf(']');
        String patternStr = instructions.substring(startBracket + 1, endBracket);

        Map<Integer, Boolean> stateByIndex = new HashMap<>();
        for (int i = 0; i < patternStr.length(); i++) {
            stateByIndex.put(i, patternStr.charAt(i) == '#');
        }
        LightPattern lightPattern = new LightPattern(stateByIndex);

        //buttons within ()
        Set<Button> buttons = new HashSet<>();
        Pattern buttonPattern = Pattern.compile("\\(([^)]+)\\)");
        Matcher matcher = buttonPattern.matcher(instructions);

        while (matcher.find()) {
            String buttonContent = matcher.group(1);
            Set<Integer> lightPositions = new HashSet<>();

            // Parse comma-separated positions or single position
            if (buttonContent.contains(",")) {
                String[] positions = buttonContent.split(",");
                for (String pos : positions) {
                    lightPositions.add(Integer.parseInt(pos.trim()));
                }
            } else {
                lightPositions.add(Integer.parseInt(buttonContent.trim()));
            }

            buttons.add(new Button(lightPositions));
        }

        return new Machine(lightPattern, buttons);
    }
}
