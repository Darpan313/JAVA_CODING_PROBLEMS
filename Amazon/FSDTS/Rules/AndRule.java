package Amazon.FSDTS.Rules;

import Amazon.FSDTS.Enums.Cost;
import Amazon.FSDTS.Model.FileEntry;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class AndRule implements Rule {
    private Rule[] rules;

    public AndRule(Rule... rules) {
        this.rules = rules;
        Arrays.sort(this.rules, Comparator.comparing(Rule::cost));
    }

    @Override
    public boolean matches(FileEntry fileEntry) {
        for(Rule rule : rules) {
            if(!rule.matches(fileEntry)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Cost cost() {
        Cost max = Cost.FREE;
        for(Rule rule : rules) {
            if(rule.cost().compareTo(max)>0) {
                max = rule.cost();
            }
        }
        return max;
    }
}
