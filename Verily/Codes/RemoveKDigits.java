package Verily.Codes;
/*
    Question:
    Given a sequence S of N digits, find a subsequence of K digits such that the number formed by these K digits
    (in order) is the largest.

    Examples given:
    S = "1", K = 1, answer = "1" # Single digit
    S = "10", K = 1, answer = "1" # Zero included
    S = "0000", K = 2, answer = "00" # All zeros
    S = "1000", K = 3, answer = "100" # Leading zeros
    More examples:
    S = "98765", K = 2, answer = "98"
    S = "98765", K = 3, answer = "987"
    S = "98765", K = 4, answer = "9876"
    S = "19191", K = 2, answer = "91"
    S = "19191", K = 3, answer = "991"
    S = "19191", K = 4, answer = "9191"
 */
public class RemoveKDigits {
    public static void main(String[] args) {
        String input = "2391467248";
        int k = 5;
        System.out.println("O(N^2): " + getMaxVal(input, k));
        System.out.println("O(N): " + getMaxValOptimized(input, k));

        input = "19191";
        k = 3;
        System.out.println("O(N^2): " + getMaxVal(input, k));
        System.out.println("O(N): " + getMaxValOptimized(input, k));

        input = "19191";
        k = 5;
        System.out.println("O(N^2): " + getMaxVal(input, k));
        System.out.println("O(N): " + getMaxValOptimized(input, k));
    }

    static String getMaxValOptimized(String input, int K) {
        int n = input.length();
        int drops = n-K;
        StringBuilder stack = new StringBuilder();

        for(int i=0; i<n; i++) {
            char currDigit = input.charAt(i);

            while(!stack.isEmpty() && drops>0 && currDigit > stack.charAt(stack.length()-1)) {
                stack.deleteCharAt(stack.length()-1);
                drops--;
            }
            stack.append(currDigit);
        }
        return stack.substring(0, K);
    }

    public static String getMaxVal(String input, int K) {
        StringBuilder sb = new StringBuilder();
        int n = input.length();
        int start = -1;
        for(int i=K; i>0; i--) {
            start = maxIndexIn(start+1, n-i, input);
            sb.append(input.charAt(start));
        }
        return sb.toString();
    }

    static int maxIndexIn(int start, int end, String input) {
        int max = Integer.MIN_VALUE;
        int maxIndex = 0;
        for(int i=start; i<=end; i++) {
            int currValue = input.charAt(i)-'0';
            if(currValue>max) {
                max = currValue;
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}
