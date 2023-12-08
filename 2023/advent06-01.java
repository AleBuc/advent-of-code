import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Scratch {
    public static void main(String[] args) {
        /*String input = """
                Time:      7  15   30
                Distance:  9  40  200""";*/
        String input = """
                Time:        40     82     91     66
                Distance:   277   1338   1349   1063""";
        
        String[] lines = input.split("\n");
        List<Integer> times = Arrays.stream(lines[0].split(" ")).filter(StringUtils::isNumeric).map(Integer::parseInt).toList();
        List<Integer> distances = Arrays.stream(lines[1].split(" ")).filter(StringUtils::isNumeric).map(Integer::parseInt).toList();
        List<Race> races = new ArrayList<>();
        for (int i = 0; i <times.size(); i++) {
            races.add(new Race(times.get(i), distances.get(i)));
        }


        List<Race> personalBests = getPersonalBest(races);
        System.out.println(personalBests);
        int score = personalBests.stream().map(Race::getDistance).reduce(1, (integer, integer2) -> integer * integer2);
        System.out.println(score);

    }

    static List<Race> getPersonalBest(List<Race> races){
        List<Race> personalBests = new ArrayList<>();

        for (Race race : races){
            int raceTime = race.time;
            int beatRecord=0;
            for (int buttonTime = 0; buttonTime<=raceTime; buttonTime++){
                int acceleration = buttonTime;
                int accelerationTime = raceTime-buttonTime;
                int distance = acceleration * accelerationTime;
                if (distance > race.distance){
                    beatRecord++;
                }
            }
            personalBests.add(new Race(raceTime, beatRecord));
        }
        return personalBests;
    }
    static class Race {
        int time;
        int distance;

        @Override
        public String toString() {
            return "Race{" +
                   "time=" + time +
                   ", distance=" + distance +
                   '}';
        }

        public Race(int time, int distance) {
            this.time = time;
            this.distance = distance;
        }

        public int getTime() {
            return time;
        }

        public int getDistance() {
            return distance;
        }
    }
}