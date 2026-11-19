package streams;

import static streams.Check.that;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import streams.Data.Employee;

/**
 * LESSON 06 - Collectors, part 1: collecting into containers and single values
 *
 * <h2>Theory</h2>
 * {@code stream.collect(Collector)} is the general "mutable reduction". {@link Collectors} has the
 * ready-made ones:
 * <pre>
 *   toList()  toSet()  toCollection(TreeSet::new)
 *   toUnmodifiableList() / stream.toList()           immutable results
 *   toMap(keyFn, valueFn)                            THROWS on duplicate keys!
 *   toMap(keyFn, valueFn, mergeFn)                   resolve duplicates
 *   toMap(keyFn, valueFn, mergeFn, TreeMap::new)     choose the map type
 *   joining(", ", "[", "]")                          CharSequence -> String
 *   counting()  summingInt/Long/Double  averagingInt/Double  minBy/maxBy
 *   summarizingInt/Double                            statistics object
 * </pre>
 * {@code Stream.toList()} (Java 16) returns an <b>unmodifiable</b> list and allows null elements;
 * {@code Collectors.toList()} returns an unspecified, usually mutable ArrayList.
 *
 * Run:  ./run.sh 06        (answers: ./run.sh --solutions 06)
 */
public class Lesson06_Collectors {

    public static void main(String[] args) {
        demo();

        Check.section("Lesson 06 - Collectors basics");
        that("1 departments", Lesson06_Collectors::departments, Set.of("ENG", "HR", "SALES"));
        that("2 namesJoined", Lesson06_Collectors::namesJoined,
                "[Alice, Bob, Carol, Dave, Eve, Frank, Grace, Heidi]");
        that("3 salaryByName(Carol)", () -> salaryByName().get("Carol"), 150_000.0);
        that("3b salaryByName size", () -> salaryByName().size(), 8);
        that("4 wordCounts", () -> wordCounts(List.of("a", "b", "a")), Map.of("a", 2, "b", 1));
        that("5 totalPayroll", Lesson06_Collectors::totalPayroll, 778_000.0);
        that("5b averageAge", Lesson06_Collectors::averageAge, 34.125);
        that("6 topEarnerName", Lesson06_Collectors::topEarnerName, "Carol");
        that("7 toListIsImmutable", Lesson06_Collectors::toListIsImmutable, true);
        that("8 sortedSkills", () -> {
            TreeSet<String> s = sortedSkills();
            return List.of(s.first(), s.last(), s.size());
        }, List.of("aws", "sql", 8));
        Check.summary();
    }

    static void demo() {
        System.out.println(Stream.of("a", "b", "c").collect(Collectors.joining("|")));
        System.out.println(Stream.of(1, 2, 3, 4).collect(Collectors.summarizingInt(i -> i)));
        try {
            Stream.of("a", "a").collect(Collectors.toMap(s -> s, s -> 1));
        } catch (IllegalStateException e) {
            System.out.println("toMap duplicate key -> " + e.getMessage());
        }
    }

    // TODO 1: The distinct set of department names.  Hint: map + collect(Collectors.toSet())
    static Set<String> departments() {
        throw Check.todo();
    }

    // TODO 2: All employee names as "[Alice, Bob, ...]".
    // Hint: Collectors.joining(", ", "[", "]")
    static String namesJoined() {
        throw Check.todo();
    }

    // TODO 3: Map name -> salary.  Hint: Collectors.toMap(Employee::name, Employee::salary)
    static Map<String, Double> salaryByName() {
        throw Check.todo();
    }

    // TODO 4: Count occurrences of each word WITHOUT groupingBy (that is next lesson).
    // Use toMap with a merge function to handle duplicates: toMap(w -> w, w -> 1, Integer::sum)
    static Map<String, Integer> wordCounts(List<String> words) {
        throw Check.todo();
    }

    // TODO 5: Total payroll.  Hint: Collectors.summingDouble(Employee::salary)
    static double totalPayroll() {
        throw Check.todo();
    }

    // TODO 5b: Average age.  Hint: Collectors.averagingInt(Employee::age)
    static double averageAge() {
        throw Check.todo();
    }

    // TODO 6: Name of the top earner using Collectors.maxBy(...) (it returns an Optional).
    static String topEarnerName() {
        throw Check.todo();
    }

    // TODO 7: Show that Stream.toList() is unmodifiable: build a list with Stream.of(1, 2).toList(),
    // try list.add(3), and return true if UnsupportedOperationException was thrown.
    static boolean toListIsImmutable() {
        throw Check.todo();
    }

    // TODO 8: Every distinct skill in a TreeSet (sorted).  Hint: flatMap + toCollection(TreeSet::new)
    static TreeSet<String> sortedSkills() {
        throw Check.todo();
    }
}
