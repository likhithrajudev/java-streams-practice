package streams;

import static streams.Check.that;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * LESSON 01 - What is a stream, and how do I create one?
 *
 * <h2>Theory</h2>
 * <ul>
 *   <li>A stream is NOT a data structure. It is a one-shot, lazy <b>pipeline</b> of computation
 *       over a source (collection, array, generator, file, ...).</li>
 *   <li>Every pipeline has three parts: <b>source</b> -> zero or more <b>intermediate</b> ops
 *       (filter, map, ...) -> exactly one <b>terminal</b> op (toList, count, forEach, ...).</li>
 *   <li>Nothing runs until the terminal operation is called (<i>laziness</i>).</li>
 *   <li>Streams never modify their source.</li>
 *   <li>A stream can be consumed only once. A second use throws {@link IllegalStateException}.</li>
 * </ul>
 *
 * <h2>Ways to create a stream</h2>
 * <pre>
 *   list.stream()                    // from any Collection
 *   Arrays.stream(array)             // from an array
 *   Stream.of(a, b, c)               // from values
 *   IntStream.range(0, 5)            // 0..4   (rangeClosed -> 0..5)
 *   Stream.iterate(1, x -> x * 2)    // infinite: 1, 2, 4, 8 ...
 *   Stream.generate(supplier)        // infinite: calls the supplier each time
 *   "text".chars()                   // IntStream of characters
 *   Stream.empty()                   // no elements
 * </pre>
 *
 * Run:  ./run.sh 01        (answers: ./run.sh --solutions 01)
 */
public class Lesson01_StreamBasics {

    public static void main(String[] args) {
        demo();

        Check.section("Lesson 01 - Stream basics");
        that("1 fromArray", () -> fromArray("a", "b").toList(), List.of("a", "b"));
        that("2 rangeToList", () -> rangeToList(3, 6), List.of(3, 4, 5, 6));
        that("3 firstPowersOfTwo", () -> firstPowersOfTwo(5), List.of(1, 2, 4, 8, 16));
        that("4 charsOf", () -> charsOf("abc"), List.of("a", "b", "c"));
        that("5 secondUseThrows", () -> secondUseThrows(Stream.of("a", "b")), true);
        that("6 repeat", () -> repeat("hi", 3), List.of("hi", "hi", "hi"));
        Check.summary();
    }

    // ---------------------------------------------------------------- demo
    static void demo() {
        List<String> names = List.of("ann", "bob", "cy");
        System.out.println("from list   : " + names.stream().map(String::toUpperCase).toList());
        System.out.println("from array  : " + Arrays.stream(new int[] {1, 2, 3}).sum());
        System.out.println("range       : " + IntStream.range(0, 5).boxed().toList());
        System.out.println("iterate     : " + Stream.iterate(1, x -> x * 3).limit(5).toList());

        // Laziness: nothing is printed here, because there is no terminal operation.
        Stream.of("x", "y").peek(s -> System.out.println("never printed " + s));
        System.out.println("(the peek above printed nothing: no terminal op)");
    }

    // ---------------------------------------------------------- exercises

    // TODO 1: Return a Stream containing exactly the given items (do NOT collect it).
    // Hint: Stream.of(...) or Arrays.stream(...)
    static Stream<String> fromArray(String... items) {
        return Arrays.stream(items);
    }

    // TODO 2: Return the integers from..toInclusive as a List<Integer>.
    // Hint: IntStream.rangeClosed(..) gives an IntStream; .boxed() turns it into Stream<Integer>.
    static List<Integer> rangeToList(int from, int toInclusive) {
        return IntStream.rangeClosed(from, toInclusive).boxed().toList();
    }

    // TODO 3: Return the first n powers of two: 1, 2, 4, 8, ...
    // Hint: Stream.iterate(seed, f) is infinite - you must limit(n) it.
    static List<Integer> firstPowersOfTwo(int n) {
        return Stream.iterate(1, x -> x * 2).limit(n).toList();
    }

    // TODO 4: Split a string into a list of single-character strings: "abc" -> ["a","b","c"].
    // Hint: s.chars() is an IntStream of code units. mapToObj((int c) -> ...) converts each to an object.
    static List<String> charsOf(String s) {
        return s.chars().mapToObj(c -> String.valueOf((char) c)).toList();
    }

    // TODO 5: Prove that a stream is single-use.
    // Consume the stream once (e.g. count()), then try to consume it again.
    // Return true if the second attempt threw IllegalStateException, false otherwise.
    static boolean secondUseThrows(Stream<String> stream) {
        stream.count();
        try {
            stream.count();
            return false;
        } catch (IllegalStateException e) {
            return true;
        }
    }

    // TODO 6: Return a list containing `value` repeated n times.
    // Hint: Stream.generate(() -> value).limit(n)
    static List<String> repeat(String value, int n) {
        return Stream.generate(() -> value).limit(n).toList();
    }
}
