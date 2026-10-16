package DesginPatterns.ObserverPattern.Observer;

import DesginPatterns.ObserverPattern.Subject.StockExchange;

import java.util.HashMap;
import java.util.Map;

public class TraderBot implements StockObserver {
    private final Map<String, Double> previousPrices = new HashMap<>();

    @Override
    public void onPriceUpdate(StockExchange exchange) {
        String symbol = exchange.getLastUpdatedSymbol();
        double currPrice = exchange.getPrice(symbol);
        double prevPrice = previousPrices.getOrDefault(symbol, currPrice);

        if(currPrice > prevPrice) {
            System.out.println("Bot -> " + symbol + " rising ($" + prevPrice +
                    " -> $" + currPrice + "). HOLD.");
        }
        else if (currPrice < prevPrice) {
            System.out.println("Bot -> " + symbol + " dropping ($" + prevPrice +
                    " -> $" + currPrice + "). BUY.");
        }

        previousPrices.put(symbol, currPrice);
    }
}
