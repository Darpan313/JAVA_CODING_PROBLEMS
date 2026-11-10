package Auvik.AlertThrottling;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AlertThrottler {
    public record Alert(String deviceId, String type, long timestampMs, String message) {}

    public interface Sink {
        void emit(Alert alert);
        void emitSummary(String deviceId, String type, int suppressedCount, long windowStartMs);
    }

    private record Key(String deviceId, String type) {}

    private static final class Window {
        final long startMs;
        int suppressed;
        Window(long startMs) {
            this.startMs = startMs;
        }
    }

    private final long windowMs;
    private final Sink sink;
    private final Map<Key, Window> windows = new HashMap<>();

    public AlertThrottler(long windowMs, Sink sink) {
        this.windowMs = windowMs;
        this.sink = sink;
    }

    public synchronized void onAlert(Alert alert) {
        Key key = new Key(alert.deviceId, alert.type);
        Window window = windows.get(key);

        if(window != null && alert.timestampMs - window.startMs < windowMs) {
            window.suppressed++;
            return;
        }

        if(window != null) {
            close(key, window);
        }

        windows.put(key, new Window(alert.timestampMs));
        sink.emit(alert);
    }

    public synchronized void expire(long nowMs) {
        Iterator<Map.Entry<Key, Window>> it = windows.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<Key, Window> entry = it.next();
            if(nowMs >= entry.getValue().startMs + windowMs) {
                close(entry.getKey(), entry.getValue());
                it.remove();
            }
        }
    }

    private void close(Key key, Window window) {
        if(window.suppressed > 0) {
            sink.emitSummary(key.deviceId, key.type, window.suppressed, window.startMs);
        }
    }

    public synchronized int openWindows() {
        return windows.size();
    }
}
