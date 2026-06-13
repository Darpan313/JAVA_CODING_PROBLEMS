package chunkedprocessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChunkIteratorImpl<T> implements chunkedprocessing.ChunkIterator<T> {
    private final List<T> list;
    private final int chunkSize;
    private int nextChunkIndex;

    public ChunkIteratorImpl(List<T> list, int chunkSize) {
        this.list = new ArrayList<>(list);
        this.chunkSize = chunkSize;
        this.nextChunkIndex = 0;
    }

    @Override
    public boolean hasNext() {
        return nextChunkIndex < list.size();
    }

    @Override
    public List<T> nextChunk() {
        int toIndex = Math.min(nextChunkIndex + chunkSize, list.size());
        List<T> subList = list.subList(nextChunkIndex, toIndex);
        nextChunkIndex = toIndex;
        return Collections.unmodifiableList(subList);
    }
}
