package FunctionalProgramming.WritingFunctionalInterfaces;

public class GacMelonPredicate  implements MelonPredicate {
    @Override
    public boolean test(Melon melon) {
        return "gac".equalsIgnoreCase(melon.getType());
    }
}
