package Concurrency.ZeroEvenOdd;

public class Main {
    public static void main(String[] args) {
        PrintNumberSeries zeo = new PrintNumberSeries(10);
        Thread t1 = new PrintNumberSeriesThread(zeo, "zero");
        Thread t2 = new PrintNumberSeriesThread(zeo, "odd");
        Thread t3 = new PrintNumberSeriesThread(zeo, "even");

        t1.start();
        t2.start();
        t3.start();
    }
}
