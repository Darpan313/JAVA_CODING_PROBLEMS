package Amazon.FSDTS;

import Amazon.FSDTS.Model.FileEntry;
import Amazon.FSDTS.Rules.*;

import java.io.File;

/**
 * STEP 1 — The dumb version. No design patterns. Just make it work.
 *
 * Goal: print every .java file under a starting folder.
 *
 * Fill in the 4 TODOs below, then run:
 *     javac Step1_DumbSearch.java && java Step1_DumbSearch
 *
 * Don't worry about efficiency, extensibility, or anything from instructions.md.
 * We are going to deliberately write bad code and then fix it.
 */
public class SearchEngine {

    /**
     * Walk `folder` and print the path of every .java file found inside it,
     * including inside sub-folders.
     * <p>
     * This is recursive: a folder contains files (print them if they match)
     * and other folders (call this same method on them).
     */
    static void search(File folder, Rule rule) {
        File[] files = folder.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                search(file, rule);
            } else {
                if (rule.matches(new FileEntry(file))) {
                    System.out.println(file.getPath());
                }
            }
        }

    }

    public static void main(String[] args) {
        // Search this project. Change the path if you like.
//        File start = new File("/Users/darpan/Desktop/Learning/JAVA_CODING_PROBLEMS/OOP");
        File start = new File("/Users/darpan/Desktop/Learning/");

//        System.out.println("Searching " + start.getPath() + " for .java files...\n");
//        Rule rule = new AndRule(
//                new OrRule(new ExtensionRule(".java"), new ExtensionRule(".txt")),
//                new SizeRule(500),
//                new NotRule(new NameContainsRule("test"))
//        );
//        search(start, rule);
//        byte[] PNG = {(byte)0x89, 0x50, 0x4E, 0x47};
//        // Order A — expensive rule first
//        search(start, new AndRule(new MagicBytesRule(PNG), new ExtensionRule(".png")));
//        System.out.println("Order A disk reads: " + MagicBytesRule.diskReads);
//
//        MagicBytesRule.diskReads = 0;
//
//        // Order B — cheap rule first
//        search(start, new AndRule(new ExtensionRule(".png"), new MagicBytesRule(PNG)));
//        System.out.println("Order B disk reads: " + MagicBytesRule.diskReads);

        byte[] PNG  = { (byte) 0x89, 0x50, 0x4E, 0x47 };
        byte[] JPEG = { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF };
        search(start, new OrRule(new MagicBytesRule(PNG), new MagicBytesRule(JPEG)));
        System.out.println("Two-signature disk reads: " + FileEntry.diskReads);

        System.out.println("\nDone.");
    }
}
