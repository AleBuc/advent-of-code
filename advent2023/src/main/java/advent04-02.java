import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

class Advent0402 {
    public static void main(String[] args) {
/*        String code = "";*/
        String code = "";

        String[] lines = code.split("\n");
        Comparator<Card> cardComparator = Comparator.comparing(card -> Integer.parseInt(card.id));
        Set<Card> cards = new TreeSet<>(cardComparator);
        for (String line : lines) {
            cards.add(mapCard(line));
        }

        for (int i = 0; i < cards.size(); i++) {
            List<Card> cardArray = cards.stream().sorted(cardComparator).toList();
            Card card = cardArray.get(i);
            int idAsInt = Integer.parseInt(card.id);
            for (int j = 0; j < card.copiesCount; j++) {
                if (card.matchingNumbersCount > 0) {
                    List<Card> cardsToAdd = cards.stream()
                            .filter(card1 -> (idAsInt + 1) <= Integer.parseInt(card1.id) && Integer.parseInt(card1.id) <= (idAsInt + card.matchingNumbersCount))
                            .map(selectedCard -> {
                                selectedCard.setCopiesCount(selectedCard.copiesCount + 1);
                                return selectedCard;
                            })
                            .toList();
                    cards.addAll(cardsToAdd);
                }
            }

        }
        Map<String, Integer> cardsCountById = cards.stream()
                .collect(Collectors.toMap(
                        card -> card.id,
                        card -> card.copiesCount
                ));
        System.out.println(cardsCountById.entrySet());
        Integer sum = cardsCountById.values().stream().reduce(0, Integer::sum);
        System.out.println(sum);


    }

    private static Card mapCard(String line) {
        String[] parts = line.split("[:|]");
        String id = parts[0].replaceAll("[^\\d.]", "");
        List<Integer> winningNumbers = Arrays.stream(parts[1].split(" ")).filter(StringUtils::isNotBlank).map(Integer::parseInt).toList();
        List<Integer> playedNumbers = Arrays.stream(parts[2].split(" ")).filter(StringUtils::isNotBlank).map(Integer::parseInt).toList();
        int matchingNumbersCount = 0;
        for (int winningNumber : winningNumbers) {
            if (playedNumbers.contains(winningNumber)) {
                matchingNumbersCount++;
            }
        }
        return new Card(id, winningNumbers, playedNumbers, matchingNumbersCount);
    }

    private static class Card {
        String id;
        List<Integer> winningNumbers;
        List<Integer> playedNumbers;
        int matchingNumbersCount;
        int copiesCount = 1;

        public void setCopiesCount(int copiesCount) {
            this.copiesCount = copiesCount;
        }

        public Card(String id, List<Integer> winningNumbers, List<Integer> playedNumbers, int matchingNumbersCount) {
            this.id = id;
            this.winningNumbers = winningNumbers;
            this.playedNumbers = playedNumbers;
            this.matchingNumbersCount = matchingNumbersCount;
        }
    }
}