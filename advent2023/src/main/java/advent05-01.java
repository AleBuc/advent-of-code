import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class Advent0501 {
    public static void main(String[] args) {
/*        String puzzle = "";*/

        String puzzle = "";

        String[] almanacParts = puzzle.split("\n\n");
        List<Long> seeds = getSeedList(almanacParts[0]);
        List<Almanac> almanacs = new ArrayList<>();
        for (int i = 1; i < almanacParts.length; i++) {
            almanacs.add(getAlmanac(almanacParts[i]));
        }
        Map<String, List<Almanac.Entry>> almanacEntriesByName = almanacs.stream().collect(Collectors.toMap(
                Almanac::getName,
                Almanac::getEntries));
        HashMap<Long, Long> seedsById = new HashMap<>();
        for (Long seed : seeds) {
            seedsById.put(seed, seed);
        }

        HashMap<Long, Long> soilBySeed = transformValuesFromSourceToDestination(seedsById, almanacEntriesByName.get("seed-to-soil"));
        HashMap<Long, Long> fertilizerBySeed = transformValuesFromSourceToDestination(soilBySeed, almanacEntriesByName.get("soil-to-fertilizer"));
        HashMap<Long, Long> waterBySeed = transformValuesFromSourceToDestination(fertilizerBySeed, almanacEntriesByName.get("fertilizer-to-water"));
        HashMap<Long, Long> lightBySeed = transformValuesFromSourceToDestination(waterBySeed, almanacEntriesByName.get("water-to-light"));
        HashMap<Long, Long> temperatureBySeed = transformValuesFromSourceToDestination(lightBySeed, almanacEntriesByName.get("light-to-temperature"));
        HashMap<Long, Long> humidityBySeed = transformValuesFromSourceToDestination(temperatureBySeed, almanacEntriesByName.get("temperature-to-humidity"));
        HashMap<Long, Long> locationBySeed = transformValuesFromSourceToDestination(humidityBySeed, almanacEntriesByName.get("humidity-to-location"));

        System.out.println(locationBySeed.entrySet());
        System.out.println(locationBySeed.values().stream().min(Long::compareTo));

    }

    static List<Long> getSeedList(String almanacPart) {
        String listString = almanacPart.replace("seeds: ", "");
        return Arrays.asList(listString.split(" ")).stream().map(Long::parseLong).toList();
    }

    static Almanac getAlmanac(String almanacPart) {
        String[] lines = almanacPart.split("\n");
        String name = lines[0].replace(" map:", "");
        List<Almanac.Entry> entries = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            String[] elements = lines[i].split(" ");
            Almanac.Entry entry = new Almanac.Entry(Long.parseLong(elements[0]), Long.parseLong(elements[1]), Long.parseLong(elements[2]));
            entries.add(entry);
        }
        return new Almanac(name, entries);
    }

    static HashMap<Long, Long> transformValuesFromSourceToDestination(HashMap<Long, Long> seedsBySource, List<Almanac.Entry> almanacEntries) {
        HashMap<Long, Long> result = new HashMap<>();
        HashMap<Long, Long> seeds = (HashMap<Long, Long>) seedsBySource.clone();
        for (Almanac.Entry almanacEntry : almanacEntries) {
            List<Map.Entry<Long, Long>> seedEntries = seeds.entrySet().stream().toList();
            for (Map.Entry<Long, Long> seedEntry : seedEntries) {
                if (seedEntry.getValue() >= almanacEntry.getSource() && seedEntry.getValue() <= almanacEntry.getSourceMax()) {
                    seedEntry.setValue(seedEntry.getValue() - almanacEntry.getDiff());
                    result.put(seedEntry.getKey(), seedEntry.getValue());
                    seeds.remove(seedEntry.getKey());
                }
            }
        }
        result.putAll(seeds);
        return result;
    }


    static class Almanac {
        String name;
        List<Entry> entries;

        public Almanac(String name, List<Entry> entries) {
            this.name = name;
            this.entries = entries;
        }

        public List<Entry> getEntries() {
            return entries;
        }

        public String getName() {
            return name;
        }

        static class Entry {
            long destination;
            long destinationMax;
            long source;
            long sourceMax;
            long range;
            long diff;

            public Entry(long destination, long source, long range) {
                this.destination = destination;
                this.destinationMax = destination + range - 1;
                this.source = source;
                this.sourceMax = source + range - 1;
                this.range = range;
                this.diff = source - destination;
            }

            public long getDestination() {
                return destination;
            }

            public long getDestinationMax() {
                return destinationMax;
            }

            public long getSource() {
                return source;
            }

            public long getSourceMax() {
                return sourceMax;
            }

            public long getRange() {
                return range;
            }

            public long getDiff() {
                return diff;
            }
        }
    }
}