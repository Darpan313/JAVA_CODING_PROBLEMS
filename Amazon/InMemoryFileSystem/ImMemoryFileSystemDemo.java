package Amazon.InMemoryFileSystem;

import Amazon.InMemoryFileSystem.core.FileSystem;
import Amazon.InMemoryFileSystem.exceptions.InvalidPathException;
import Amazon.InMemoryFileSystem.exceptions.IsADirectoryException;
import Amazon.InMemoryFileSystem.exceptions.NotADirectoryException;
import Amazon.InMemoryFileSystem.exceptions.NotFoundException;

/*
    Design an in-memory file system.
    Support mkdir, writeFile, readFile, and ls. Paths are absolute, like /a/b/c.
    Design it in Java.
 */
public class ImMemoryFileSystemDemo {
    public static void main(String[] args) {
        FileSystem fs = new FileSystem();
        fs.mkdir("/a/b/c");
        fs.writeFile("/a/b/c/hello.txt", "hi");
        fs.writeFile("/a/x.txt", "x");
        System.out.println(fs.ls("/"));     // [a]
        System.out.println(fs.ls("/a"));    // [b, x.txt]
        System.out.println(fs.ls("/a/b/c/hello.txt"));      // [hello.txt]
        System.out.println(fs.readFile("/a/b/c/hello.txt")); // hi


        try { fs.readFile("/a/b"); }        catch (IsADirectoryException e)   { System.out.println(e.getMessage()); }
        try { fs.mkdir("/a/x.txt/y"); }     catch (NotADirectoryException e)  { System.out.println(e.getMessage()); }
        try { fs.ls("/nope"); }             catch (NotFoundException e)       { System.out.println(e.getMessage()); }
        try { fs.mkdir("/a//b"); }          catch (InvalidPathException e)    { System.out.println(e.getMessage()); }
    }
}
