import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

class Advent0701 {
    public static void main(String[] args) {
/*        String input = ""; */

        String input = "";

        String[] lines = input.split("\n");

        List<Hand> hands = mapHands(lines);
        System.out.println(hands);
        long score = hands.stream().map(hand -> hand.getBid() * hand.getRank()).reduce(0L, Long::sum);
        System.out.println(score);

    }

    static List<Hand> mapHands(String[] lines) {
        List<Hand> hands = new ArrayList<>();
        for (String line : lines) {
            Hand hand = new Hand();
            String[] parts = line.split(" ");
            hand.setCards(mapCards(parts[0]));
            hand.setBid(Long.parseLong(parts[1]));
            hand.setType(determinateType(hand.cards));
            hands.add(hand);
        }
        hands.sort(Hand::compareTo);
        for (int i = 0; i < hands.size(); i++) {
            hands.get(i).setRank(i + 1);
        }
        return hands;
    }

    static List<Card> mapCards(String string) {
        return Arrays.stream(string.split("")).map(Card::getBySymbol).toList();
    }

    static Type determinateType(List<Card> cards) {
        Map<Card, Integer> countByCard = new EnumMap<>(Card.class);
        for (Card card : cards) {
            countByCard.merge(card, 1, Integer::sum);
        }
        if (countByCard.containsValue(5)) {
            return Type.FIVE_OF_A_KIND;
        }
        if (countByCard.containsValue(4)) {
            return Type.FOUR_OF_A_KIND;
        }
        if (countByCard.containsValue(3) && countByCard.containsValue(2)) {
            return Type.FULL_HOUSE;
        }
        if (countByCard.containsValue(3)) {
            return Type.THREE_OF_A_KIND;
        }
        if (countByCard.entrySet().stream().filter(entry -> entry.getValue().equals(2)).count() == 2) {
            return Type.TWO_PAIR;
        }
        if (countByCard.entrySet().stream().filter(entry -> entry.getValue().equals(2)).count() == 1) {
            return Type.ONE_PAIR;
        }
        return Type.HIGH_CARD;
    }


    static class Hand {
        List<Card> cards;
        long bid;
        Type type;
        long rank;


        public int compareTo(Hand hand) {
            if (this.getType().ordinal() > hand.getType().ordinal()) {
                return 1;
            } else if (this.getType().ordinal() < hand.getType().ordinal()) {
                return -1;
            } else {
                for (int i = 0; i < this.getCards().size(); i++) {
                    if (this.getCards().get(i).ordinal() > hand.getCards().get(i).ordinal()) {
                        return 1;
                    }
                    if (this.getCards().get(i).ordinal() < hand.getCards().get(i).ordinal()) {
                        return -1;
                    }
                }
            }
            return 0;
        }

        public List<Card> getCards() {
            return cards;
        }

        public long getBid() {
            return bid;
        }

        public Type getType() {
            return type;
        }

        public long getRank() {
            return rank;
        }

        public void setCards(List<Card> cards) {
            this.cards = cards;
        }

        public void setBid(long bid) {
            this.bid = bid;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        @Override
        public String toString() {
            return "Hand{" +
                   "cards=" + cards +
                   ", bid=" + bid +
                   ", type=" + type +
                   ", rank=" + rank +
                   '}';
        }
    }

    enum Card {
        DEUX("2"),
        TROIS("3"),
        QUATRE("4"),
        CINQ("5"),
        SIX("6"),
        SEPT("7"),
        HUIT("8"),
        NEUF("9"),
        CAV("T"),
        JOKER("J"),
        REINE("Q"),
        ROI("K"),
        AS("A");
        final String symbol;

        Card(String symbol) {
            this.symbol = symbol;
        }
        static Card getBySymbol(String symbol) {
            return  Arrays.stream(Card.values()).filter(card -> symbol.equals(card.symbol)).findFirst().get();
        }

        @Override
        public String toString() {
            return symbol;
        }
    }

    enum Type {
        HIGH_CARD,
        ONE_PAIR,
        TWO_PAIR,
        THREE_OF_A_KIND,
        FULL_HOUSE,
        FOUR_OF_A_KIND,
        FIVE_OF_A_KIND
    }

}