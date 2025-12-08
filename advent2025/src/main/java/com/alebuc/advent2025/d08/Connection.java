package com.alebuc.advent2025.d08;

import java.math.BigDecimal;
import java.util.Set;

public record Connection(
        Set<Junction> junctions,
        BigDecimal distance
) implements Comparable<Connection> {
    public Connection (Junction junctionA, Junction junctionB){
        this(Set.of(junctionA, junctionB),
                BigDecimal.valueOf(
                        Math.sqrt(
                                Math.pow(junctionA.x().subtract(junctionB.x()).doubleValue(), 2) +
                                        Math.pow(junctionA.y().subtract(junctionB.y()).doubleValue(), 2) +
                                        Math.pow(junctionA.z().subtract(junctionB.z()).doubleValue(), 2)
                        )
                )
        );
    }

    @Override
    public int compareTo(Connection o) {
        int comparison = this.distance.compareTo(o.distance);
        if (comparison == 0) {
            Integer thisHash = this.junctions.stream()
                    .map(Junction::hashCode)
                    .reduce(0, Integer::sum);
            Integer otherHash = o.junctions.stream()
                    .map(Junction::hashCode)
                    .reduce(0, Integer::sum);
            return thisHash.compareTo(otherHash);
        } else {
            return comparison;
        }
    }
}
