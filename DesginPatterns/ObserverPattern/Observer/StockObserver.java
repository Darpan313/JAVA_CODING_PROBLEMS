package DesginPatterns.ObserverPattern.Observer;

import DesginPatterns.ObserverPattern.Subject.StockExchange;

public interface StockObserver {
    void onPriceUpdate(StockExchange exchange);
}
