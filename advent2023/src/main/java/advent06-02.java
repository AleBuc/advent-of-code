class Advent0602 {
    public static void main(String[] args) {
/*        String input = "";*/
        String input = "";

        String[] lines = input.split("\n");
        long time = Long.parseLong(lines[0].replaceAll("[^\\d.]", ""));
        long distance = Long.parseLong(lines[1].replaceAll("[^\\d.]", ""));

        Race personalBest = getPersonalBest(new Race(time, distance));
        System.out.println(personalBest);

    }

    static Race getPersonalBest(Race race) {

        long raceTime = race.time;
        long beatRecord = 0;
        for (long buttonTime = 0; buttonTime <= raceTime; buttonTime++) {
            long acceleration = buttonTime;
            long accelerationTime = raceTime - buttonTime;
            long distance = acceleration * accelerationTime;
            if (distance > race.distance) {
                beatRecord++;
            }
        }

        return new Race(raceTime, beatRecord);
    }

    static class Race {
        long time;
        long distance;

        @Override
        public String toString() {
            return "Race{" +
                   "time=" + time +
                   ", distance=" + distance +
                   '}';
        }

        public Race(long time, long distance) {
            this.time = time;
            this.distance = distance;
        }

        public long getTime() {
            return time;
        }

        public long getDistance() {
            return distance;
        }
    }
}