import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class Advent0502b {
    public static void main(String[] args) {
/*        String puzzle = "";*/

        String puzzle = "";

        String[] almanacParts = puzzle.split("\n\n");
        List<SeedBag> seeds = getSeedList(almanacParts[0]);
        List<Almanac> almanacs = new ArrayList<>();
        for (int i = 1; i < almanacParts.length; i++) {
            almanacs.add(getAlmanac(almanacParts[i]));
        }
        Map<String, List<Almanac.Entry>> almanacEntriesByName = almanacs.stream().collect(Collectors.toMap(
                Almanac::getName,
                Almanac::getEntries));
        HashMap<SeedBag, SeedBag> seedsById = new HashMap<>();
        for (SeedBag seed : seeds) {
            seedsById.put(seed, seed);
        }

        HashMap<SeedBag, SeedBag> soilBySeed = transformValuesFromSourceToDestination(seedsById, almanacEntriesByName.get("seed-to-soil"));
        HashMap<SeedBag, SeedBag> fertilizerBySeed = transformValuesFromSourceToDestination(soilBySeed, almanacEntriesByName.get("soil-to-fertilizer"));
        HashMap<SeedBag, SeedBag> waterBySeed = transformValuesFromSourceToDestination(fertilizerBySeed, almanacEntriesByName.get("fertilizer-to-water"));
        HashMap<SeedBag, SeedBag> lightBySeed = transformValuesFromSourceToDestination(waterBySeed, almanacEntriesByName.get("water-to-light"));
        HashMap<SeedBag, SeedBag> temperatureBySeed = transformValuesFromSourceToDestination(lightBySeed, almanacEntriesByName.get("light-to-temperature"));
        HashMap<SeedBag, SeedBag> humidityBySeed = transformValuesFromSourceToDestination(temperatureBySeed, almanacEntriesByName.get("temperature-to-humidity"));
        HashMap<SeedBag, SeedBag> locationBySeed = transformValuesFromSourceToDestination(humidityBySeed, almanacEntriesByName.get("humidity-to-location"));

        System.out.println(locationBySeed.entrySet());
        System.out.println(locationBySeed.values().stream().min(Comparator.comparingLong(o -> o.min)));

    }

    static List<SeedBag> getSeedList(String almanacPart) {
        String listString = almanacPart.replace("seeds: ", "");
        List<Long> values = Arrays.stream(listString.split(" ")).map(Long::parseLong).toList();
        List<SeedBag> seedBags = new ArrayList<>();
        for (int i = 0; i < values.size(); i = i + 2) {
            SeedBag seedBag = new SeedBag(values.get(i), values.get(i) + values.get(i + 1) - 1);
            seedBags.add(seedBag);
        }
        return seedBags;
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

    static HashMap<SeedBag, SeedBag> transformValuesFromSourceToDestination(HashMap<SeedBag, SeedBag> seedsBySource, List<Almanac.Entry> almanacEntries) {
        HashMap<SeedBag, SeedBag> result = new HashMap<>();
        List<HashMap<SeedBag, SeedBag>> toProcessList = new ArrayList<>(List.of(seedsBySource));
        int lastEntryIndex = 0;
        for (int entryIndex = 0; entryIndex < almanacEntries.size(); entryIndex++) {
            lastEntryIndex = entryIndex;
            if (toProcessList.size() > entryIndex) {
                Almanac.Entry almanacEntry = almanacEntries.get(entryIndex);
                HashMap<SeedBag, SeedBag> toReProcess = new HashMap<>();
                for (Map.Entry<SeedBag, SeedBag> seedEntry : toProcessList.get(entryIndex).entrySet()) {
                    if (seedEntry.getValue().min >= almanacEntry.getSource() && seedEntry.getValue().max <= almanacEntry.getSourceMax()) {
                        SeedBag newBag = new SeedBag(seedEntry.getValue().min - almanacEntry.getDiff(), seedEntry.getValue().max - almanacEntry.getDiff());
                        seedEntry.setValue(newBag);
                        result.put(seedEntry.getKey(), seedEntry.getValue());
                    } else if (seedEntry.getValue().min < almanacEntry.getSource() && seedEntry.getValue().max >= almanacEntry.getSource() && seedEntry.getValue().max <= almanacEntry.getSourceMax()) {
                        long diff1 = almanacEntry.getSource() - seedEntry.getValue().min - 1;
                        SeedBag seedBag1Value = new SeedBag(seedEntry.getValue().min, almanacEntry.getSource() - 1);
                        SeedBag seedBag1Key = new SeedBag(seedEntry.getKey().min, seedEntry.getKey().min + diff1);
                        toReProcess.put(seedBag1Key, seedBag1Value);

                        long diff2 = seedEntry.getValue().max - almanacEntry.getSource();
                        SeedBag seedBag2Value = new SeedBag(seedEntry.getValue().max - diff2 - almanacEntry.diff, seedEntry.getValue().max - almanacEntry.diff);
                        SeedBag seedBag2Key = new SeedBag(seedEntry.getKey().max - diff2 - almanacEntry.diff, seedEntry.getKey().max - almanacEntry.diff);
                        result.put(seedBag2Key, seedBag2Value);
                    } else if (seedEntry.getValue().min >= almanacEntry.getSource() && seedEntry.getValue().min <= almanacEntry.getSourceMax() && seedEntry.getValue().max > almanacEntry.getSourceMax()) {
                        long diff1 = almanacEntry.getSourceMax() - seedEntry.getValue().min;
                        SeedBag seedBag1Value = new SeedBag(seedEntry.getValue().min - almanacEntry.diff, almanacEntry.getSourceMax() - almanacEntry.diff);
                        SeedBag seedBag1Key = new SeedBag(seedEntry.getKey().min, seedEntry.getKey().min + diff1);
                        result.put(seedBag1Key, seedBag1Value);

                        long diff2 = seedEntry.getValue().max - almanacEntry.getSourceMax() - 1;
                        SeedBag seedBag2Value = new SeedBag(seedEntry.getValue().max - diff2, seedEntry.getValue().max);
                        SeedBag seedBag2Key = new SeedBag(seedEntry.getKey().max - diff2, seedEntry.getKey().max);
                        toReProcess.put(seedBag2Key, seedBag2Value);

                    } else if (seedEntry.getValue().min < almanacEntry.getSource() && seedEntry.getValue().max > almanacEntry.getSourceMax()) {
                        long diff1 = almanacEntry.getSource() - seedEntry.getValue().min - 1;
                        SeedBag seedBag1Value = new SeedBag(seedEntry.getValue().min, seedEntry.getValue().min + diff1);
                        SeedBag seedBag1Key = new SeedBag(seedEntry.getKey().min, seedEntry.getKey().min + diff1);
                        toReProcess.put(seedBag1Key, seedBag1Value);

                        long diff2 = seedEntry.getValue().max - almanacEntry.getSourceMax() - 1;
                        SeedBag seedBag2Value = new SeedBag(seedEntry.getValue().max - diff2, seedEntry.getValue().max);
                        SeedBag seedBag2Key = new SeedBag(seedEntry.getKey().max - diff2, seedEntry.getKey().max);
                        toReProcess.put(seedBag2Key, seedBag2Value);

                        SeedBag seedBag3Value = new SeedBag(seedEntry.getValue().min + diff1 + 1 - almanacEntry.diff, seedEntry.getValue().max - diff2 - 1 - almanacEntry.diff);
                        SeedBag seedBag3Key = new SeedBag(seedEntry.getKey().min + diff1 + 1, seedEntry.getKey().max - diff2 - 1);
                        result.put(seedBag3Key, seedBag3Value);
                    } else toReProcess.put(seedEntry.getKey(), seedEntry.getValue());
                }
                toProcessList.add(toReProcess);
            }

        }
        if (!toProcessList.get(lastEntryIndex + 1).isEmpty()) {
            result.putAll(toProcessList.get(lastEntryIndex + 1));
        }

        return result;
    }

    static class SeedBag {
        long min;
        long max;

        public SeedBag(long seedMin, long seedMax) {
            this.min = seedMin;
            this.max = seedMax;
        }

        public long getSeedMin() {
            return min;
        }

        public long getSeedMax() {
            return max;
        }

        @Override
        public String toString() {
            return "SeedBag{" +
                   "min=" + min +
                   ", max=" + max +
                   '}';
        }
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