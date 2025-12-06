import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Advent1001 {
    public static void main(String[] args) {
        /*String input = "";*/
        String input = "";
        String[] parts = input.split("\n");

        List<List<Content>> entriesLines = new ArrayList<>();
        for (int lineIndex = 0; lineIndex < parts.length; lineIndex++) {
            entriesLines.add(mapLine(parts[lineIndex], lineIndex));
        }
        Content startContent = retrieveS(entriesLines);

        List<Content> contents = getPipeMapping(entriesLines, startContent);
        int pipeIndex = contents.size() / 2;
        System.out.println("Middle pipe number = " + pipeIndex);


    }

    private static List<Content> mapLine(String line, int lineIndex) {
        List<Content> contentList = new ArrayList<>();
        for (int i = 0; i < line.length(); i++) {
            char charac = line.charAt(i);
            switch (charac) {
                case '.': {
                    contentList.add(new Content(Pipe.NONE, new Position(lineIndex, i)));
                    break;
                }
                case '-': {
                    contentList.add(new Content(Pipe.HORIZ, new Position(lineIndex, i)));
                    break;
                }
                case '|': {
                    contentList.add(new Content(Pipe.VERTIC, new Position(lineIndex, i)));
                    break;
                }
                case 'F': {
                    contentList.add(new Content(Pipe.F, new Position(lineIndex, i)));
                    break;
                }
                case '7': {
                    contentList.add(new Content(Pipe.SEVEN, new Position(lineIndex, i)));
                    break;
                }
                case 'L': {
                    contentList.add(new Content(Pipe.L, new Position(lineIndex, i)));
                    break;
                }
                case 'J': {
                    contentList.add(new Content(Pipe.J, new Position(lineIndex, i)));
                    break;
                }
                default: {
                    contentList.add(new Content(Pipe.S, new Position(lineIndex, i)));
                    break;
                }
            }
        }
        return contentList;
    }

    private static Content retrieveS(List<List<Content>> pipesLines) {
        for (List<Content> contents : pipesLines) {
            for (Content content : contents) {
                if (content.getPipe().equals(Pipe.S)) {
                    return content;
                }
            }
        }
        return null;
    }

    static List<Content> getPipeMapping(List<List<Content>> entriesList, Content sContent) {
        List<Content> contents = new ArrayList<>();
        Direction direction = getFirstDirection(entriesList, sContent);
        Content actualContent = sContent;
        do {
            actualContent = getNextContent(entriesList, actualContent, direction);
            contents.add(actualContent);
            if (!actualContent.getPipe().equals(Pipe.S)){
                direction = actualContent.getPipe().getNextDirection(direction);
                System.out.println(actualContent);
            }
        } while (!actualContent.getPipe().equals(Pipe.S));
        return contents;
    }

    private static Direction getFirstDirection(List<List<Content>> entriesList, Content sContent) {
        Position position = sContent.getPosition();
        Content north = position.getLine() - 1 >= 0 ? entriesList.get(position.getLine() - 1).get(position.getColumn()) : new Content(Pipe.NONE, null);
        if (!north.getPipe().equals(Pipe.NONE) && north.getPipe().getDirections().contains(Direction.SOUTH)) {
            return Direction.NORTH;
        }
        Content west = position.getColumn() - 1 >= 0 ? entriesList.get(position.getLine()).get(position.getColumn() - 1) : new Content(Pipe.NONE, null);
        if (!west.getPipe().equals(Pipe.NONE) && west.getPipe().getDirections().contains(Direction.EAST)) {
            return Direction.WEST;
        }
        Content east = position.getColumn() + 1 < entriesList.get(position.getLine()).size() ? entriesList.get(position.getLine()).get(position.getColumn() + 1) : new Content(Pipe.NONE, null);
        if (!east.getPipe().equals(Pipe.NONE) && east.getPipe().getDirections().contains(Direction.WEST)) {
            return Direction.EAST;
        }
        Content south = position.getLine() + 1 < entriesList.size() ? entriesList.get(position.getLine() + 1).get(position.getColumn()) : new Content(Pipe.NONE, null);
        if (!south.getPipe().equals(Pipe.NONE) && south.getPipe().getDirections().contains(Direction.NORTH)) {
            return Direction.SOUTH;
        }
        return null;
    }
    public static Content getNextContent(List<List<Content>> entriesList, Content content, Direction direction){
        Position position = content.getPosition();
        int nextLine = position.getLine() + direction.getPosition().getLine();
        if (nextLine< entriesList.size()) {
            int nextColumn = position.getColumn() + direction.getPosition().getColumn();
            int lineSize = entriesList.get(nextLine).size();
            if (nextColumn<lineSize){
                return entriesList.get(nextLine).get(nextColumn);
            }
        }
        return null;
    }

    private static class Content {
        Pipe pipe;
        Position position;

        public Content(Pipe pipe, Position position) {
            this.pipe = pipe;
            this.position = position;
        }

        public Pipe getPipe() {
            return pipe;
        }

        public Position getPosition() {
            return position;
        }

        @Override
        public String toString() {
            return "Content{" +
                    "pipe=" + pipe +
                    ", position=" + position +
                    '}';
        }
    }

    private enum Pipe {
        NONE(),
        S(),
        HORIZ(Direction.WEST, Direction.EAST),
        VERTIC(Direction.NORTH, Direction.SOUTH),
        F(Direction.EAST, Direction.SOUTH),
        J(Direction.NORTH, Direction.WEST),
        L(Direction.NORTH, Direction.EAST),
        SEVEN(Direction.WEST, Direction.SOUTH);
        final ArrayList<Direction> directions;

        Pipe(Direction... directions) {
            this.directions = new ArrayList<>(Arrays.stream(directions).toList());
        }

        public ArrayList<Direction> getDirections() {
            return directions;
        }
        public Direction getNextDirection(Direction previousDirection){
            Direction newDirection = null;
            switch (previousDirection){
                case NORTH -> newDirection = Direction.SOUTH;
                case SOUTH -> newDirection = Direction.NORTH;
                case EAST ->  newDirection = Direction.WEST;
                case WEST -> newDirection = Direction.EAST;
            }
            List<Direction> directionList = new ArrayList<>(this.directions);
            directionList.remove(newDirection);
            return directionList.getFirst();
        }
    }

    private enum Direction {
        NORTH(-1, 0),
        SOUTH(1, 0),
        WEST(0, -1),
        EAST(0, 1);
        Position position;

        Direction(int deltaLine, int deltaColumn) {
            this.position = new Position(deltaLine, deltaColumn);
        }

        public Position getPosition() {
            return position;
        }
    }

    private static class Position {
        int line;
        int column;

        public Position(int line, int column) {
            this.line = line;
            this.column = column;
        }

        public int getLine() {
            return line;
        }

        public int getColumn() {
            return column;
        }

        @Override
        public String toString() {
            return "Position{" +
                    "line=" + line +
                    ", column=" + column +
                    '}';
        }
    }

}