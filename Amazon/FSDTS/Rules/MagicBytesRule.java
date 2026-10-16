package Amazon.FSDTS.Rules;

import Amazon.FSDTS.Enums.Cost;
import Amazon.FSDTS.Model.FileEntry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class MagicBytesRule implements Rule {
    byte[] signature;
    public MagicBytesRule(byte[] signature) {
        this.signature = signature;
    }
    @Override
    public boolean matches(FileEntry fileEntry) {
        byte[] head = fileEntry.header(signature.length);
        if(head.length<signature.length) return false;
        return Arrays.equals(head, 0, signature.length, signature, 0, signature.length);
    }

    @Override
    public Cost cost() {
        return Cost.EXPENSIVE;
    }
}
