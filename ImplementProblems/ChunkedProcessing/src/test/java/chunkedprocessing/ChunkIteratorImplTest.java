package chunkedprocessing;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChunkIteratorImplTest {

    // Test 1: Exact multiple — 10 items, chunk size 5 → exactly 2 full chunks
    @Test
    void exactMultiple() {
        List<Integer> data = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        ChunkIterator<Integer> iterator = new ChunkIteratorImpl<>(data, 5);

        assertTrue(iterator.hasNext());
        List<Integer> chunk1 = iterator.nextChunk();
        assertEquals(5, chunk1.size());
        assertEquals(List.of(1, 2, 3, 4, 5), chunk1);

        assertTrue(iterator.hasNext());
        List<Integer> chunk2 = iterator.nextChunk();
        assertEquals(5, chunk2.size());
        assertEquals(List.of(6, 7, 8, 9, 10), chunk2);

        assertFalse(iterator.hasNext());
    }

    // Test 2: Non-multiple — 11 items, chunk size 5 → 2 full + 1 partial
    @Test
    void nonMultiple() {
        List<Integer> data = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
        ChunkIterator<Integer> iterator = new ChunkIteratorImpl<>(data, 5);

        assertTrue(iterator.hasNext());
        assertEquals(5, iterator.nextChunk().size());

        assertTrue(iterator.hasNext());
        assertEquals(5, iterator.nextChunk().size());

        assertTrue(iterator.hasNext());
        List<Integer> lastChunk = iterator.nextChunk();
        assertEquals(1, lastChunk.size());
        assertEquals(List.of(11), lastChunk);

        assertFalse(iterator.hasNext());
    }

    // Test 3: Empty input — hasNext() is false immediately
    @Test
    void emptyInput() {
        ChunkIterator<Integer> iterator = new ChunkIteratorImpl<>(List.of(), 5);
        assertFalse(iterator.hasNext());
    }

    // Test 4: Chunk size larger than input — single chunk with all items
    @Test
    void chunkSizeLargerThanInput() {
        List<Integer> data = List.of(1, 2, 3);
        ChunkIterator<Integer> iterator = new ChunkIteratorImpl<>(data, 100);

        assertTrue(iterator.hasNext());
        List<Integer> chunk = iterator.nextChunk();
        assertEquals(3, chunk.size());
        assertEquals(List.of(1, 2, 3), chunk);

        assertFalse(iterator.hasNext());
    }

    // Bonus: returned chunk is unmodifiable — mutating it should throw
    @Test
    void returnedChunkIsUnmodifiable() {
        List<Integer> data = new ArrayList<>(List.of(1, 2, 3));
        ChunkIterator<Integer> iterator = new ChunkIteratorImpl<>(data, 3);

        List<Integer> chunk = iterator.nextChunk();
        assertThrows(UnsupportedOperationException.class, () -> chunk.set(0, 99));
    }
}
