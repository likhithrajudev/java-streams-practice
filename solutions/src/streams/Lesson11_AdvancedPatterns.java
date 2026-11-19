package streams;

import static streams.Check.that;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import streams.Data.Employee;

/**
 * LESSON 11 - Advanced patterns and idioms
 *
 * <h2>Theory</h2>
 * Streams have no built-in zip, window or index, but {@code IntStream.range(0, n)} over a List gives you
 * all of them. A cheat-sheet of patterns for this lesson:
 * <pre>
 *   zip            IntStream.range(0, min(a,b)).mapToObj(i -> f(a.get(i), b.get(i)))
 *   sliding window IntStream.rangeClosed(0, n - k).mapToObj(i -> list.subList(i, i + k))
 *   null-safe      filter(Objects::nonNull)   or   Stream.ofNullable(x)
 *   keep order     collect(toMap(k, v, merge, LinkedHashMap::new))   groupingBy(f, LinkedHashMap::new, ..)
 *   filtering      groupingBy(f, filtering(p, ...))   keeps EMPTY groups; filter(p) before groupingBy drops them
 *   top-N          sorted(cmp).limit(n)
 *   concat         Stream.concat(a, b)
 *   chars          s.chars().mapToObj(c -> (char) c)
 * </pre>
 * When NOT to use a stream: complex control flow, checked exceptions, index-heavy algorithms,
 * running totals, or when a for-loop is simply clearer. Readability wins.
 *
 * Run:  ./run.sh 11        (answers: ./run.sh --solutions 11)
 */
public class Lesson11_AdvancedPatterns {

    public static void main(String[] args) {
        demo();

        Check.section("Lesson 11 - Advanced patterns");
        that("1 zip", () -> zip(List.of("x", "y"), List.of(1, 2, 3)), List.of("x1", "y2"));
        that("2 slidingWindows", () -> slidingWindows(List.of(1, 2, 3, 4), 2),
                List.of(List.of(1, 2), List.of(2, 3), List.of(3, 4)));
        that("3 top3Skills", () -> top3Skills().toString(), "{java=3, sql=3, aws=2}");
        that("4 mergeSortedDistinct", () -> mergeSortedDistinct(List.of(3, 1), List.of(2, 3)), List.of(1, 2, 3));
        that("5 seniorNamesByDept", Lesson11_AdvancedPatterns::seniorNamesByDept,
                Map.of("ENG", List.of("Carol"), "HR", List.of(), "SALES", List.of("Grace")));
        that("6 secondHighestPaid", Lesson11_AdvancedPatterns::secondHighestPaid, Optional.of("Alice"));
        that("7 cleanTrimmed", () -> cleanTrimmed(java.util.Arrays.asList(" a ", null, "  ", "b")), List.of("a", "b"));
        that("8 charFrequency", () -> charFrequency("aab"), Map.of('a', 2L, 'b', 1L));
        that("9 firstUnique(swiss)", () -> firstUnique("swiss"), Optional.of('w'));
        that("9b firstUnique(aabb)", () -> firstUnique("aabb"), Optional.empty());
        Check.summary();
    }

    static void demo() {
        List<String> names = List.of("ann", "bob", "cy");
        // Index + element:
        System.out.println(IntStream.range(0, names.size()).mapToObj(i -> i + ":" + names.get(i)).toList());
        System.out.println(Stream.ofNullable((String) null).count());
    }

    // TODO 1: Zip two lists into "<a><b>" strings, stopping at the shorter list.
    // Hint: IntStream.range(0, Math.min(a.size(), b.size())).mapToObj(i -> a.get(i) + b.get(i))
    static List<String> zip(List<String> a, List<Integer> b) {
        return IntStream.range(0, Math.min(a.size(), b.size())).mapToObj(i -> a.get(i) + b.get(i)).toList();
    }

    // TODO 2: All windows of size k:  [1,2,3,4], k=2 -> [[1,2],[2,3],[3,4]]
    // Hint: IntStream.rangeClosed(0, in.size() - k).mapToObj(i -> in.subList(i, i + k))
    static List<List<Integer>> slidingWindows(List<Integer> in, int k) {
        return IntStream.rangeClosed(0, in.size() - k).mapToObj(i -> in.subList(i, i + k)).toList();
    }

    // TODO 3: The 3 most common skills as a LinkedHashMap skill -> count, ordered by count desc, then name asc.
    // Steps: flatMap skills -> groupingBy(identity, counting()) -> entrySet().stream()
    //        -> sorted(by value desc, then key asc) -> limit(3) -> collect(toMap(.., .., (a,b)->a, LinkedHashMap::new))
    static LinkedHashMap<String, Long> top3Skills() {
        Map<String, Long> freq = Data.employees().stream()
                .flatMap(e -> e.skills().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return freq.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                .limit(3)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    // TODO 4: Concatenate two lists, remove duplicates, sort.  Hint: Stream.concat
    static List<Integer> mergeSortedDistinct(List<Integer> a, List<Integer> b) {
        return Stream.concat(a.stream(), b.stream()).distinct().sorted().toList();
    }

    // TODO 5: dept -> names of employees aged >= 40, but KEEP departments that have nobody (empty list).
    // Hint: groupingBy(dept, filtering(e -> e.age() >= 40, mapping(name, toList())))
    //       (a plain .filter() before groupingBy would drop the HR key - try it and see!)
    static Map<String, List<String>> seniorNamesByDept() {
        return Data.employees().stream().collect(Collectors.groupingBy(
                Employee::dept,
                Collectors.filtering(e -> e.age() >= 40, Collectors.mapping(Employee::name, Collectors.toList()))));
    }

    // TODO 6: Name of the SECOND highest paid employee (Optional).  Hint: sorted desc, skip(1), findFirst
    static Optional<String> secondHighestPaid() {
        return Data.employees().stream()
                .sorted(Comparator.comparingDouble(Employee::salary).reversed())
                .skip(1)
                .findFirst()
                .map(Employee::name);
    }

    // TODO 7: Drop nulls, trim, drop empty results.  [" a ", null, "  ", "b"] -> ["a", "b"]
    static List<String> cleanTrimmed(List<String> in) {
        return in.stream().filter(Objects::nonNull).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }

    // TODO 8: Character -> how many times it occurs.  Hint: chars().mapToObj(c -> (char) c) then groupingBy
    static Map<Character, Long> charFrequency(String s) {
        return s.chars().mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    // TODO 9: First character that appears exactly once ("swiss" -> 'w'), empty if none.
    // Hint: group with LinkedHashMap::new (keeps first-seen order!) + counting(), then stream the entries.
    static Optional<Character> firstUnique(String s) {
        return s.chars().mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()))
                .entrySet().stream()
                .filter(e -> e.getValue() == 1)
                .map(Map.Entry::getKey)
                .findFirst();
    }
}
