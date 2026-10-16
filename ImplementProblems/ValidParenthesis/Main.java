package ImplementProblems.ValidParenthesis;

import ImplementProblems.Stack.Stack;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String input = "(())()";
        System.out.println(input + ":" + isValid(input));

        input = "[()]()";
        System.out.println(input + ":" + isValid(input));

        input = "[()]{}";
        System.out.println(input + ":" + isValid(input));

        input = "[(]){}";
        System.out.println(input + ":" + isValid(input));

        input = "[[({{";
        System.out.println(input + ":" + isValid(input));

        input = "]]}}";
        System.out.println(input + ":" + isValid(input));
    }

    private static boolean isValid(String input) {
        if(input == null || input.isEmpty()) {
            return true;
        }
        Map<Character, Character> complimentary = Map.of(
                ')', '(',
                '}', '{',
                ']', '['
        );
        Stack<Character> stack = new Stack<>();
        for(char ch : input.toCharArray()) {
            if(ch == '(' || ch == '[' || ch == '{') {
                stack.push(ch);
            }
            else if(ch == ')' || ch == ']' || ch == '}') {
                if(stack.isEmpty() || complimentary.get(ch) != stack.peek()) {
                    return false;
                }
                else {
                    stack.pop();
                }
            }
            else {
                System.out.println("UNSUPPORTED PARENTHESIS!");
            }
        }
        return stack.isEmpty();
    }
}
