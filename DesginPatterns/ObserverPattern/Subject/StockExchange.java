package DesginPatterns.ObserverPattern.Subject;

import DesginPatterns.ObserverPattern.Observer.StockObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockExchange {
    private final Map<String, Double> prices = new HashMap<>();
    private final List<StockObserver> observers = new ArrayList<>();
    private String lastUpdatedSymbol;

    public void registerObserver(StockObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(StockObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for(StockObserver observer : new ArrayList<>(observers)) {
            observer.onPriceUpdate(this);
        }
    }

    public double getPrice(String symbol) {
        return prices.getOrDefault(symbol, 0.0);
    }

    public String getLastUpdatedSymbol() {
        return lastUpdatedSymbol;
    }

    public void updatePrice(String symbol, double price) {
        prices.put(symbol, price);
        lastUpdatedSymbol = symbol;
        System.out.println("\nExchange: " + symbol + " updated to $" + price);
        notifyObservers();
    }
}
