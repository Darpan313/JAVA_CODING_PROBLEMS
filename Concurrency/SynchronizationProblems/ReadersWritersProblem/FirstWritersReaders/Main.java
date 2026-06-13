package Concurrency.SynchronizationProblems.ReadersWritersProblem.FirstWritersReaders;

public class Main {
    public static void main(String[] args) throws InterruptedException{
        WritersPreferenceRW rw = new WritersPreferenceRW();

        Thread r1 = new Thread(rw.new Readers(), "Reader-1");
        Thread r2 = new Thread(rw.new Readers(), "Reader-2");
        Thread r3 = new Thread(rw.new Readers(), "Reader-3 (Late Arrival)");
        Thread w1 = new Thread(rw.new Writers(), "Writer-1");

        r1.start(); // Reader 1 enters
        r2.start(); // Reader 2 enters

        Thread.sleep(100);

        w1.start(); // Writer 1 arrives. It locks the Turnstile but waits for R1 and R2 to finsh.

        Thread.sleep(100);

        r3.start(); // Reader 3 arrives. It gets BLOCKED at the Turnstile by Writer 1!
    }
}
