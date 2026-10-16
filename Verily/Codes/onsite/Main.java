package Verily.Codes.onsite;

import java.util.Arrays;

public class Main {
    public record Result(boolean isFit, int wordLength, int charCount){}
    public static void main(String[] args) {
        String input = "Tests areee got ";
        int height = 2;
        int width = 9;
        Result res = solve(input, height, width);
        System.out.println(res.isFit + ", " + res.wordLength +", "+ res.charCount);
        System.out.println("+++++++++++++++++++++++++++++");
        input = "Tests ar got tests ";
        height = 2;
        width = 9;
        res = solve(input, height, width);
        System.out.println(res.isFit + ", " + res.wordLength +", "+ res.charCount);
        System.out.println("+++++++++++++++++++++++++++++");
        input = "Tests ar got testsss";
        height = 2;
        width = 9;
        res = solve(input, height, width);
        System.out.println(res.isFit + ", " + res.wordLength +", "+ res.charCount);
    }

    private static Result solve2(String input, int height, int width) {
        int start = 0;
        int length = input.length();
        for (int i = 0; i < height; i++) {
            start += width;
            if (input.charAt(start%length) == ' ') {
                start++;
            } else {
                while (start > 0 && input.charAt((start - 1)%length) != ' ') {
                    start--;
                }
            }
        }
        String[] words = input.split("\\s");
        int charCount = 0;
        for(String word : words) {
            charCount += word.length();
        }
        if(start == input.length()) {
            return new Result(true, input.split("\\s").length, charCount);
        }
        return new Result(false, -1, -1);
    }

    private static Result solve(String input, int height, int width) {
        char[][] grid = new char[height][width];

        // FIX 1: Fill the grid with actual space characters first
        for (int i = 0; i < height; i++) {
            Arrays.fill(grid[i], ' ');
        }

        int charCount = 0;
        // Use "\\s+" just in case there are multiple spaces between words in the input
        String[] words = input.trim().split("\\s+");
        int currRow = 0, currCol = 0;

        for (String s : words) {
            // FIX 2: If a single word is larger than the entire width, it will never fit
            if (s.length() > width) {
                return new Result(false, -1, -1);
            }

            // FIX 3: Check if it fits on the current row. If not, drop to the next row.
            if (currCol + s.length() > width) {
                currRow++;
                currCol = 0;
            }

            // FIX 4: Check if we have run out of grid height BEFORE placing the word
            if (currRow >= height) {
                return new Result(false, -1, -1);
            }

            // Now we are 100% sure the word fits at grid[currRow][currCol]
            for (int i = 0; i < s.length(); i++) {
                grid[currRow][currCol] = s.charAt(i);
                currCol++;
            }

            charCount += s.length();

            // Handle the space after the word
            if (currCol == width) {
                // Hit the right edge perfectly, wrap to next line
                currCol = 0;
                currRow++;
            } else {
                // Leave a space for the next word
                currCol++;
            }
        }

        // Print the grid
        for(int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(grid[i][j]);
            }
            System.out.println();
        }

        return new Result(true, words.length, charCount);
    }
}
