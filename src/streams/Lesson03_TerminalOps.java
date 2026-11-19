package streams;

import static streams.Check.that;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import streams.Data.Employee;

/**
 * LESSON 03 - Terminal operations (they trigger the pipeline and produce a result)
 *
 * <h2>Theory</h2>
 * <pre>
 *   toList()                    immutable List (Java 16+)
 *   toArray(String[]::new)      typed array
 *   count()                     long
 *   forEach / forEachOrdered    side effect per element (use sparingly)
 *   reduce(identity, op)        fold to ONE value:  reduce(0, Integer::sum)
 *   reduce(op)                  Optional<T>  (no identity, may be empty)
 *   min(cmp) / max(cmp)         Optional<T>
 *   findFirst() / findAny()     Optional<T>   (short-circuit)
 *   anyMatch / allMatch / noneMatch   boolean (short-circuit)
 * </pre>
 * <b>Optional</b> models "maybe a value": prefer {@code map}, {@code orElse}, {@code orElseThrow}
 * over {@code isPresent()} + {@code get()}.
 * <br><b>Short-circuiting</b> ops (findFirst, anyMatch, limit...) can stop early, even on infinite streams.
 *
 * Run:  ./run.sh 03        (answers: ./run.sh --solutions 03)
 */
public class Lesson03_TerminalOps {

    public static void main(String[] args) {
        demo();

        Check.section("Lesson 03 - Terminal operations");
        that("1 sum", () -> sum(List.of(1, 2, 3, 4)), 10);
        that("2 longest", () -> longest(List.of("a", "abc", "ab")), Optional.of("abc"));
        that("2b longest(empty)", () -> longest(List.of()), Optional.empty());
        that("3 anyEmployeeOlderThan(44)", () -> anyEmployeeOlderThan(44), true);
        that("3b anyEmployeeOlderThan(45)", () -> anyEmployeeOlderThan(45), false);
        that("4 allEarnAtLeast(60000)", () -> allEarnAtLeast(60_000), true);
        that("4b allEarnAtLeast(61000)", () -> allEarnAtLeast(61_000), false);
        that("5 firstNameInDept(HR)", () -> firstNameInDept("HR", "none"), "Dave");
        that("5b firstNameInDept(OPS)", () -> firstNameInDept("OPS", "none"), "none");
        that("6 countWithSkill(sql)", () -> countWithSkill("sql"), 3L);
        that("7 youngestName", Lesson03_TerminalOps::youngestName, "Frank");
        that("8 totalSalary", Lesson03_TerminalOps::totalSalary, 778_000.0);
        that("9 toUpperArray", () -> toUpperArray(List.of("a", "b")), new String[] {"A", "B"});
        that("10 noneBlank", () -> noneBlank(List.of("a", " ")), false);
        Check.summary();
    }

    static void demo() {
        List<Integer> nums = List.of(4, 8, 15, 16, 23, 42);
        System.out.println("count  = " + nums.stream().count());
        System.out.println("sum    = " + nums.stream().reduce(0, Integer::sum));
        System.out.println("max    = " + nums.stream().max(Integer::compare));
        System.out.println("any>40 = " + nums.stream().anyMatch(n -> n > 40));
        // Short-circuit on an INFINITE stream: this terminates after the first match.
        System.out.println("first multiple of 7 above 50 = "
                + Stream.iterate(1, n -> n + 1).filter(n -> n > 50 && n % 7 == 0).findFirst().orElseThrow());
    }

    // TODO 1: Sum the integers using reduce. Hint: reduce(0, Integer::sum)
    static int sum(List<Integer> in) {
        throw Check.todo();
    }

    // TODO 2: Return the longest string, or Optional.empty() if the list is empty.
    // Hint: max(Comparator.comparingInt(String::length))
    static Optional<String> longest(List<String> in) {
        throw Check.todo();
    }

    // TODO 3: Is ANY employee strictly older than `age`?   Hint: anyMatch
    static boolean anyEmployeeOlderThan(int age) {
        throw Check.todo();
    }

    // TODO 4: Do ALL employees earn at least `min`?   Hint: allMatch
    static boolean allEarnAtLeast(double min) {
        throw Check.todo();
    }

    // TODO 5: Name of the first employee in `dept`, or `fallback` if the department has nobody.
    // Hint: filter -> findFirst -> map(Employee::name) -> orElse(fallback)
    static String firstNameInDept(String dept, String fallback) {
        throw Check.todo();
    }

    // TODO 6: How many employees have `skill` in their skills list? Return a long.
    static long countWithSkill(String skill) {
        throw Check.todo();
    }

    // TODO 7: Name of the youngest employee. Use min(...) and fail loudly if there are no employees.
    // Hint: orElseThrow()
    static String youngestName() {
        throw Check.todo();
    }

    // TODO 8: Total of all salaries using map(...) + reduce(0.0, Double::sum).
    // (Lesson 05 shows the nicer mapToDouble(..).sum().)
    static double totalSalary() {
        throw Check.todo();
    }

    // TODO 9: Upper-case everything into a String[].  Hint: toArray(String[]::new)
    static String[] toUpperArray(List<String> in) {
        throw Check.todo();
    }

    // TODO 10: True when no string is blank.  Hint: noneMatch(String::isBlank)
    static boolean noneBlank(List<String> in) {
        throw Check.todo();
    }
}
