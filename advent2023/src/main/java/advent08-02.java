import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Map.entry;

class Advent0802b {
    public static void main(String[] args) {
        /*String input = "";*/
        String input = "";
        String[] lines = input.split("\n");
        String sequence = lines[0];
        char[] sequenceChars = sequence.toCharArray();
        Map<String, Instruction> instructionMap = new HashMap<>();
        for (int i = 2; i < lines.length; i++) {
            String line = lines[i];
            Map.Entry<String, Instruction> instructionEntry = mapInstruction(line);
            instructionMap.put(instructionEntry.getKey(), instructionEntry.getValue());
        }

        Map<String, Instruction> nodes = new HashMap<>(instructionMap.entrySet().stream()
                .filter(entry -> entry.getKey().charAt(entry.getKey().length() - 1) == 'A')
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        Map<String, List<Summit>> analysisData = new HashMap<>();

        for (Map.Entry<String, Instruction> nodeEntry : nodes.entrySet()) {
            long count = 0;
            long cycle = 0;
            List<Summit> summitList = new ArrayList<>();
            Map.Entry<String, Instruction> actualNode = nodeEntry;
            do {
                for (int i = 0; i < sequenceChars.length; i++) {
                    char character = sequenceChars[i];
                    Map.Entry<String, Instruction> newInstruction = getNextNode(character, actualNode, instructionMap);
                    actualNode = Map.entry(newInstruction.getKey(), newInstruction.getValue());
                    count++;
                    if (newInstruction.getKey().charAt(2) == 'Z') {
                        Summit summit = new Summit(newInstruction.getKey(), cycle, i, count);
                        if (summitList.stream().anyMatch(summit1 -> summit1.sequenceIndex == summit.sequenceIndex)) {
                            summitList.add(summit);
                            break;
                        }
                        summitList.add(summit);
                    }


                }
                cycle++;
            } while (summitList.stream().filter(summit1 -> summit1.sequenceIndex == summitList.getLast().sequenceIndex).count() < 2);
            analysisData.put(nodeEntry.getKey(), summitList);
        }

        long lcm = lcm(analysisData.values().stream().map(summits -> summits.getFirst().sequenceNbr).toList());
        System.out.println("Result: " + lcm);


    }

    private static Map.Entry<String, Instruction> getNextNode(char sequenceChar, Map.Entry<String, Instruction> instructionEntry, Map<String, Instruction> instructionMap) {
        String nextNodeKey = "";
        Instruction instruction = instructionEntry.getValue();
        if (sequenceChar == 'L') {
            nextNodeKey = instruction.getLeft();
        }
        if (sequenceChar == 'R') {
            nextNodeKey = instruction.getRight();
        }
        return Map.entry(nextNodeKey, instructionMap.get(nextNodeKey));

    }

    static Map.Entry<String, Instruction> mapInstruction(String line) {
        String[] parts = line.split(" = ");
        String id = parts[0];
        String[] instructionString = parts[1].replace("(", "").replace(")", "").split(", ");
        Instruction instruction = new Instruction(instructionString[0], instructionString[1]);
        return entry(id, instruction);
    }

    private static long gcd(long a, long b) {
        while (b > 0) {
            long temp = b;
            b = a % b; // % is remainder
            a = temp;
        }
        return a;
    }

    private static long lcm(long a, long b) {
        return a * (b / gcd(a, b));
    }

    private static long lcm(List<Long> input) {
        long result = input.get(0);
        for (int i = 1; i < input.size(); i++) result = lcm(result, input.get(i));
        return result;
    }

    static class Instruction {
        private final String left;
        private final String right;

        public Instruction(String left, String right) {
            this.left = left;
            this.right = right;
        }

        public String getLeft() {
            return left;
        }

        public String getRight() {
            return right;
        }

        @Override
        public String toString() {
            return "Instruction{" +
                    "left='" + left + '\'' +
                    ", right='" + right + '\'' +
                    '}';
        }
    }

    static class Summit {
        private String node;
        private long cycle;
        private int sequenceIndex;
        private long sequenceNbr;

        public Summit(String node, long cycle, int sequenceIndex, long sequenceNbr) {
            this.node = node;
            this.cycle = cycle;
            this.sequenceIndex = sequenceIndex;
            this.sequenceNbr = sequenceNbr;
        }

        @Override
        public String toString() {
            return "Summit{" +
                    "node='" + node + '\'' +
                    ", cycle=" + cycle +
                    ", sequenceIndex=" + sequenceIndex +
                    ", sequenceNbr=" + sequenceNbr +
                    '}';
        }
    }

    static class Cycle {
        long frequency;
        long offset;

        public Cycle(long frequency, long offset) {
            this.frequency = frequency;
            this.offset = offset;
        }

        @Override
        public String toString() {
            return "Cycle{" +
                    "frequency=" + frequency +
                    ", offset=" + offset +
                    '}';
        }
    }

}