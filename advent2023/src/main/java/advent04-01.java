import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class Advent0401 {
    public static void main(String[] args) {
//        String code = "";
        String code = "";

        String[] lines = code.split("\n");
        List<Card> cards = new ArrayList<>();
        for (String line : lines) {
            cards.add(mapCard(line));
        }
        Map<String, List<Integer>> matchedNumbersPerCardId = new HashMap<>();

        for (Card card : cards) {
            List<Integer> matchedNumbers = new ArrayList<>();
            for (Integer winningNumber : card.winningNumbers) {
                if (card.playedNumbers.contains(winningNumber)) {
                    matchedNumbers.add(winningNumber);
                }
            }
            matchedNumbersPerCardId.put(card.id, matchedNumbers);
        }
        System.out.println(matchedNumbersPerCardId);
        Map<String, Integer> scorePerCard = matchedNumbersPerCardId.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        stringListEntry -> {
                            List<Integer> numbers = stringListEntry.getValue();
                            if (numbers != null && !numbers.isEmpty()) {
                                int result = 1;
                                if (numbers.size() > 1) {
                                    for (int i = 1; i <= numbers.size() - 1; i++) {
                                        result = result * 2;
                                    }
                                }
                                return result;
                            }
                            return 0;
                        }));
        System.out.println(scorePerCard.entrySet());
        Integer sum = scorePerCard.values().stream().reduce(0, Integer::sum);
        System.out.println(sum);
    }

    private static Card mapCard(String line) {
        String[] parts = line.split("[:|]");
        String id = parts[0].replace("[^\\d.]", "");
        List<Integer> winningNumbers = Arrays.stream(parts[1].split(" ")).filter(StringUtils::isNotBlank).map(Integer::parseInt).toList();
        List<Integer> playedNumbers = Arrays.stream(parts[2].split(" ")).filter(StringUtils::isNotBlank).map(Integer::parseInt).toList();
        return new Card(id, winningNumbers, playedNumbers);
    }

    private static class Card {
        String id;

        public Card(String id, List<Integer> winningNumbers, List<Integer> playedNumbers) {
            this.id = id;
            this.winningNumbers = winningNumbers;
            this.playedNumbers = playedNumbers;
        }

        List<Integer> winningNumbers;
        List<Integer> playedNumbers;
    }
}