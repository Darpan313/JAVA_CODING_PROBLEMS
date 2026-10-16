package DesginPatterns.ObserverPattern.Observer;

import DesginPatterns.ObserverPattern.Subject.StockExchange;

public class PriceDisplay implements StockObserver {
    @Override
    public void onPriceUpdate(StockExchange exchange) {
        String symbol = exchange.getLastUpdatedSymbol();
        System.out.println("Display -> " + symbol + ": $" + exchange.getPrice(symbol));
    }
}
