package Concurrency.FizzBuzz;

import java.util.function.IntConsumer;

public class FizzBuzzMultiThreaded {
    private final int n;
    private int current = 1;

    public FizzBuzzMultiThreaded(int n) {
        this.n = n;
    }

    public synchronized void fizz(Runnable printFizz) throws InterruptedException {
        while(current <= n) {
            if(current % 3 == 0 && current % 5 != 0) {
                printFizz.run();
                current++;
                notifyAll();    // wake up other waiting threads
            }
            else {
                wait();
            }
        }
    }

    public synchronized void buzz(Runnable printBuzz) throws InterruptedException {
        while(current <= n) {
            if(current % 5 == 0 && current % 3 != 0) {
                printBuzz.run();
                current++;
                notifyAll();    // wake up other waiting threads
            }
            else {
                wait();
            }
        }
    }

    public synchronized void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
        while(current <= n) {
            if(current % 15 == 0) {
                printFizzBuzz.run();
                current++;
                notifyAll();    // wake up other waiting threads
            }
            else {
                wait();
            }
        }
    }

    public synchronized void number(IntConsumer printNumber) throws InterruptedException {
        while (current <= n) {
            if (current % 3 != 0 && current % 5 != 0) {
                printNumber.accept(current);
                current++;
                notifyAll();
            } else {
                wait();
            }
        }
    }
}
