package OOP.VendingMachine.model;

import OOP.VendingMachine.enums.Coin;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class CoinBank {
    private final EnumMap<Coin, Integer> stock = new EnumMap<>(Coin.class);

    public CoinBank() {
        for(Coin c : Coin.values()) {
            stock.put(c, 0);
        }
    }
    public void add(Coin c, int n) {
        stock.merge(c, n, Integer::sum);
    }
    public void add(Coin c) {
        add(c, 1);
    }

    public Optional<Map<Coin, Integer>> planChange(int cents) {
        Map<Coin, Integer> plan = new EnumMap<>(Coin.class);
        Coin[] desc = {Coin.DOLLAR, Coin.QUARTER, Coin.DIME, Coin.NICKEL};
        for(Coin c : desc) {
            int use = Math.min(cents/c.cents, stock.get(c));
            if(use>0) {
                plan.put(c, use);
                cents -= (use*c.cents);
            }
        }

        return cents==0 ? Optional.of(plan) : Optional.empty();
    }

    public void remove(Map<Coin, Integer> plan) {
        plan.forEach((c, n) -> stock.merge(c, -n, Integer::sum));
    }
}
