package Lyft.Practice.StreamRead;

import java.util.LinkedList;
import java.util.Queue;

interface Stream{
    String read(int n);
}
public class StringStream implements Stream{
    private String data;
    private int pointer;

    public StringStream(String data) {
        this.data = data;
        this.pointer = 0;
    }

    @Override
    public String read(int n) {
        if(pointer >= data.length()) return "";

        int end = Math.min(pointer + n, data.length());
        String result = data.substring(pointer, end);

        pointer = end;
        return result;
    }

    public static class MultiStream{
        private Queue<Stream> streamQueue;

        public MultiStream() {
            streamQueue = new LinkedList<>();
        }

        public void add(Stream s) {
            if(s!=null) {
                streamQueue.add(s);
            }
        }

        public void delete(Stream s) {
            if(s!=null) {
                streamQueue.remove(s);
            }
        }

        public String read(int n) {
            StringBuilder res = new StringBuilder();

            while (n>0 && !streamQueue.isEmpty()) {
                Stream currStream = streamQueue.peek();

                int charRequested = n;
                String fetched = currStream.read(charRequested);
                res.append(fetched);
                n-=fetched.length();

                if(fetched.length() < charRequested) {
                    streamQueue.poll();
                }
            }
            return res.toString();
        }
    }
}
