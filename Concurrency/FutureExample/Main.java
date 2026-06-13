package Concurrency.FutureExample;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    /*
        LIMITATIONS OF FUTURE:
        While Future was a big step up from raw threads, it has severe limitations for modern, reactive programming
            - get() is Blocking: You cannot tell a Future, "When you are done, run this next piece of code". You have to
              call get(), which halts your current thread until the result is ready
            - No Chaining: You cannot easily chain multiple asynchronous operations together(e.g, fetch userId, then fetch
              their profile)
            - Cannot Manually Complete: If an external event happens, you cannot manaully mark a Future as complete
            - Clunky Combination: There is no clean way to say "wait for all 5 of these Future's to finish" or "return the
              result of the first Future to finish.
            - No Pipeline Exception Handling: You have to warp .get() in a try/catch block.
     */
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Submit a background task
        Future<String> future = executor.submit(() -> {
            Thread.sleep(2000);
            return " Data fetched successfully";
        });

        System.out.println("Doing other work while waiting.");

        // Block and wait for the result
        String result = future.get();
        System.out.println(result);

        executor.shutdown();
    }
}
