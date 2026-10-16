package Amazon.FSDTS.Rules;

import Amazon.FSDTS.Enums.Cost;
import Amazon.FSDTS.Model.FileEntry;

import java.io.File;

public class NameContainsRule implements Rule {
    private String text;

    public NameContainsRule(String text) {
        this.text = text.toLowerCase();
    }

    @Override
    public boolean matches(FileEntry fileEntry) {
        return fileEntry.name().toLowerCase().contains(text);
    }

    @Override
    public Cost cost() {
        return Cost.FREE;
    }
}
