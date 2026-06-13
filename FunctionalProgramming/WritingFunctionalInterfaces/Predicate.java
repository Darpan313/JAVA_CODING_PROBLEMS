package FunctionalProgramming.WritingFunctionalInterfaces;

@FunctionalInterface
public interface Predicate<T> {
    boolean test(T t);
}
