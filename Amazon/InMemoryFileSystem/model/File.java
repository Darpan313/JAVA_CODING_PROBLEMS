package Amazon.InMemoryFileSystem.model;

public class File extends Node {
    private StringBuilder content = new StringBuilder();
    public File(String name, Directory parent) {
        super(name, parent);
    }
    public String read() {
        return content.toString();
    }
    public void write(String s) {
        content.setLength(0);
        content.append(s);
    }
    public void append(String s) {
        content.append(s);
    }
}
