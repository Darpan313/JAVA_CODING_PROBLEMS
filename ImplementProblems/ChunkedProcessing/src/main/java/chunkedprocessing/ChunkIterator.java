package chunkedprocessing;

import java.util.List;

public interface ChunkIterator<T> {
    boolean hasNext();
    List<T> nextChunk();
}
