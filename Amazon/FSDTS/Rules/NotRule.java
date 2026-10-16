package Amazon.FSDTS.Rules;

import Amazon.FSDTS.Enums.Cost;
import Amazon.FSDTS.Model.FileEntry;

import java.io.File;

public class NotRule implements Rule {
    private Rule rule;

    public NotRule(Rule rule) {
        this.rule = rule;
    }

    @Override
    public boolean matches(FileEntry fileEntry) {
        return !rule.matches(fileEntry);
    }

    @Override
    public Cost cost() {
        return rule.cost();
    }
}
