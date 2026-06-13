package ImplementProblems.MinimumWindowSubstring;

public class Main {
    /*
        Given two strings s and t of lengths m and n respectively, return the minimum window substring of s such that
        every character in t (including duplicates) is included in the window. If there is no such substring,
        return the empty string "".

        The testcases will be generated such that the answer is unique.

        Example 1:
        Input: s = "ADOBECODEBANC", t = "ABC"
        Output: "BANC"
        Explanation: The minimum window substring "BANC" includes 'A', 'B', and 'C' from string t.
     */
    public static void main(String[] args) {
        String s = "ADOBECODEBANC";
        String t = "ABC";

        System.out.println(minWindow(s,t));
    }

    private static String minWindow(String s, String t) {
        if(s==null || t == null || s.isEmpty() || t.isEmpty()) {
            return "";
        }

        int[] charCount = new int[128];
        for(char c : t.toCharArray()) {
            charCount[c]++;
        }

        int left = 0;
        int right = 0;
        int minLen = Integer.MAX_VALUE;
        int minStart = 0;
        int matchedCount = 0;

        while(right < s.length()) {
            char rightChar = s.charAt(right);
            if(charCount[rightChar] > 0) {
                matchedCount++;
            }

            charCount[rightChar]--;
            right++;

            while(matchedCount == t.length()) {
                if(right - left < minLen) {
                    minLen = right - left;
                    minStart = left;
                }

                char leftChar = s.charAt(left);
                charCount[leftChar]++;

                if(charCount[leftChar]>0) {
                    matchedCount--;
                }
                left++;
            }
        }
        return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart+minLen);
    }
}
