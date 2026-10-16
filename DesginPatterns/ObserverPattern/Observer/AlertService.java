package DesginPatterns.ObserverPattern.Observer;

import DesginPatterns.ObserverPattern.Subject.StockExchange;

import java.util.HashMap;
import java.util.Map;

public class AlertService implements StockObserver {
    private final Map<String, Double> thresholds = new HashMap<>();
    public void setAlert(String symbol, double threshold) {
        thresholds.put(symbol, threshold);
    }
    @Override
    public void onPriceUpdate(StockExchange exchange) {
        String symbol = exchange.getLastUpdatedSymbol();
        if(thresholds.containsKey(symbol)) {
            double threshold = thresholds.get(symbol);
            double price = exchange.getPrice(symbol);
            if(price>=threshold) {
                System.out.println("ALERT -> " + symbol + " hit $" + price + " (threshold: $" + threshold + ")");
            }
        }
    }
}
