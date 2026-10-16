package Verily.Codes.FileStructure;

import java.util.*;

public class FileApp {

    static class File {
        String name;
        long size;

        public File(String name, long size) {
            this.name = name;
            this.size = size;
        }
    }

    static class Folder {
        String name;
        List<Folder> folderList;
        List<File> fileList;
        long totalSize;

        public Folder(String name) {
            this.name = name;
            this.folderList = new ArrayList<>();
            this.fileList = new ArrayList<>();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Folder> getFolderList() {
            return folderList;
        }

        public void setFolderList(List<Folder> folderList) {
            this.folderList = folderList;
        }

        public List<File> getFileList() {
            return fileList;
        }

        public void setFileList(List<File> fileList) {
            this.fileList = fileList;
        }

        public long getTotalSize() {
            return totalSize;
        }

        public void setTotalSize(long totalSize) {
            this.totalSize = totalSize;
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
        Map<String, Folder> folderMap = new HashMap<>();
        Stack<String> stack = new Stack<>();
        String[] lines = input.split("\n");
        for(String line : lines) {
            if(line.startsWith("$")) {
                if(line.substring(2,4).equals("cd")) {
                    String dirName = line.substring(5);
                    if(dirName.equals("..")) {
                        if(stack.size()>1) stack.pop();
                    }
                    else {
                        stack.push(dirName);
                    }
                }
//                else if(line.substring(2,4).equals("ls")) {
//                }
            }
            else {
                if(line.startsWith("dir")) {
                    String dirName = line.substring(4);
                    folderMap.computeIfAbsent(stack.peek(), v->new Folder(dirName))
                            .getFolderList().add(new Folder(dirName));
                }
                else {
                    String[] parts = line.split("\\s");
                    long size = Long.parseLong(parts[0]);
                    String fileName = parts[1];

                    folderMap.computeIfAbsent(stack.peek(), v->new Folder(stack.peek()))
                            .getFileList().add(new File(fileName, size));

                    folderMap.get(stack.peek()).setTotalSize(folderMap.get(stack.peek()).getTotalSize()+size);
                }
            }
        }

        printFolderStruct(folderMap);


    }

    private static void printFolderStruct(Map<String, Folder> map) {
        for(Map.Entry<String, Folder> entry : map.entrySet()) {
            String folderName = entry.getKey();
            Folder folder = entry.getValue();
            if(folder.getTotalSize()>100000) {
                List<Folder> folderList = folder.getFolderList();
                List<File> fileList = folder.getFileList();
                System.out.print(folderName+"=");
                System.out.println("[");

                for (Folder f : folderList) {
                    System.out.println(f.name);
                }
                for(File file : fileList) {
                    System.out.println(file.name + ", " + file.size);
                }
                System.out.println(folder.getTotalSize());
                System.out.println("]");
            }
        }
    }
}
