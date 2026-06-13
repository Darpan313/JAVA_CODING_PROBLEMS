package chunkedprocessing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IteratorChunkImpl<T> implements ChunkIterator<T> {
    private final Iterator<T> iterator;
    private final int chunkSize;

    public IteratorChunkImpl(Iterator<T> iterator, int chunkSize) {
        this.iterator = iterator;
        this.chunkSize = chunkSize;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public List<T> nextChunk() {
        List<T> chunk = new ArrayList<>(chunkSize);
        for(int i=0; i<chunkSize && iterator.hasNext(); i++) {
            chunk.add(iterator.next());
        }
        return chunk;
    }
}
