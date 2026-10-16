package Lyft.Practice.StreamRead;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Test Case 1: Reading across multiple streams ---");
        StringStream.MultiStream ms1 = new StringStream.MultiStream();
        ms1.add(new StringStream("01234"));
        ms1.add(new StringStream("56789"));

        System.out.println("Read 2 (Expected: 01)   -> Output: " + ms1.read(2));
        // The next read asks for 4 chars. Stream 1 only has "234" left (3 chars).
        // It should pull "234", drop stream 1, and pull "5" from stream 2.
        System.out.println("Read 4 (Expected: 2345) -> Output: " + ms1.read(4));
        System.out.println("Read 4 (Expected: 6789) -> Output: " + ms1.read(4));
        System.out.println("Read 2 (Expected: )     -> Output: " + ms1.read(2)); // Both streams exhausted
        System.out.println();

        System.out.println("--- Test Case 2: Deletion ---");
        StringStream.MultiStream ms2 = new StringStream.MultiStream();
        Stream s1 = new StringStream("abc");
        Stream s2 = new StringStream("def"); // We will delete this one
        Stream s3 = new StringStream("ghi");

        ms2.add(s1);
        ms2.add(s2);
        ms2.add(s3);

        // Remove s2 before we ever read from it
        ms2.delete(s2);

        // Should read "abc" from s1, skip s2 entirely, and grab "gh" from s3
        System.out.println("Read 5 (Expected: abcgh) -> Output: " + ms2.read(5));
        System.out.println();

        System.out.println("--- Test Case 3: Empty streams and exact boundaries ---");
        StringStream.MultiStream ms3 = new StringStream.MultiStream();
        ms3.add(new StringStream("")); // Starts empty
        ms3.add(new StringStream("A"));
        ms3.add(new StringStream("B"));

        // Should gracefully skip the empty stream and pull from the next two
        System.out.println("Read 2 (Expected: AB)   -> Output: " + ms3.read(2));
    }
}
