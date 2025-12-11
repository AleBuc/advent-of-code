package com.alebuc.advent2025.d11;

import java.util.Objects;

record MemoKey(String target, String init, String current) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemoKey(String target1, String init1, String current1))) return false;
        return Objects.equals(target, target1)
                && Objects.equals(init, init1)
                && Objects.equals(current, current1);
    }

}
