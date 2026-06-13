package chunkedprocessing;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ChunkByStreaming<T> {

    public List<List<T>> iterate(List<T> list, int chunkSize) {
        return IntStream.range(0, list.size())
                .boxed()
                .collect(Collectors.groupingBy(i -> i / chunkSize,
                        Collectors.mapping(list::get, Collectors.toList())))
                .values()
                .stream()
                .toList();
    }
}
