package com.alebuc.advent2025.d08;

import com.alebuc.advent2025.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class D08e1 {
    public static void main(String[] args) {
        // int numberOfConnections = 10;
        // String fileName = "/ex08.csv";
        int numberOfConnections = 1000;
        String fileName = "/data08.csv";

        List<String> contentLines = new ArrayList<>(Utils.readResourceFileAsLines(fileName));

        List<Junction> junctions = contentLines.stream()
                .map(line -> {
                    String[] parts = StringUtils.split(line, ",");
                    return new Junction(
                            new BigInteger(parts[0]),
                            new BigInteger(parts[1]),
                            new BigInteger(parts[2])
                    );
                })
                .toList();

        TreeSet<Connection> connections = new TreeSet<>();
        for (int i = 0; i < junctions.size(); i++) {
            for (int j = i + 1; j < junctions.size(); j++) {
                connections.add(new Connection(junctions.get(i), junctions.get(j)));
            }
        }

        TreeSet<Circuit> circuits = new TreeSet<>();
        Map<Junction, Circuit> junctionToCircuit = new HashMap<>();

        for (int i = 0; i < numberOfConnections; i++) {
            Connection connection = connections.pollFirst();
            if (connection == null) {
                break;
            }

            Iterator<Junction> it = connection.junctions().iterator();
            Junction a = it.next();
            Junction b = it.next();

            Circuit ca = junctionToCircuit.get(a);
            Circuit cb = junctionToCircuit.get(b);

            if (ca == null && cb == null) {
                // new circuit
                Circuit c = new Circuit(connection);
                circuits.add(c);
                junctionToCircuit.put(a, c);
                junctionToCircuit.put(b, c);
            } else if (ca != null && cb == null) {
                // only a in a circuit
                ca.addConnection(connection);
                junctionToCircuit.put(b, ca);
            } else if (ca == null && cb != null) {
                // only b in a circuit
                cb.addConnection(connection);
                junctionToCircuit.put(a, cb);
            } else if (ca == cb) {
                // a and b are already in the same circuit
                ca.addConnection(connection);
            } else {
                // fusion
                circuits.remove(ca);
                circuits.remove(cb);

                for (Connection conn : cb.connections()) {
                    ca.addConnection(conn);
                }
                ca.addConnection(connection);

                for (Junction j : cb.junctions()) {
                    junctionToCircuit.put(j, ca);
                }

                circuits.add(ca);
            }
        }

        TreeSet<Circuit> keptCircuits = new TreeSet<>();
        for (int i = 0; i < 3; i++) {
            Circuit c = circuits.pollLast();
            if (c == null) {
                break;
            }
            keptCircuits.add(c);
        }

        System.out.println("Kept circuits sizes: ");
        keptCircuits.forEach(circuit -> System.out.println(circuit.junctions().size()));

        int result = keptCircuits.stream()
                .mapToInt(circuit -> circuit.junctions().size())
                .reduce(1, Math::multiplyExact);

        System.out.println("Result: " + result);
    }

}
