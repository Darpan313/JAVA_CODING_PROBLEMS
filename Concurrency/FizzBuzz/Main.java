package Concurrency.FizzBuzz;

public class Main {
    public static void main(String[] args) {
        int n = 15;
        FizzBuzzMultiThreaded fizzBuzz = new FizzBuzzMultiThreaded(n);

        // t1
        Thread t1 = new Thread(() -> {
           try {
               fizzBuzz.fizz(() -> System.out.print("fizz, "));
           } catch (InterruptedException e) {
               Thread.currentThread().interrupt();
           }
        });

        // t2
        Thread t2 = new Thread(() -> {
            try {
                fizzBuzz.buzz(() -> System.out.print("buzz, "));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // t3
        Thread t3 = new Thread(() -> {
            try {
                fizzBuzz.fizzbuzz(() -> System.out.print("fizzbuzz, "));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // t4
        Thread t4 = new Thread(() -> {
            try {
                fizzBuzz.number(value -> System.out.print(value + ", "));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        t1.start();
        t2.start();
        t3.start();
        t4.start();
    }
}
