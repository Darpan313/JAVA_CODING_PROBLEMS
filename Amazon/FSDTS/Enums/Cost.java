package Amazon.FSDTS.Enums;

public enum Cost {
    FREE,   // pure string work on the name - ExtensionRule, NameContainsRule
    CHEAP,  // one cheap metadata lookup - SizeRule
    EXPENSIVE   // touches the disk - MagicBytesRule
}
