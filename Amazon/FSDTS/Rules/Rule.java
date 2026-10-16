package Amazon.FSDTS.Rules;

import Amazon.FSDTS.Enums.Cost;
import Amazon.FSDTS.Model.FileEntry;

import java.io.File;

public interface Rule {
    boolean matches(FileEntry fileEntry);
    Cost cost();
}
