package streams;

import static streams.Check.that;

import java.io.BufferedReader;
import java.io.StringReader;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * LESSON 15 - Creating streams: every source you will meet
 *
 * <h2>Theory</h2>
 * Lesson 01 showed the basics. Real code gets streams from many more places:
 * <pre>
 *   Finite, known elements    Stream.of(a, b, c)   Arrays.stream(arr)   Arrays.stream(arr, from, toExclusive)
 *   Empty / optional          Stream.empty()       Stream.ofNullable(x) (0 or 1 element)    optional.stream()
 *   Infinite, computed        Stream.iterate(seed, f)            Stream.generate(supplier)
 *   Bounded iterate           Stream.iterate(seed, hasNext, next)   (a stream-flavoured for loop)
 *   Imperative producer       Stream.builder()  add(..) add(..) build()
 *   Combine                   Stream.concat(a, b)      Stream.of(list1, list2).flatMap(List::stream)
 *   I/O and text              BufferedReader.lines()   Files.lines(path) (CLOSE it: try-with-resources!)
 *   Iterable / Iterator       StreamSupport.stream(iterable.spliterator(), false)
 *   Dates                     start.datesUntil(endExclusive)
 *   Enums                     Arrays.stream(MyEnum.values())      EnumSet.allOf(MyEnum.class).stream()
 * </pre>
 * Things to remember:
 * <ul>
 *   <li>Infinite sources need a short-circuiting operation ({@code limit}, {@code takeWhile}, {@code findFirst}).</li>
 *   <li>{@code Stream.iterate(seed, f)} followed by {@code filter} then {@code limit}: the filter is applied
 *       to an endless sequence, so make sure enough elements ever pass, or it never ends.</li>
 *   <li>Streams that wrap a resource (files, DB cursors, {@code Files.lines}, {@code Files.walk}) must be closed.
 *       {@code Stream} is {@code AutoCloseable}: use try-with-resources.</li>
 *   <li>{@code Stream.of(array)} with a single array argument streams its elements (varargs), but
 *       {@code Stream.of(intArray)} gives a one-element {@code Stream<int[]>}. Use {@code Arrays.stream(intArray)}.</li>
 * </ul>
 *
 * Run:  ./run.sh 15        (answers: ./run.sh --solutions 15)
 */
public class Lesson15_StreamSources {

    public static void main(String[] args) {
        demo();

        Check.section("Lesson 15 - Stream sources");
        that("1 powersOfTwo", () -> powersOfTwo(10), List.of(1, 2, 4, 8, 16, 32, 64, 128, 256, 512));
        that("2 fibonacci", () -> fibonacci(10), List.of(0L, 1L, 1L, 2L, 3L, 5L, 8L, 13L, 21L, 34L));
        that("3 collatz", () -> collatz(6), List.of(6L, 3L, 10L, 5L, 16L, 8L, 4L, 2L, 1L));
        that("4 concatTwo", () -> concatTwo(Stream.of(1, 2), Stream.of(3)).toList(), List.of(1, 2, 3));
        that("5 nonNulls", () -> nonNulls(new String[] {"a", null, "b", null}), List.of("a", "b"));
        that("6 nonBlankLines", () -> nonBlankLines("alpha\nbeta\n\n   \ngamma\n"), List.of("alpha", "beta", "gamma"));
        that("7 itemsWithBuilder", Lesson15_StreamSources::itemsWithBuilder, List.of("item-1", "item-2", "item-3"));
        that("8 repeated", () -> repeated("ab", 3), "ababab");
        that("9 sumOfSlice", () -> sumOfSlice(new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 2, 5), 12);
        that("10 fromIterable", () -> fromIterable(() -> List.of("a", "b").iterator()), List.of("A", "B"));
        that("11 weekdaysBetween", () -> weekdaysBetween(LocalDate.of(2024, 2, 26), LocalDate.of(2024, 3, 4)), 5L);
        that("12 weekendNames", Lesson15_StreamSources::weekendNames, List.of("SATURDAY", "SUNDAY"));
        Check.summary();
    }

    static void demo() {
        System.out.println("iterate:      " + Stream.iterate(1, x -> x * 3).limit(5).toList());
        System.out.println("bounded:      " + Stream.iterate(1, x -> x < 100, x -> x * 3).toList());
        System.out.println("ofNullable:   " + Stream.ofNullable(null).count() + " and " + Stream.ofNullable("x").count());
        System.out.println("Stream.of(int[]) is ONE element: " + Stream.of(new int[] {1, 2, 3}).count()
                + ", Arrays.stream(int[]) is " + Arrays.stream(new int[] {1, 2, 3}).count());
    }

