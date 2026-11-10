package ImplementProblems.JavaStreams;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/*
    Chaining intermediate operations (distinct, sorted, skip) to filter and position the data before short-circuiting
    terminal operation(findFirst).
 */
public class SecondHighest {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(5, 9, 11, 2, 8, 21, 1 ,21);
        Integer res = numbers.stream()
                .distinct()
                .sorted(Comparator.reverseOrder())
                .skip(1)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("List does not have enough elements."));
        System.out.println(res);
    }
}
