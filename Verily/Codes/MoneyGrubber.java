package Verily.Codes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
    This is UVa problem 10980.

    The Problem
    You want to buy apples, so you go to an apple store. The store has a price for buying one apple—for example, $1.75.
    The store also has m pricing schemes, where each pricing scheme gives a number n and a price p for buying n apples.
    For example, one pricing scheme might state that three apples cost a total of $4.00; another might state that two
    apples cost a total of $2.50. You want to buy at least k apples and do so as cheaply as possible.

    Input
    We read test cases until there’s no more input. Each test case consists of the following lines:

    A line containing the price for buying one apple, followed by the number m of pricing schemes for this test case.
    m is at most 20.
    m lines, each of which gives a number n and total price p for buying n apples. n is between 1 and 100.
    A line containing integers, where each integer k is between 0 and 100 and gives the desired number of apples to buy.
    Each price in the input is a floating-point number with exactly two decimal digits.
 */
public class MoneyGrubber {
    public static void main(String[] args) {
        Map<Integer, Double> priceMap = new HashMap<>();
        priceMap.put(1, 2.00);
        priceMap.put(3, 5.00);
        priceMap.put(4, 6.5);

        List<Integer> quantityList = List.of(7);
        for(int k : quantityList) {
            // output should be 11.50 but it gives 13.0
            System.out.println(solve(priceMap, k));
        }
    }

    /*
        This code fails when we can pick from multiple schemes
     */
    static double solve(Map<Integer, Double> priceMap, int k) {
        double minPrice = Double.MAX_VALUE;
        for(Map.Entry<Integer, Double> entry : priceMap.entrySet()) {
            double price = Math.ceilDiv(k, entry.getKey()) * entry.getValue();
            if(price<minPrice) {
                minPrice = price;
            }
        }
        return minPrice;
    }
}
