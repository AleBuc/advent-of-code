import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

class Advent1002 {
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
        contents = contents.stream().peek(content -> content.isLoop = true).toList();

        Long count = getInCount(entriesLines);

        System.out.println("Enclosed tiles = " + count);


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
        Direction firstDirection = getFirstDirection(entriesList, sContent);
        Direction direction = SerializationUtils.clone(firstDirection);
        Content actualContent = sContent;
        do {
            actualContent = getNextContent(entriesList, actualContent, direction);
            contents.add(actualContent);
            if (!actualContent.getPipe().equals(Pipe.S)) {
                direction = actualContent.getPipe().getNextDirection(direction);
                System.out.println(actualContent);
            }
        } while (!actualContent.getPipe().equals(Pipe.S));
        sContent.setCorner(isSACorner(firstDirection, direction));
        sContent.setsDirections(List.of(firstDirection, getOppositeDirection(direction)));
        contents.removeLast();
        contents.add(sContent);
        return contents;
    }

    private static Direction getFirstDirection(List<List<Content>> entriesList, Content sContent) {
        Position position = sContent.getPosition();
        Content north = position.getLine() - 1 >= 0 ? entriesList.get(position.getLine() - 1).get(position.getColumn()) : new Content(Pipe.NONE, null);
        if (!north.getPipe().equals(Pipe.NONE) && north.getPipe().directions.contains(Direction.SOUTH)) {
            return Direction.NORTH;
        }
        Content west = position.getColumn() - 1 >= 0 ? entriesList.get(position.getLine()).get(position.getColumn() - 1) : new Content(Pipe.NONE, null);
        if (!west.getPipe().equals(Pipe.NONE) && west.getPipe().directions.contains(Direction.EAST)) {
            return Direction.WEST;
        }
        Content east = position.getColumn() + 1 < entriesList.get(position.getLine()).size() ? entriesList.get(position.getLine()).get(position.getColumn() + 1) : new Content(Pipe.NONE, null);
        if (!east.getPipe().equals(Pipe.NONE) && east.getPipe().directions.contains(Direction.WEST)) {
            return Direction.EAST;
        }
        Content south = position.getLine() + 1 < entriesList.size() ? entriesList.get(position.getLine() + 1).get(position.getColumn()) : new Content(Pipe.NONE, null);
        if (!south.getPipe().equals(Pipe.NONE) && south.getPipe().directions.contains(Direction.NORTH)) {
            return Direction.SOUTH;
        }
        return null;
    }

    public static Content getNextContent(List<List<Content>> entriesList, Content content, Direction direction) {
        Position position = content.getPosition();
        int nextLine = position.getLine() + direction.getPosition().getLine();
        if (nextLine < entriesList.size()) {
            int nextColumn = position.getColumn() + direction.getPosition().getColumn();
            int lineSize = entriesList.get(nextLine).size();
            if (nextColumn < lineSize) {
                return entriesList.get(nextLine).get(nextColumn);
            }
        }
        return null;
    }

    private static boolean isSACorner(Direction directionA, Direction directionB) {
        List<Direction> directions = List.of(directionA, directionB);
        return !List.of(Direction.NORTH, Direction.SOUTH).containsAll(directions) && !List.of(Direction.WEST, Direction.EAST).containsAll(directions);
    }

    private static Direction getOppositeDirection(Direction direction) {
        if (Direction.NORTH.equals(direction)) {
            return Direction.SOUTH;
        }
        if (Direction.SOUTH.equals(direction)) {
            return Direction.NORTH;
        }
        if (Direction.WEST.equals(direction)) {
            return Direction.EAST;
        }
        if (Direction.EAST.equals(direction)) {
            return Direction.WEST;
        }
        return null;
    }


    private static long getInCount(List<List<Content>> contentsList) {
        Long count = 0L;

        for (List<Content> contents : contentsList) {
            long lineCount = 0L;
            long barrierCount = 0L;
            boolean isIn = false;
            Iterator<Content> contentIterator = contents.iterator();
            while (contentIterator.hasNext()) {
                Content content = contentIterator.next();
                if (content.isLoop) {
                    if (content.isCorner) {
                        barrierCount += countCornerBarrier(content, contentIterator);
                    } else {
                        barrierCount++;
                    }
                } else {
                    if (barrierCount % 2 != 0) {
                        isIn = !isIn;
                    }
                    barrierCount = 0L;
                    if (isIn) {
                        lineCount++;
                    }
                }
            }
            System.out.println(lineCount);
            count += lineCount;
        }

        return count;
    }

    // Returns 2 if the corners form a U loop. Returns 1 if the corners forms a Z loop.
    private static long countCornerBarrier(Content cornerA, Iterator<Content> contentIterator) {
        Direction directionA = getCornerVersticalDirection(cornerA);
        Content cornerB = new Content(null, null);
        cornerB.setCorner(false);
        while (!cornerB.isCorner) {
            cornerB = contentIterator.next();
        }
        Direction directionB = getCornerVersticalDirection(cornerB);
        if (directionA.equals(directionB)) {
            return 2L;
        } else {
            return 1L;
        }
    }

    private static Direction getCornerVersticalDirection(Content corner) {
        List<Direction> directions;
        if (corner.getPipe().equals(Pipe.S)) {
            directions = corner.sDirections;
        } else {
            directions = corner.getPipe().getDirections();
        }
        return directions.stream().filter(direction -> List.of(Direction.NORTH, Direction.SOUTH).contains(direction)).findFirst().get();

    }


    static class Content {
        Pipe pipe;
        Position position;

        boolean isLoop;

        boolean isCorner;
        List<Direction> sDirections = new ArrayList<>(2);

        public void setsDirections(List<Direction> sDirections) {
            this.sDirections = sDirections;
        }

        public void setLoop(boolean loop) {
            isLoop = loop;
        }

        public void setCorner(boolean corner) {
            isCorner = corner;
        }

        public boolean isLoop() {
            return isLoop;
        }

        public Content(Pipe pipe, Position position) {
            this.pipe = pipe;
            this.position = position;
            if (pipe != null) {
                this.isCorner = List.of(Pipe.L, Pipe.J, Pipe.F, Pipe.SEVEN).contains(pipe);
            }
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
                    ", isLoop=" + isLoop +
                    ", isCorner=" + isCorner +
                    '}';
        }
    }

    enum Pipe {
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

        public Direction getNextDirection(Direction previousDirection) {
            Direction newDirection = null;
            switch (previousDirection) {
                case NORTH -> newDirection = Direction.SOUTH;
                case SOUTH -> newDirection = Direction.NORTH;
                case EAST -> newDirection = Direction.WEST;
                case WEST -> newDirection = Direction.EAST;
            }
            List<Direction> directionList = new ArrayList<>(this.directions);
            directionList.remove(newDirection);
            return directionList.getFirst();
        }
    }

    enum Direction {
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

    static class Position {
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