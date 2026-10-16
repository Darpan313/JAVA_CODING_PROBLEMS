package Amazon.FSDTS.Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class FileEntry {
    private final File file;
    private byte[] cachedHeader;
    private static final int MIN_READ = 512;

    public static int diskReads = 0;

    public FileEntry(File file) {
        this.file = file;
    }

    public File file() {
        return file;
    }
    public String name() {
        return file.getName();
    }
    public long size() {
        return file.length();
    }

    public static int getDiskReads() {
        return diskReads;
    }

    public byte[] header(int n) {
        if(cachedHeader!=null) {
            return cachedHeader;
        }
        diskReads++;
        try(var in = new FileInputStream(file)) {
            cachedHeader = in.readNBytes(Math.max(n, MIN_READ));
            return cachedHeader;
        } catch (IOException ex) {
            cachedHeader = new byte[0];
            return cachedHeader;
        }
    }
}
