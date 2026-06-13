package FunctionalProgramminDeepDive.FilterNonZeroElements;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Numbers {
    public static void main(String[] args) {
        List<Integer> ints = Arrays.asList(1,2,-4,0,2,0,-1,14,0,-1);

        List<Integer> result1 = ints.stream()
                .filter(i -> i != 0)
                .toList();
        System.out.println(result1);

        // Filter zeros, filter dups, skip 1 value, truncate the remaining stream to two elements, and sort them by natural order
        List<Integer> result2 = ints.stream()
                .filter(i -> i != 0)
                .distinct()
                .skip(1)
                .limit(2)
                .sorted()
                .toList();
        System.out.println(result2);

        // When filter operation needs a complex/compound or long condition. then it is advisable to extract it in ancillary static method
        // and rely on method references.
        List<Integer> result3 = ints.stream()
                .filter(Numbers::evenBetween0And10)
                .toList();
        System.out.println(result3);
    }

    private static boolean evenBetween0And10(int value) {
        return value>0 && value<10 && value%2==0;
    }
}
