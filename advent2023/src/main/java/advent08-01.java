import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

class Advent0801 {
    public static void main(String[] args) {
/*        String input = "";*/
/*        String input = "";*/
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
        String actualNode = "AAA";
        long count = 0;
        do {
            String nextNode = null;
            for (char character : sequenceChars) {
                Instruction instruction = instructionMap.get(actualNode);
                if (character == 'L') {
                    nextNode = instruction.getLeft();
                }
                if (character == 'R') {
                    nextNode = instruction.getRight();
                }
                count++;
                actualNode = nextNode;
                System.out.println(nextNode);
                if ("ZZZ".equals(nextNode)) {
                    break;
                }
            }

        } while (!"ZZZ".equals(actualNode));

        System.out.println(count);


    }

    static Map.Entry<String, Instruction> mapInstruction(String line) {
        String[] parts = line.split(" = ");
        String id = parts[0];
        String[] instructionString = parts[1].replace("(", "").replace(")", "").split(", ");
        Instruction instruction = new Instruction(instructionString[0], instructionString[1]);
        return entry(id, instruction);
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

}