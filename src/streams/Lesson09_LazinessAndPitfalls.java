package streams;

import static streams.Check.that;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * LESSON 09 - How streams really execute: laziness, ordering, pitfalls
 *
 * <h2>Theory</h2>
 * <ol>
 *   <li><b>Lazy</b>: building a pipeline does no work. Only the terminal op pulls elements through.</li>
 *   <li><b>Vertical (element-at-a-time) processing</b>: element 1 goes through ALL stateless stages
 *       before element 2 starts. Exception: a stateful barrier such as {@code sorted()} must collect
 *       everything first.</li>
 *   <li><b>Short-circuiting</b>: limit/findFirst/anyMatch stop pulling early, so later stages may
 *       never see most elements. That is why infinite streams are usable.</li>
 *   <li><b>count() may skip your pipeline</b>: if the size is known from the source (e.g. a List) and no
 *       filter/flatMap changed it, JDK 9+ returns the size without running map/peek at all.</li>
 *   <li><b>Single use</b>: keep a {@code Supplier<Stream<T>>} if you need to traverse twice.</li>
 *   <li><b>No interference</b>: never modify the source while the stream runs
 *       (ConcurrentModificationException or silent wrong results).</li>
 *   <li><b>Avoid side effects in lambdas</b>: do not {@code forEach(list::add)}; use collect/toList.
 *       Behaviour is wrong or racy as soon as the stream becomes parallel.</li>
 *   <li><b>peek is for debugging</b>, never for logic: it may not run at all (see 4).</li>
 * </ol>
 *
 * Run:  ./run.sh 09        (answers: ./run.sh --solutions 09)
 */
public class Lesson09_LazinessAndPitfalls {

    public static void main(String[] args) {
        demo();

        Check.section("Lesson 09 - Laziness and pitfalls");
        that("1 firstTwoSquaresOver100", Lesson09_LazinessAndPitfalls::firstTwoSquaresOver100, List.of(121, 144));
        that("2 peekCallsWithoutTerminal", Lesson09_LazinessAndPitfalls::peekCallsWithoutTerminal, 0);
        that("3 peekCallsWithCountOnly", Lesson09_LazinessAndPitfalls::peekCallsWithCountOnly, 0);
        that("3b peekCallsWithFilterThenCount", Lesson09_LazinessAndPitfalls::peekCallsWithFilterThenCount, 3);
        that("4 verticalOrder", Lesson09_LazinessAndPitfalls::verticalOrder,
                List.of("peek-a", "map-a", "peek-b", "map-b"));
        that("5 sortedBarrierOrder", Lesson09_LazinessAndPitfalls::sortedBarrierOrder,
                List.of("peek-b", "peek-a", "map-a", "map-b"));
        that("6 countTwice", () -> countTwice(() -> Stream.of("x", "y", "z")), 6L);
        that("7 plusOne leaves input untouched", () -> {
            List<Integer> in = new ArrayList<>(List.of(1, 2, 3));
            List<Integer> out = plusOne(in);
            return List.of(in, out);
        }, List.of(List.of(1, 2, 3), List.of(2, 3, 4)));
        Check.summary();
    }

    static void demo() {
        System.out.println("-- vertical processing with a short-circuit --");
        Stream.of("one", "two", "three", "four")
                .filter(s -> {
                    System.out.println("filter " + s);
                    return s.length() == 3;
                })
                .map(s -> {
                    System.out.println("  map " + s);
                    return s.toUpperCase();
                })
                .findFirst()
                .ifPresent(r -> System.out.println("result " + r));
        System.out.println("(\"three\" and \"four\" were never touched)");
    }

    // TODO 1: From the INFINITE stream 1,2,3,... return the first two squares greater than 100.
    // Hint: Stream.iterate(1, n -> n + 1).map(square).filter(> 100).limit(2)
    static List<Integer> firstTwoSquaresOver100() {
        throw Check.todo();
    }

    // TODO 2: Build Stream.of(1, 2, 3).peek(increment a counter) but do NOT call a terminal operation.
    // Return the counter. (Predict the answer first!)
    static int peekCallsWithoutTerminal() {
        throw Check.todo();
    }

    // TODO 3: List.of(1, 2, 3).stream().peek(increment counter).count()  -> return the counter.
    // Predict first: does peek run? (JDK 9+ can compute count() from the List's size.)
    static int peekCallsWithCountOnly() {
        throw Check.todo();
    }

    // TODO 3b: Same as above but insert .filter(x -> true) before .count().  Return the counter.
    // A filter makes the size unknown, so the pipeline must really run.
    static int peekCallsWithFilterThenCount() {
        throw Check.todo();
    }

    // TODO 4: Run Stream.of("a", "b") through  peek -> map -> toList  where
    //   peek adds "peek-<x>" to a log list and map adds "map-<x>" to the same log (then returns x).
    // Return the log. It shows vertical processing: [peek-a, map-a, peek-b, map-b].
    static List<String> verticalOrder() {
        throw Check.todo();
    }

    // TODO 5: Same as TODO 4 but over Stream.of("b", "a") with .sorted() between peek and map.
    // sorted() is a barrier: expected log [peek-b, peek-a, map-a, map-b].
    static List<String> sortedBarrierOrder() {
        throw Check.todo();
    }

    // TODO 6: Traverse the same data twice, which a single Stream cannot do: return the sum of
    // the two counts.  Hint: call supplier.get() once per traversal.
    static long countTwice(Supplier<Stream<String>> supplier) {
        throw Check.todo();
    }

    // TODO 7: Return a NEW list with every element + 1. The input list must not be modified
    // and you must NOT use forEach with side effects. Hint: map + toList
    static List<Integer> plusOne(List<Integer> in) {
        throw Check.todo();
    }
}
