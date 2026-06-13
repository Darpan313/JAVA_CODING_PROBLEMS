package FunctionalProgramminDeepDive.DebuggingLambdas;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("anna", "bob", "christian", "carmen", "rick", "carla");

        System.out.println("After:");
        names.stream()
                .peek(p -> System.out.println("\tstream(): " + p))
                .filter(s -> s.startsWith("c"))
                .peek(p -> System.out.println("\tfilter(): " + p))
                .map(String::toUpperCase)
                .peek(p -> System.out.println("\tmap(): " + p))
                .sorted()
                .peek(p -> System.out.println("\tsorted(): " + p))
                .toList();

        System.out.println();

        List<String> namesNull = Arrays.asList("anna", "bob", null, "mary");

        namesNull.stream()
                .peek(p -> System.out.println("\tstream(): " + p))
                .map(s -> s.toUpperCase())
                .peek(p -> System.out.println("\tmap(): " + p))
                .toList();
    }
}
