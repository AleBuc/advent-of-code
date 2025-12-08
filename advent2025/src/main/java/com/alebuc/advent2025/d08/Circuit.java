package com.alebuc.advent2025.d08;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public record Circuit(
        Set<Junction> junctions,
        Set<Connection> connections
) implements Comparable<Circuit> {

    public Circuit(Connection connection) {
        this(
                new HashSet<>(connection.junctions()),
                new HashSet<>(Set.of(connection))
        );
    }

    public void addConnection(Connection connection) {
        if (!this.junctions.containsAll(connection.junctions())) {
            this.connections.add(connection);
            this.junctions.addAll(connection.junctions());
        }
    }

    @Override
    public int compareTo(Circuit o) {
        int comparison = junctions.size() - o.junctions.size();
        if (comparison == 0) {
            BigDecimal thisTotalDistance = connections.stream()
                    .map(Connection::distance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal otherTotalDistance = o.connections.stream()
                    .map(Connection::distance)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return thisTotalDistance.compareTo(otherTotalDistance);
        } else {
            return comparison;
        }
    }
}
