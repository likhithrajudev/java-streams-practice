package streams;

import static streams.Check.that;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import streams.Data.Employee;

/**
 * LESSON 02 - Intermediate operations (they return a new Stream and are lazy)
 *
 * <h2>Theory</h2>
 * <pre>
 *   filter(Predicate)        keep elements that match
 *   map(Function)            transform each element (1 in -> 1 out)
 *   distinct()               remove duplicates (uses equals/hashCode)
 *   sorted() / sorted(cmp)   order elements
 *   limit(n) / skip(n)       take first n / drop first n
 *   peek(Consumer)           look at elements (debugging ONLY)
 * </pre>
 * <b>Stateless</b> ops (filter, map, peek) look at one element at a time.
 * <b>Stateful</b> ops (sorted, distinct, limit, skip) need to remember other elements; {@code sorted}
 * must see ALL elements before emitting the first one.
 *
 * <h2>Comparators you will use constantly</h2>
 * <pre>
 *   Comparator.comparing(Employee::name)
 *   Comparator.comparingInt(String::length)
 *   Comparator.comparingDouble(Employee::salary).reversed()
 *   Comparator.comparing(String::length).thenComparing(Comparator.naturalOrder())
 * </pre>
 * Rule of thumb: put <b>filter before map/sorted</b> so later steps work on fewer elements.
 *
 * Run:  ./run.sh 02        (answers: ./run.sh --solutions 02)
 */
public class Lesson02_IntermediateOps {

    public static void main(String[] args) {
        demo();

        Check.section("Lesson 02 - Intermediate operations");
        that("1 evens", () -> evens(List.of(1, 2, 3, 4, 5, 6)), List.of(2, 4, 6));
        that("2 lengths", () -> lengths(List.of("java", "go", "rust")), List.of(4, 2, 4));
        that("3 upperDistinctSorted", () -> upperDistinctSorted(List.of("b", "a", "b", "c")), List.of("A", "B", "C"));
        that("4 sortByLengthThenAlpha",
                () -> sortByLengthThenAlpha(List.of("pear", "fig", "apple", "kiwi", "date")),
                List.of("fig", "date", "kiwi", "pear", "apple"));
        that("5 page", () -> page(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 1, 3), List.of(4, 5, 6));
        that("6 highEarnerNames", Lesson02_IntermediateOps::highEarnerNames, List.of("Carol", "Alice", "Heidi"));
        that("7 threeOldest", Lesson02_IntermediateOps::threeOldest, List.of("Carol", "Grace", "Dave"));
        Check.summary();
    }

    static void demo() {
        System.out.println(Stream.of(5, 3, 8, 3, 1).distinct().sorted().toList());
        // Vertical processing: each element travels through the WHOLE pipeline before the next starts.
        Stream.of("a", "b").peek(s -> System.out.println("peek " + s)).map(String::toUpperCase)
                .forEach(s -> System.out.println("got  " + s));
    }

    // TODO 1: Keep only the even numbers.
    static List<Integer> evens(List<Integer> in) {
        throw Check.todo();
    }

    // TODO 2: Map each string to its length.  Hint: String::length
    static List<Integer> lengths(List<String> in) {
        throw Check.todo();
    }

    // TODO 3: Upper-case each string, drop duplicates, then sort alphabetically.
    static List<String> upperDistinctSorted(List<String> in) {
        throw Check.todo();
    }

    // TODO 4: Sort by length first, ties broken alphabetically.
    // Hint: Comparator.comparing(String::length).thenComparing(Comparator.naturalOrder())
    static List<String> sortByLengthThenAlpha(List<String> in) {
        throw Check.todo();
    }

    // TODO 5: Pagination. page is 0-based. page(list, 1, 3) is the 2nd page of size 3.
    // Hint: skip(...) then limit(...). Watch out for int overflow: use (long) page * size.
    static List<Integer> page(List<Integer> in, int page, int size) {
        throw Check.todo();
    }

    // TODO 6: Names of employees earning MORE than 100_000, highest salary first.
    // Hint: Data.employees(); sorted(Comparator.comparingDouble(Employee::salary).reversed())
    static List<String> highEarnerNames() {
        throw Check.todo();
    }

    // TODO 7: Names of the 3 oldest employees, oldest first.
    static List<String> threeOldest() {
        throw Check.todo();
    }
}
