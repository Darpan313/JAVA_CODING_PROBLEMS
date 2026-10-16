package Amazon.FSDTS.Rules;

import Amazon.FSDTS.Enums.Cost;
import Amazon.FSDTS.Model.FileEntry;

import java.io.File;

public class ExtensionRule implements Rule {
    private String ext;
    public ExtensionRule(String ext) {
        this.ext = ext;
    }
    @Override
    public boolean matches(FileEntry fileEntry) {
        return fileEntry.name().endsWith(ext);
    }

    @Override
    public Cost cost() {
        return Cost.FREE;
    }
}
