package Verily.Codes.FileStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FileApp2 {
    public record Result(Directory directory, long totalSize, List<File> files){}
    static class File {
        String name;
        long size;

        File(String name, long size) {
            this.name = name;
            this.size = size;
        }
    }
    static class Directory {
        String name;
        Directory parent;
        Map<String, Directory> directories;
        List<File> files;

        public Directory(String name, Directory parent) {
            this.name = name;
            this.parent = parent;
            directories = new HashMap<>();
            files = new ArrayList<>();
        }
    }

    public static void main(String[] args) {
        String input = "$ cd /\n" +
                "$ ls\n" +
                "dir a\n" +
                "14848514 b.txt\n" +
                "8504156 c.dat\n" +
                "dir d\n" +
                "$ cd a\n" +
                "$ ls\n" +
                "dir e\n" +
                "29116 f\n" +
                "2557 g\n" +
                "62596 h.lst\n" +
                "$ cd e\n" +
                "$ ls\n" +
                "584 i\n" +
                "$ cd ..\n" +
                "$ cd ..\n" +
                "$ cd d\n" +
                "$ ls\n" +
                "4060174 j\n" +
                "8033020 d.log\n" +
                "5626152 d.ext\n" +
                "7214296 k";
        Directory root = new Directory("/", null);
        String[] lines = input.split("\n");

        Directory curr = root;
        for(String line : lines) {
            if(line.startsWith("$")) {
                if(line.substring(2,4).equals("cd")) {
                    String dirName = line.substring(5);
                    if(dirName.equals("/")) {
                        curr = root;
                    }
                    else if(dirName.equals("..")) {
                        curr = curr.parent;
                    }
                    else {
                        curr = curr.directories.get(dirName);
                    }
                }
                else if(line.substring(2,4).equals("ls")) {
                }
            }
            else {
                if(line.startsWith("dir")) {
                    String dirName = line.substring(4);
                    curr.directories.putIfAbsent(dirName, new Directory(dirName, curr));
                }
                else {
                    String[] parts = line.split("\\s");
                    long size = Long.parseLong(parts[0]);
                    String fileName = parts[1];
                    curr.files.add(new File(fileName, size));
                }
            }
        }
        List<Result> result = new ArrayList<>();
        getDirectoriesMaxSizeK(root, 100000, result);

        printResult(result);
    }

    static long getDirectoriesMaxSizeK(Directory root, int maxSize, List<Result> result) {
        long totalSize = 0;
        for(File file : root.files) {
            totalSize+=file.size;
        }
        for(Map.Entry<String, Directory> entry : root.directories.entrySet()) {
            Directory directory= entry.getValue();
            totalSize += getDirectoriesMaxSizeK(directory, maxSize, result);
        }

        if(totalSize<=maxSize) {
            result.add(new Result(root, totalSize, root.files));
        }
        return totalSize;
    }

    static void printResult(List<Result> directories) {
        for (Result result : directories) {
            System.out.print("[ ");
            System.out.println(result.directory.name);
            List<File> files = result.files;
            for (File file : files) {
                System.out.println(file.size +"  "+ file.name);
            }
            System.out.println("totalSize = "+result.totalSize);
            System.out.println("]");
        }
    }
}
