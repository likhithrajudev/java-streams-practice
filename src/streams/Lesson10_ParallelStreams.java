package streams;

import static streams.Check.that;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * LESSON 10 - Parallel streams: power and traps
 *
 * <h2>Theory</h2>
 * {@code .parallel()} (or {@code collection.parallelStream()}) splits the source into chunks that run on the
 * JVM-wide <b>ForkJoinPool.commonPool()</b> and merges the results. It is a one-word change, but:
 * <ul>
 *   <li><b>It is not automatically faster.</b> Splitting + merging has overhead. It only pays off for
 *       CPU-bound work, large data (think 10k+ elements with non-trivial work per element) and sources
 *       that split well (ArrayList, arrays, ranges - NOT LinkedList or Stream.iterate).</li>
 *   <li><b>Never use it for blocking I/O</b> (HTTP, JDBC): it starves the shared common pool for the whole
 *       application. Use an executor / virtual threads instead.</li>
 *   <li><b>Lambdas must be stateless, non-interfering and thread-safe.</b> Mutating a shared ArrayList or
 *       counter from a parallel stream is a data race.</li>
 *   <li><b>reduce operators must be associative</b> and identities neutral (lesson 08).</li>
 *   <li><b>Encounter order</b>: toList()/collect(toList()) keep order. forEach does NOT; use
 *       forEachOrdered if order matters (it costs parallelism).</li>
 *   <li><b>Measure.</b> Use JMH, not a single System.nanoTime, for real decisions.</li>
 * </ul>
 * Prefer {@code collect} with thread-safe collectors, or {@code groupingByConcurrent}, over manual locking.
 *
 * Run:  ./run.sh 10        (answers: ./run.sh --solutions 10)
 */
public class Lesson10_ParallelStreams {

    public static void main(String[] args) {
        demo();

        Check.section("Lesson 10 - Parallel streams");
        that("1 parallelSum(1000)", () -> parallelSum(1000), 500_500);
        that("2 parallelCollect keeps order", () -> parallelCollect(10_000).subList(0, 5), List.of(0, 1, 2, 3, 4));
        that("2b parallelCollect size", () -> parallelCollect(10_000).size(), 10_000);
        that("3 countEvensParallel", () -> countEvensParallel(100_000), 50_000L);
        that("4 orderedJoin", () -> orderedJoin(List.of("a", "b", "c", "d", "e")), "abcde");
        that("5 countByLength", () -> countByLength(List.of("a", "bb", "cc", "d")), java.util.Map.of(1, 2L, 2, 2L));
        that("6 isParallelPipeline", () -> isParallelPipeline(), List.of(false, true, false));
        Check.summary();
    }

    static void demo() {
        int limit = 300_000;
        long t0 = System.nanoTime();
        long seq = IntStream.rangeClosed(2, limit).filter(Lesson10_ParallelStreams::slowIsPrime).count();
        long t1 = System.nanoTime();
        long par = IntStream.rangeClosed(2, limit).parallel().filter(Lesson10_ParallelStreams::slowIsPrime).count();
        long t2 = System.nanoTime();
        System.out.printf("primes<=%d  sequential=%d ms, parallel=%d ms (same result: %b, cores=%d)%n",
                limit, (t1 - t0) / 1_000_000, (t2 - t1) / 1_000_000, seq == par,
                Runtime.getRuntime().availableProcessors());

        // The classic bug: a shared, non-thread-safe list mutated from a parallel stream.
        // We only show it, we do not assert on it, because the outcome is random.
        List<Integer> broken = new java.util.ArrayList<>();
        try {
            IntStream.range(0, 10_000).parallel().forEach(broken::add);
            System.out.println("BROKEN list size (expected 10000): " + broken.size());
        } catch (RuntimeException e) {
            System.out.println("BROKEN list threw: " + e);
        }
    }

    static boolean slowIsPrime(int n) {
        for (int d = 2; (long) d * d <= n; d++) {
            if (n % d == 0) {
                return false;
            }
        }
        return true;
    }

    // TODO 1: Sum 1..n using a parallel IntStream.  Hint: IntStream.rangeClosed(1, n).parallel().sum()
    static int parallelSum(int n) {
        throw Check.todo();
    }

    // TODO 2: Return the numbers 0..n-1 as a List<Integer>, built from a PARALLEL stream, using
    // collect/toList - NOT forEach(list::add). Order must be preserved.
    static List<Integer> parallelCollect(int n) {
        throw Check.todo();
    }

    // TODO 3: Count the even numbers in 0..n-1 in parallel.  Return a long.
    static long countEvensParallel(int n) {
        throw Check.todo();
    }

    // TODO 4: Process a parallel stream but append each element to a StringBuilder IN ORDER.
    // Hint: in.parallelStream().forEachOrdered(sb::append). Return sb.toString().
    static String orderedJoin(List<String> in) {
        throw Check.todo();
    }

    // TODO 5: Group words by length and count them, using a parallel stream and the concurrent collector.
    // Hint: Collectors.groupingByConcurrent(String::length, Collectors.counting())
    static ConcurrentMap<Integer, Long> countByLength(List<String> words) {
        throw Check.todo();
    }

    // TODO 6: Return a list of three booleans using Stream.of(1, 2, 3):
    //   [ stream.isParallel(),  stream.parallel().isParallel(),  stream.parallel().sequential().isParallel() ]
    // Use a FRESH stream for each (streams are single use only when you run a terminal op, but
    // parallel()/sequential() return the same pipeline and flip ONE flag for the whole pipeline).
    // Expected: [false, true, false]
    static List<Boolean> isParallelPipeline() {
        throw Check.todo();
    }
}
