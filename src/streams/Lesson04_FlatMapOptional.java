package streams;

import static streams.Check.that;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * LESSON 04 - flatMap, takeWhile/dropWhile, mapMulti and Optional
 *
 * <h2>Theory</h2>
 * <b>map</b> is 1-to-1. <b>flatMap</b> is 1-to-many: the function returns a <i>Stream</i> for every
 * element and flatMap concatenates (flattens) all of them into one stream.
 * <pre>
 *   [[1,2],[3],[4,5]]  --flatMap(List::stream)-->  1,2,3,4,5
 *   "a b"              --flatMap(s -> Arrays.stream(s.split(" ")))--> "a","b"
 * </pre>
 * <b>takeWhile(p)</b>: take from the start until p is false, then STOP.
 * <b>dropWhile(p)</b>: skip from the start while p is true, then take everything.
 * (Unlike filter, they only look at the leading run of elements.)
 * <br><b>mapMulti</b> (Java 16): like flatMap but pushes 0..n results into a sink; avoids creating a
 * tiny Stream per element.
 * <br><b>Optional.stream()</b> turns an Optional into a 0-or-1 element stream: great inside flatMap.
 *
 * Run:  ./run.sh 04        (answers: ./run.sh --solutions 04)
 */
public class Lesson04_FlatMapOptional {

    public static void main(String[] args) {
        demo();

        Check.section("Lesson 04 - flatMap, takeWhile, mapMulti, Optional");
        that("1 flatten", () -> flatten(List.of(List.of(1, 2), List.of(3), List.of(4, 5))), List.of(1, 2, 3, 4, 5));
        that("2 uniqueWords", () -> uniqueWords(List.of("b a", "a c")), List.of("a", "b", "c"));
        that("3 allSkills", Lesson04_FlatMapOptional::allSkills,
                List.of("aws", "crm", "excel", "go", "java", "kotlin", "react", "sql"));
        that("4 pairs(3)", () -> pairs(3), List.of("1-2", "1-3", "2-3"));
        that("5 untilNegative", () -> untilNegative(List.of(3, 5, -1, 7)), List.of(3, 5));
        that("6 afterLeadingZeros", () -> afterLeadingZeros(List.of(0, 0, 4, 0, 5)), List.of(4, 0, 5));
        that("7 shout(present)", () -> shout(Optional.of("hi")), "HI");
        that("7b shout(empty)", () -> shout(Optional.empty()), "EMPTY");
        that("8 presentOnly", () -> presentOnly(List.of(Optional.of("a"), Optional.empty(), Optional.of("c"))),
                List.of("a", "c"));
        that("9 duplicateEvens", () -> duplicateEvens(List.of(1, 2, 3, 4)), List.of(2, 2, 4, 4));
        Check.summary();
    }

    static void demo() {
        System.out.println(Stream.of(List.of(1, 2), List.of(3)).flatMap(List::stream).toList());
        System.out.println(Stream.of(1, 2, 3, 10, 1).takeWhile(n -> n < 5).toList());
        System.out.println(Stream.of(1, 2, 3, 10, 1).dropWhile(n -> n < 5).toList());
        System.out.println(Optional.of("x").map(String::toUpperCase).orElse("?"));
    }

    // TODO 1: Flatten a list of lists.  Hint: flatMap(List::stream)
    static List<Integer> flatten(List<List<Integer>> in) {
        throw Check.todo();
    }

    // TODO 2: Split every sentence on whitespace, return the distinct words sorted.
    // Hint: flatMap(s -> Stream.of(s.split("\\s+")))
    static List<String> uniqueWords(List<String> sentences) {
        throw Check.todo();
    }

    // TODO 3: All distinct skills across all employees, sorted alphabetically.
    static List<String> allSkills() {
        throw Check.todo();
    }

    // TODO 4: All pairs "i-j" with 1 <= i < j <= n, in order (nested loop as a stream).
    // Hint: outer IntStream.rangeClosed(1, n).boxed().flatMap(i -> inner IntStream ... mapToObj(...))
    static List<String> pairs(int n) {
        throw Check.todo();
    }

    // TODO 5: Take numbers from the start until the first negative one (exclusive).
    static List<Integer> untilNegative(List<Integer> in) {
        throw Check.todo();
    }

    // TODO 6: Drop only the LEADING zeros: [0,0,4,0,5] -> [4,0,5]
    static List<Integer> afterLeadingZeros(List<Integer> in) {
        throw Check.todo();
    }

    // TODO 7: Upper-case the value if present, otherwise return "EMPTY". (No isPresent/get!)
    static String shout(Optional<String> in) {
        throw Check.todo();
    }

    // TODO 8: Given a list of Optionals, return only the present values.
    // Hint: flatMap(Optional::stream)
    static List<String> presentOnly(List<Optional<String>> in) {
        throw Check.todo();
    }

    // TODO 9: Keep even numbers and emit each of them TWICE; drop odd numbers. [1,2,3,4] -> [2,2,4,4]
    // Hint: .<Integer>mapMulti((n, sink) -> { ... sink.accept(n); ... })
    //       The explicit <Integer> type witness is required so the compiler knows the output type.
    static List<Integer> duplicateEvens(List<Integer> in) {
        throw Check.todo();
    }
}
