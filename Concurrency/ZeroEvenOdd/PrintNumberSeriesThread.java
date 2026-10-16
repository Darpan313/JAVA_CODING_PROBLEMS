package Concurrency.ZeroEvenOdd;

public class PrintNumberSeriesThread extends Thread {
    PrintNumberSeries zeo;
    String method;

    PrintNumberSeriesThread(PrintNumberSeries zeo, String method) {
        this.zeo = zeo;
        this.method = method;
    }

    public void run() {
        if("zero".equals(method)) {
            zeo.printZero();
        } else if("even".equals(method)) {
            zeo.printEven();
        } else if("odd".equals(method)) {
            zeo.printOdd();
        }
    }
}
