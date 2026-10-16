package Amazon.InMemoryFileSystem.model;

public abstract class Node {
    protected final String name;
    protected Directory parent;     // null for root
    Node(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }
}
