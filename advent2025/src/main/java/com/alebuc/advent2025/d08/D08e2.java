package com.alebuc.advent2025.d08;

import com.alebuc.advent2025.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class D08e2 {
    public static void main(String[] args) {
         String fileName = "/ex08.csv";
//        String fileName = "/data08.csv";

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
        Connection lastConnection = null;
        int usedConnections = 0;
        int targetConnections = junctions.size() - 1;

        while (!connections.isEmpty() && usedConnections < targetConnections) {
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
                usedConnections++;
                lastConnection = connection;
            } else if (ca != null && cb == null) {
                // only a in a circuit
                ca.addConnection(connection);
                junctionToCircuit.put(b, ca);
                usedConnections++;
                lastConnection = connection;
            } else if (ca == null && cb != null) {
                // only b in a circuit
                cb.addConnection(connection);
                junctionToCircuit.put(a, cb);
                usedConnections++;
                lastConnection = connection;
            } else if (ca == cb) {
                // a and b are already in the same circuit
                continue;
            } else {
                // fusion
                circuits.remove(ca);
                circuits.remove(cb);

                Set<Junction> mergedJunctions = new HashSet<>(ca.junctions());
                mergedJunctions.addAll(cb.junctions());

                Set<Connection> mergedConnections = new HashSet<>(ca.connections());
                mergedConnections.addAll(cb.connections());
                mergedConnections.add(connection);

                Circuit merged = new Circuit(mergedJunctions, mergedConnections);
                circuits.add(merged);

                for (Junction j : mergedJunctions) {
                    junctionToCircuit.put(j, merged);
                }
                usedConnections++;
                lastConnection = connection;

            }
        }

        if (lastConnection == null || usedConnections != targetConnections) {
            System.out.println("Impossible to connect all junctions into a single circuit.");
            return;
        }

        Iterator<Junction> junctionIterator = lastConnection.junctions().iterator();
        Junction junctionA = junctionIterator.next();
        Junction junctionB = junctionIterator.next();

        BigInteger xA = junctionA.x();
        BigInteger xB = junctionB.x();

        BigInteger result = xA.multiply(xB);
        System.out.println("Last connection between junctions: " + junctionA + " and " + junctionB);
        System.out.println("Result (xA * xB): " + result);
    }

}
