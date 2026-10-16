package Amazon.InMemoryFileSystem.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Directory extends Node {
    private final TreeMap<String, Node> children = new TreeMap<>();
    public Directory(String name, Directory parent) {
        super(name, parent);
    }

    public Node getChild(String name) {
        return children.get(name);
    }
    public void put(Node child) {
        children.put(child.getName(), child);
    }
    public Node remove(String child) {
        return children.remove(child);
    }
    public List<String> childNames() {
        return new ArrayList<>(children.keySet());
    }
    public boolean isEmpty() {
        return children.isEmpty();
    }
}
