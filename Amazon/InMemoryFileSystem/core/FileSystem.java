package Amazon.InMemoryFileSystem.core;

import Amazon.InMemoryFileSystem.exceptions.InvalidPathException;
import Amazon.InMemoryFileSystem.exceptions.IsADirectoryException;
import Amazon.InMemoryFileSystem.exceptions.NotADirectoryException;
import Amazon.InMemoryFileSystem.exceptions.NotFoundException;
import Amazon.InMemoryFileSystem.model.Directory;
import Amazon.InMemoryFileSystem.model.File;
import Amazon.InMemoryFileSystem.model.Node;

import java.util.ArrayList;
import java.util.List;

public class FileSystem {
    private final Directory root = new Directory("", null);

    public void mkdir(String path) {
        walk(parts(path), true, path);
    }

    public void writeFile(String path, String content) {
        List<String> ps = parts(path);
        if(ps.isEmpty()) throw new IsADirectoryException(path);
        Directory parent = walk(ps.subList(0,ps.size()-1), true, path);
        String leaf = ps.get(ps.size()-1);
        Node existing = parent.getChild(leaf);
        if(existing == null) {
            File f = new File(leaf, parent);
            f.write(content);
            parent.put(f);
        }
        else if(existing instanceof File f) {
            f.write(content);
        }
        else {
            throw new IsADirectoryException(path);
        }
    }

    public List<String> ls(String path) {
        List<String> ps = parts(path);
        if(ps.isEmpty()) return root.childNames();
        Directory parent = walk(ps.subList(0, ps.size()-1), false, path);
        Node n = parent.getChild(ps.get(ps.size()-1));
        if(n==null) throw new NotFoundException(path);
        if(!(n instanceof File f)) return List.of(n.getName());
        return ((Directory)n).childNames();
    }

    public String readFile(String path) {
        List<String> ps = parts(path);
        if(ps.isEmpty()) throw new IsADirectoryException(path);
        Directory parent = walk(ps.subList(0, ps.size()-1), false, path);
        Node n = parent.getChild(ps.get(ps.size()-1));
        if(n==null) throw new NotFoundException(path);
        if(!(n instanceof File f)) throw new IsADirectoryException(path);
        return f.read();
    }

    private static List<String> parts(String path) {
        if(path == null || !path.startsWith("/")) throw new InvalidPathException(path);
        if(path.equals("/")) return List.of();

        String[] raw = path.substring(1).split("/", -1);
        List<String> out = new ArrayList<>(raw.length);
        for(String p : raw) {
            if(p.isEmpty() || p.equals(".") || p.equals("..")) throw new InvalidPathException(path);
            out.add(p);
        }
        return out;
    }

    private Directory walk(List<String> parts, boolean createMissing, String pathForErrors) {
        Directory curr = root;
        for(String p : parts) {
            Node next = curr.getChild(p);
            if(next == null) {
                if(!createMissing) throw new NotFoundException(pathForErrors);
                next = new Directory(p, curr);
                curr.put(next);
            }
            else if(!(next instanceof Directory)) {
                throw new NotADirectoryException(pathForErrors);
            }
            curr = (Directory) next;
        }
        return curr;
    }
}
