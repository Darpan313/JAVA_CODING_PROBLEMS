package ImplementProblems.JavaStreams;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
    Collectors.groupingBy() groups elements based on a classification function.
    Collectors.counting() acts as the downstream collector to count occurrences.
 */
public class FrequencyOfChars {
    public static void main(String[] args) {
        String input = "interview";
        Map<String, Long> frequencyMap = Arrays.stream(input.split(""))
                .collect(
                        Collectors.groupingBy(
                                Function.identity(),
                                Collectors.counting()
                        )
                );
        System.out.println(frequencyMap);
    }
}
