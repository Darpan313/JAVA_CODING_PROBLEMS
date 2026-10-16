package Amazon.InMemoryFileSystem.exceptions;

public class NotADirectoryException extends RuntimeException {
    public NotADirectoryException(String message) {
        super(message);
    }
}
