package com.alebuc.advent2025.d10;

import java.util.Map;

public record LightPattern(
        Map<Integer, Boolean> stateByIndex
) {
}
