package ImplementProblems.LongestSubStringWithoutRepeatChars;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
//        Given a string s, find the length of the longest substring without duplicate characters.
//        Input: "abcabcbb" output: 3

        String input = "abcabcbb";
        System.out.println("O(N^2): " + lengthOfLongestSubstring(input));
        System.out.println("O(N): " + optimizedLengthOfLongestSubstring(input));
    }

//  O(N)
    public static int optimizedLengthOfLongestSubstring(String s) {
        int maxLen = 0;
        int left = 0;
        Map<Character, Integer> charIndexMap = new HashMap<>();

        for(int right=0; right<s.length(); right++) {
            char c = s.charAt(right);

            if(charIndexMap.containsKey(c)) {
//              Move the left pointer to just past the duplicate character... unless the left pointer is already past it.
                left = Math.max(left, charIndexMap.get(c) + 1);
            }
            charIndexMap.put(c, right);
            maxLen = Math.max(maxLen, right-left+1);
        }
        return maxLen;
    }

    //O(N^2) solution since I moved my pointer backward and recalculate
    public static int lengthOfLongestSubstring(String s) {
        int i=0;
        Map<Character, Integer> charIndexMap = new HashMap<>();
        int lastStartPos = 0;
        int maxLen = Integer.MIN_VALUE;

        while(i!=s.length()) {
            if(charIndexMap.containsKey(s.charAt(i))) {
                maxLen = Math.max(maxLen, i-lastStartPos);
                i = charIndexMap.get(s.charAt(i)) + 1;
                lastStartPos = i;
                charIndexMap = new HashMap<>();
            }
            else {
                charIndexMap.put(s.charAt(i), i);
                i++;
            }
        }
//      This line is very tricky and important, imagine you reach end of line while working on valid substring.
//      aabc -> lastStartPos would be 1 and i reaches 4 and exits from while loop without updating maxLen, so we should
//      reevaluate maxLen as Math.max(maxLen, i-lastStartPos)
        return Math.max(maxLen, i-lastStartPos);
    }
}
