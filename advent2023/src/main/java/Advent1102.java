import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

class Advent1102 {
    public static void main(String[] args) {
        /*String input = "";*/
        String input = "";


        Map<Coordinate, Content> spaceMap = getSpaceMap(input);

        Set<Galaxy> galaxies = spaceMap.values().stream().filter(content -> content instanceof Galaxy).map(content -> (Galaxy) content).collect(Collectors.toSet());
        Set<Set<Galaxy>> galaxiesPairs = getGalaxiesPairs(galaxies);
        System.out.println("Number of pairs: " + galaxiesPairs.size());
        Map<Set<Galaxy>, Long> galaxiesPairsDistances = getGalaxiesPairsDistances(galaxiesPairs, spaceMap);
        System.out.println("Distances: " + galaxiesPairsDistances.entrySet());
        long sum = galaxiesPairsDistances.values().stream().reduce(0L, Long::sum);
        System.out.println("Sum: " + sum);

    }

    private static Map<Coordinate, Content> getSpaceMap(String input) {
        String[] parts = input.split("\n");
        AtomicInteger galaxyIndex = new AtomicInteger(1);
        Map<Coordinate, Content> spaceMap = new HashMap<>();
        for (int lineIndex = 0; lineIndex < parts.length; lineIndex++) {
            List<Content> contents = getGalaxyLineMap(parts[lineIndex], lineIndex, galaxyIndex);
            for (Content content : contents) {
                spaceMap.put(new Coordinate(content.getX(), content.getY()), content);
            }
        }
        analyseColumns(spaceMap, parts.length);
        return spaceMap;
    }

    private static List<Content> getGalaxyLineMap(String input, int lineIndex, AtomicInteger galaxyIndex) {
        if (lineIndex == 139) {
            System.out.println(139);
        }
        List<Content> lineContent = new ArrayList<>();
        String[] entries = input.split("");
        for (int columnIndex = 0; columnIndex < entries.length; columnIndex++) {
            String entry = entries[columnIndex];
            if ("#".equals(entry)) {
                lineContent.add(columnIndex, new Galaxy(galaxyIndex.getAndIncrement(), columnIndex, lineIndex, 1, 1));
            } else {
                lineContent.add(columnIndex, new EmptySpace(columnIndex, lineIndex, 1, 1));
            }
        }
        if (lineContent.stream().map(Content::getClass).distinct().count() == 1) {
            for (Content content : lineContent) {
                content.setLineValue(1000000);
            }
        }
        return lineContent;
    }

    private static void analyseColumns(Map<Coordinate, Content> spaceMap, int columnSize) {

        Set<Map.Entry<Coordinate, Content>> allGalaxyEntries = spaceMap.entrySet();
        for (int column = 0; column < columnSize; column++) {
            int finalColumn = column;
            List<Content> columnContents = allGalaxyEntries.stream().filter(entry -> entry.getKey().getX() == finalColumn).map(Map.Entry::getValue).collect(Collectors.toList());
            Set<Class<? extends Content>> columnClasses = columnContents.stream().map(Content::getClass).collect(Collectors.toSet());
            if (columnClasses.size() == 1) {
                for (Content content : columnContents) {
                    content.setColumnValue(1000000);
                }
            }
        }
    }

    private static Set<Set<Galaxy>> getGalaxiesPairs(Set<Galaxy> galaxies) {
        Set<Set<Galaxy>> galaxiesPairs = new HashSet<>();
        for (Galaxy galaxy : galaxies) {
            Set<Galaxy> otherGalaxies = galaxies.stream().filter(galaxy1 -> !galaxy1.equals(galaxy)).collect(Collectors.toCollection(TreeSet::new));
            for (Galaxy otherGalaxy : otherGalaxies) {
                galaxiesPairs.add(Set.of(galaxy, otherGalaxy));
            }
        }
        return galaxiesPairs;
    }

    private static Map<Set<Galaxy>, Long> getGalaxiesPairsDistances(Set<Set<Galaxy>> galaxiesPairs, Map<Coordinate, Content> spaceMap) {
        Map<Set<Galaxy>, Long> galaxiesPairsDistances = new HashMap<>();
        for (Set<Galaxy> galaxiesPair : galaxiesPairs) {
            Map.Entry<Set<Galaxy>, Long> galaxiesPairsDistance = getGalaxiesPairDistance(galaxiesPair, spaceMap);
            galaxiesPairsDistances.put(galaxiesPairsDistance.getKey(), galaxiesPairsDistance.getValue());
        }
        return galaxiesPairsDistances;
    }

    private static Map.Entry<Set<Galaxy>, Long> getGalaxiesPairDistance(Set<Galaxy> galaxiesPair, Map<Coordinate, Content> spaceMap) {
        Galaxy[] galaxyArray = galaxiesPair.toArray(Galaxy[]::new);
        System.out.println(galaxiesPair);
        Galaxy galaxy1 = galaxyArray[0];
        Galaxy galaxy2 = galaxyArray[1];
        int xMin = Integer.min(galaxy1.getX(), galaxy2.getX());
        int yMin = Integer.min(galaxy1.getY(), galaxy2.getY());
        int xMax = Integer.max(galaxy1.getX(), galaxy2.getX());
        int yMax = Integer.max(galaxy1.getY(), galaxy2.getY());
        long distance = 0;
        for (int x = xMin; x < xMax; x++) {
            Content content = spaceMap.get(new Coordinate(x, yMin));
            try {
                distance += content.getColumnValue();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        for (int y = yMin; y < yMax; y++) {
            Content content = spaceMap.get(new Coordinate(xMin, y));
            distance += content.getLineValue();
        }
        return Map.entry(galaxiesPair, distance);

    }


    private static class Galaxy extends Content implements Comparable<Galaxy> {
        private final long id;

        public long getId() {
            return id;
        }

        public Galaxy(long id, int x, int y, int lineValue, int columnValue) {
            super(x, y, lineValue, columnValue);
            this.id = id;
        }

        @Override
        public String toString() {
            return "Galaxy{" +
                    "id=" + id +
                    '}';
        }

        @Override
        public int compareTo(Galaxy o) {
            int result;
            if (this.id < o.id){
                return 1;
            } else if (this.id > o.id) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private static class EmptySpace extends Content {
        public EmptySpace(int x, int y, int lineValue, int columnValue) {
            super(x, y, lineValue, columnValue);
        }
    }

    private static class Content {
        int x;
        int y;
        int lineValue;
        int columnValue;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getLineValue() {
            return lineValue;
        }

        public void setLineValue(int lineValue) {
            this.lineValue = lineValue;
        }

        public int getColumnValue() {
            return columnValue;
        }

        public void setColumnValue(int columnValue) {
            this.columnValue = columnValue;
        }

        public Content(int x, int y, int lineValue, int columnValue) {
            this.x = x;
            this.y = y;
            this.lineValue = lineValue;
            this.columnValue = columnValue;
        }

        @Override
        public String toString() {
            return "Content{" +
                   "x=" + x +
                   ", y=" + y +
                   ", lineValue=" + lineValue +
                   ", columnValue=" + columnValue +
                   '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Content content = (Content) o;
            return x == content.x && y == content.y && lineValue == content.lineValue && columnValue == content.columnValue;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, lineValue, columnValue);
        }
    }

    private static class Coordinate {
        int x;
        int y;

        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public String toString() {
            return "Coordinate{" +
                   "x=" + x +
                   ", y=" + y +
                   '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coordinate that = (Coordinate) o;
            return x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

}