package streams;

import static streams.Check.that;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * LESSON 08 - reduce in depth, iterate with a predicate, and writing your own Collector
 *
 * <h2>Theory</h2>
 * <b>reduce</b> has three forms:
 * <pre>
 *   reduce(BinaryOperator)                         -> Optional<T>
 *   reduce(identity, BinaryOperator)               -> T
 *   reduce(identity, accumulator, combiner)        -> U     (result type differs from element type)
 * </pre>
 * Rules (they matter for parallel streams in lesson 10):
 * <ul>
 *   <li>identity must be neutral: {@code op(identity, x) == x} (0 for +, 1 for *, "" for concat).</li>
 *   <li>the operator must be <b>associative</b>: {@code (a op b) op c == a op (b op c)}.</li>
 *   <li>the operator must not mutate shared state. To accumulate into a mutable container use
 *       <b>collect</b>, not reduce.</li>
 * </ul>
 * <b>Stream.iterate(seed, hasNext, next)</b> (Java 9) is a stream-shaped {@code for} loop.
 * <br>A <b>Collector</b> is 4 functions: supplier (new container), accumulator (add one element),
 * combiner (merge two containers, for parallel), finisher (container -> result).
 *
 * Run:  ./run.sh 08        (answers: ./run.sh --solutions 08)
 */
public class Lesson08_ReduceAndCustomCollectors {

    public static void main(String[] args) {
        demo();

        Check.section("Lesson 08 - reduce, iterate, custom collectors");
        that("1 factorial(5)", () -> factorial(5), 120);
        that("1b factorial(0)", () -> factorial(0), 1);
        that("2 fibonacci(8)", () -> fibonacci(8), List.of(0L, 1L, 1L, 2L, 3L, 5L, 8L, 13L));
        that("3 collatz(6)", () -> collatz(6), List.of(6, 3, 10, 5, 16, 8, 4, 2));
        that("4 longestWord", () -> longestWord(List.of("aa", "b", "ccc", "dd")), Optional.of("ccc"));
        that("5 totalLength", () -> totalLength(List.of("ab", "cde")), 5);
        that("6 sumOfSquares collector", () -> Stream.of(1, 2, 3).collect(sumOfSquares()), 14);
        that("7 commaSeparatedInitials", () -> Stream.of("ada", "grace", "linus").collect(initials()), "A,G,L");
        Check.summary();
    }

    static void demo() {
        System.out.println(Stream.of("a", "b", "c").reduce("", String::concat));
        // Non-associative op: result depends on evaluation order, so parallel() would break it.
        System.out.println("10-1-2-3 sequential = " + Stream.of(1, 2, 3).reduce(10, (a, b) -> a - b));
    }

    // TODO 1: n! using reduce.  Hint: IntStream.rangeClosed(1, n).reduce(1, (a, b) -> a * b)
    static int factorial(int n) {
        return IntStream.rangeClosed(1, n).reduce(1, (a, b) -> a * b);
    }

    // TODO 2: First n Fibonacci numbers: 0,1,1,2,3,5,...
    // Hint: iterate over PAIRS: Stream.iterate(new long[] {0, 1}, f -> new long[] {f[1], f[0] + f[1]})
    //       then limit(n) and map each pair to its first value.
    static List<Long> fibonacci(int n) {
        return Stream.iterate(new long[] {0, 1}, f -> new long[] {f[1], f[0] + f[1]})
                .limit(n)
                .map(f -> f[0])
                .toList();
    }

    // TODO 3: The Collatz sequence from `start` until (but excluding) 1:
    //   even -> n/2, odd -> 3n+1.  collatz(6) = 6,3,10,5,16,8,4,2
    // Hint: Stream.iterate(start, n -> n != 1, n -> ...)
    static List<Integer> collatz(int start) {
        return Stream.iterate(start, n -> n != 1, n -> n % 2 == 0 ? n / 2 : 3 * n + 1).toList();
    }

    // TODO 4: Longest word via reduce(BinaryOperator) (the Optional form), not max().
    static Optional<String> longestWord(List<String> in) {
        return in.stream().reduce((a, b) -> a.length() >= b.length() ? a : b);
    }

    // TODO 5: Total number of characters using the 3-argument reduce.
    // Hint: reduce(0, (acc, s) -> acc + s.length(), Integer::sum)
    static int totalLength(List<String> in) {
        return in.stream().reduce(0, (acc, s) -> acc + s.length(), Integer::sum);
    }

    // TODO 6: Write a Collector<Integer, ?, Integer> that returns the sum of squares of the elements.
    // Hint: Collector.of(() -> new int[1],            // supplier: a 1-slot mutable box
    //                    (box, x) -> box[0] += x * x, // accumulator
    //                    (a, b) -> { a[0] += b[0]; return a; }, // combiner
    //                    box -> box[0]);              // finisher
    static Collector<Integer, ?, Integer> sumOfSquares() {
        return Collector.of(
                () -> new int[1],
                (box, x) -> box[0] += x * x,
                (a, b) -> {
                    a[0] += b[0];
                    return a;
                },
                box -> box[0]);
    }

    // TODO 7: Write a Collector<String, ?, String> that joins the FIRST LETTER of each string
    // upper-cased, separated by commas: "ada","grace","linus" -> "A,G,L".
    // Hint: either Collector.of(StringBuilder::new, ...) or compose existing collectors:
    //       Collectors.mapping(s -> s.substring(0, 1).toUpperCase(), Collectors.joining(","))
    static Collector<String, ?, String> initials() {
        return Collectors.mapping(s -> s.substring(0, 1).toUpperCase(), Collectors.joining(","));
    }
}
