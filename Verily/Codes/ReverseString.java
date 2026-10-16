package Verily.Codes;

public class ReverseString {
    static StringBuilder sb = new StringBuilder();
    public static void main(String[] args) {
        String name = "ABCDEF";
        reverse(name);
        System.out.println(sb.toString());
    }

    static void reverse(String name) {
        if(name.isEmpty()) {
            return;
        }
        System.out.println(name);
        sb.append(name.charAt(name.length()-1));
        reverse(name.substring(0, name.length()-1));
    }
}
