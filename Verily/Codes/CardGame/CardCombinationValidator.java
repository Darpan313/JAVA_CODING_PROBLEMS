package Verily.Codes.CardGame;

import java.util.*;

public class CardCombinationValidator {

    /*
        Description:
        You are building the logic for a card game. A card has a Suit (Hearts, Diamonds, Clubs, Spades) and a Value (1-13).
        A combination of 4 cards is considered valid if it satisfies either of the following two rules:

        // StraightFlush
        Four cards of the same suit with consecutive numbers.

        // FourOfAKind
        Four cards of the same number but all four different suits.

        Task A: Write a method boolean isValid(List<Card> hand) to determine if a given 4-card hand is valid.

        Task B (Follow-up): Write a method List<List<Card>> getValidSubsets(List<Card> pool) that takes a larger pool
        of cards (e.g., 7 or more) and returns all possible valid 4-card subsets.
     */
    public record Card(Suit suit, int code) {}
    public static void main(String[] args) {
        List<Card> hand = List.of(new Card(Suit.SPADES, 3), new Card(Suit.SPADES, 4),
        new Card(Suit.SPADES, 5), new Card(Suit.SPADES, 6));
        System.out.println(isValid(hand));

        List<Card> hand2 = List.of(new Card(Suit.CLUBS, 3), new Card(Suit.SPADES, 4),
                new Card(Suit.SPADES, 5), new Card(Suit.SPADES, 6));
        System.out.println(isValid(hand2));

        List<Card> hand3 = List.of(new Card(Suit.SPADES, 3), new Card(Suit.SPADES, 4),
                new Card(Suit.SPADES, 5), new Card(Suit.SPADES, 8));
        System.out.println(isValid(hand3));

        List<Card> hand4 = List.of(new Card(Suit.SPADES, 3), new Card(Suit.CLUBS, 3),
                new Card(Suit.HEARTS, 3), new Card(Suit.DIAMONDS, 3));
        System.out.println(isValid(hand4));
    }

    static boolean isValid(List<Card> hand) {
        if (hand == null || hand.size() != 4) {
            return false;
        }

        return isFourOfAKind(hand) || isStraightFlush(hand);
    }

    private static boolean isFourOfAKind(List<Card> hand) {
        int firstValue = hand.get(0).code;
        Set<Suit> uniqueSuits = new HashSet<>();

        for(Card card : hand) {
            if(card.code != firstValue) {
                return false;
            }
            uniqueSuits.add(card.suit);
        }
        return uniqueSuits.size() == 4;
    }

    private static boolean isStraightFlush(List<Card> hand) {
        Suit firstSuit = hand.get(0).suit;
        List<Integer> values = new ArrayList<>();
        for(Card card : hand) {
            if(card.suit != firstSuit) {
                return false;
            }
            values.add(card.code);
        }

        Collections.sort(values);
        for(int i=1; i<values.size(); i++) {
            if(values.get(i) != values.get(i-1)+1) {
                return false;
            }
        }
        return true;
    }

    List<List<Card>> getValidSubSets(List<Card> pool) {
        return null;
    }
}
