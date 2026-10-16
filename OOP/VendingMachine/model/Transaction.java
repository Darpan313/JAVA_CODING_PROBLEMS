package OOP.VendingMachine.model;

import OOP.VendingMachine.enums.Coin;

import java.util.Map;

public record Transaction(String slotCode, int priceCents, int paidCents, Map<Coin, Integer> change) {
}