    // TODO 1: the first n powers of two starting at 1 (1, 2, 4, ...).   Hint: Stream.iterate(1, x -> x * 2).limit(n)
    static List<Integer> powersOfTwo(int n) {
        return Stream.iterate(1, x -> x * 2).limit(n).toList();
    }

    // TODO 2: the first n Fibonacci numbers 0, 1, 1, 2, 3, ...
    // Hint: iterate over PAIRS: Stream.iterate(new long[] {0, 1}, p -> new long[] {p[1], p[0] + p[1]}),
    //       then map(p -> p[0]).limit(n)
    static List<Long> fibonacci(int n) {
        return Stream.iterate(new long[] {0, 1}, p -> new long[] {p[1], p[0] + p[1]})
                .map(p -> p[0])
                .limit(n)
                .toList();
    }

    // TODO 3: the Collatz sequence from start down to and INCLUDING 1: even n -> n / 2, odd n -> 3n + 1.
    // Hint: the 3-arg iterate(start, n -> n != 1, next) stops BEFORE 1, so concat Stream.of(1L) at the end.
    static List<Long> collatz(long start) {
        return Stream.concat(
                        Stream.iterate(start, n -> n != 1, n -> n % 2 == 0 ? n / 2 : 3 * n + 1),
                        Stream.of(1L))
                .toList();
    }

    // TODO 4: glue two streams together.   Hint: Stream.concat
    static <T> Stream<T> concatTwo(Stream<T> a, Stream<T> b) {
        return Stream.concat(a, b);
    }

    // TODO 5: drop the nulls from an array WITHOUT filter: use Stream.ofNullable inside flatMap.
    // Hint: Arrays.stream(arr).flatMap(Stream::ofNullable).toList()
    static List<String> nonNulls(String[] arr) {
        return Arrays.stream(arr).flatMap(Stream::ofNullable).toList();
    }

    // TODO 6: the non-blank lines of a text (read through a BufferedReader).
    // Hint: new BufferedReader(new StringReader(text)).lines().filter(l -> !l.isBlank()).toList()
    //       (For a real file you would use try-with-resources around Files.lines(path).)
    static List<String> nonBlankLines(String text) {
        return new BufferedReader(new StringReader(text)).lines().filter(l -> !l.isBlank()).toList();
    }

    // TODO 7: when elements come from imperative code, use Stream.builder().
    // Emit "item-1", "item-2", "item-3" with a for loop calling builder.add(..), then build() and toList().
    static List<String> itemsWithBuilder() {
        Stream.Builder<String> builder = Stream.builder();
        for (int i = 1; i <= 3; i++) {
            builder.add("item-" + i);
        }
        return builder.build().toList();
    }

    // TODO 8: repeat s n times and join: ("ab", 3) -> "ababab".
    // Hint: Stream.generate(() -> s).limit(n).collect(joining())   (String.repeat exists, but practise the stream)
    static String repeated(String s, int n) {
        return Stream.generate(() -> s).limit(n).collect(Collectors.joining());
    }

    // TODO 9: sum of arr[from .. to) (to is EXCLUSIVE).   Hint: Arrays.stream(arr, from, to).sum()
    static int sumOfSlice(int[] arr, int from, int to) {
        return Arrays.stream(arr, from, to).sum();
    }

    // TODO 10: an Iterable is not a Collection, so it has no stream(). Convert it, then upper-case every element.
    // Hint: StreamSupport.stream(iterable.spliterator(), false)
    static List<String> fromIterable(Iterable<String> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false).map(String::toUpperCase).toList();
    }

    // TODO 11: how many Monday-to-Friday days are in [from, toExclusive)?
    // Hint: from.datesUntil(toExclusive) is a Stream<LocalDate>; d.getDayOfWeek().getValue() is 1 (Mon) .. 7 (Sun)
    static long weekdaysBetween(LocalDate from, LocalDate toExclusive) {
        return from.datesUntil(toExclusive).filter(d -> d.getDayOfWeek().getValue() <= 5).count();
    }

    // TODO 12: names of the weekend days, in enum order.
    // Hint: Arrays.stream(DayOfWeek.values()).filter(..).map(Enum::name).toList()
    static List<String> weekendNames() {
        return Arrays.stream(DayOfWeek.values())
                .filter(d -> d == DayOfWeek.SATURDAY || d == DayOfWeek.SUNDAY)
                .map(Enum::name)
                .toList();
    }
}
