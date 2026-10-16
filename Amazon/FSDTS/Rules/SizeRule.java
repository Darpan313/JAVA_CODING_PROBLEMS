package Amazon.FSDTS.Rules;

import Amazon.FSDTS.Enums.Cost;
import Amazon.FSDTS.Model.FileEntry;

import java.io.File;

public class SizeRule implements Rule {
    private long minBytes;

    public SizeRule(long minBytes) {
        this.minBytes = minBytes;
    }

    @Override
    public boolean matches(FileEntry fileEntry) {
        return fileEntry.size()>minBytes;
    }

    @Override
    public Cost cost() {
        return Cost.CHEAP;
    }
}
