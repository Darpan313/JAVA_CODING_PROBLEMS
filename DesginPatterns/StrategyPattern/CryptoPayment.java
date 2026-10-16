package DesginPatterns.StrategyPattern;

public class CryptoPayment implements PaymentStrategy {
    private final String walletId;

    public CryptoPayment(String walletId) {
        this.walletId = walletId;
    }


    @Override
    public boolean pay(double amount) {
        System.out.println("Transferring $" + amount + " in crypto to " + walletId);
        return true;
    }
}
