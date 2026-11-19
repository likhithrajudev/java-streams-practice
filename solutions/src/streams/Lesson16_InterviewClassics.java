package streams;

import static streams.Check.that;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * LESSON 16 - Interview classics, the stream way
 *
 * <h2>Theory</h2>
 * These are the small puzzles that show up again and again in interviews and code reviews. For each one there is
 * usually a "loop" answer and a "stream" answer; the point is to know when the stream version is shorter
 * AND clearer (and to say so out loud when it isn't).
 * <pre>
 *   index-based problems      IntStream.range(0, n)  then use arr[i] / list.get(i)
 *   "does any pair ..."       nested IntStream.range + flatMap
 *   counting / duplicates     groupingBy(identity(), counting())
 *   "second largest" etc.     distinct() + sorted(reverseOrder()) + skip(1) + findFirst()
 *   math                      IntStream/LongStream.rangeClosed(..).reduce / filter / noneMatch
 * </pre>
 * Stateful lambdas (a counter, a "seen" Set filled inside {@code filter}) can look like a shortcut but break under
 * {@code parallel()} and hide bugs. Prefer {@code groupingBy} or {@code distinct}.
 *
 * Run:  ./run.sh 16        (answers: ./run.sh --solutions 16)
 */
public class Lesson16_InterviewClassics {

    public static void main(String[] args) {
        demo();

        Check.section("Lesson 16 - Interview classics");
        that("1 fizzBuzz", () -> fizzBuzz(15), List.of(
                "1", "2", "Fizz", "4", "Buzz", "Fizz", "7", "8", "Fizz", "Buzz", "11", "Fizz", "13", "14", "FizzBuzz"));
        that("2 isPrime", () -> List.of(isPrime(1), isPrime(2), isPrime(15), isPrime(17)),
                List.of(false, true, false, true));
        that("3 primesUpTo", () -> primesUpTo(30), List.of(2, 3, 5, 7, 11, 13, 17, 19, 23, 29));
        that("4 secondHighest", () -> List.of(secondHighest(new int[] {5, 3, 9, 9, 1, 7}), secondHighest(new int[] {4, 4})),
                List.of(OptionalInt.of(7), OptionalInt.empty()));
        that("5 findDuplicates", () -> findDuplicates(List.of(1, 2, 3, 2, 4, 3, 5, 2)), Set.of(2, 3));
        that("6 twoSumIndexPairs", () -> twoSumIndexPairs(new int[] {2, 7, 11, 4, 5, 5}, 9),
                List.of(List.of(0, 1), List.of(3, 4), List.of(3, 5)));
        that("7 missingNumber", () -> missingNumber(new int[] {3, 0, 1, 5, 2}), 4);
        that("8 prefixSums", () -> prefixSums(new int[] {1, 2, 3, 4}), new int[] {1, 3, 6, 10});
        that("9 reversed", () -> reversed(List.of("a", "b", "c")), List.of("c", "b", "a"));
        that("10 factorial", () -> factorial(10), 3_628_800L);
        that("11 digitSum", () -> digitSum(9875), 29);
        that("12 mostFrequentSmallest", () -> mostFrequentSmallest(new int[] {4, 1, 2, 2, 3, 3}), 2);
        that("13 pascalRow", () -> pascalRow(5), List.of(1L, 5L, 10L, 10L, 5L, 1L));
        Check.summary();
    }

    static void demo() {
        System.out.println("squares of evens: " + IntStream.rangeClosed(1, 10).filter(i -> i % 2 == 0).map(i -> i * i).boxed().toList());
        System.out.println("index trick:      " + IntStream.range(0, 3).mapToObj(i -> i + ":" + "xyz".charAt(i)).toList());
        System.out.println("nested ranges:    " + IntStream.rangeClosed(1, 3).boxed()
                .flatMap(i -> IntStream.rangeClosed(i, 3).mapToObj(j -> i + "," + j)).toList());
    }

    // TODO 1: numbers 1..n; multiples of 3 -> "Fizz", of 5 -> "Buzz", of both -> "FizzBuzz", otherwise the number.
    // Hint: IntStream.rangeClosed(1, n).mapToObj(i -> ... nested ternaries or a small helper ...)
    static List<String> fizzBuzz(int n) {
        return IntStream.rangeClosed(1, n)
                .mapToObj(i -> i % 15 == 0 ? "FizzBuzz" : i % 3 == 0 ? "Fizz" : i % 5 == 0 ? "Buzz" : String.valueOf(i))
                .toList();
    }

    // TODO 2: is n prime? (1 is not.) Only test divisors up to sqrt(n).
    // Hint: n > 1 && IntStream.rangeClosed(2, (int) Math.sqrt(n)).noneMatch(d -> n % d == 0)
    static boolean isPrime(int n) {
        return n > 1 && IntStream.rangeClosed(2, (int) Math.sqrt(n)).noneMatch(d -> n % d == 0);
    }

    // TODO 3: all primes <= n.   Hint: IntStream.rangeClosed(2, n).filter(Lesson16_InterviewClassics::isPrime).boxed()
    static List<Integer> primesUpTo(int n) {
        return IntStream.rangeClosed(2, n).filter(Lesson16_InterviewClassics::isPrime).boxed().toList();
    }

    // TODO 4: second-largest DISTINCT value, or empty if there isn't one ({5,3,9,9,1,7} -> 7; {4,4} -> empty).
    // Hint: distinct(), boxed(), sorted(Comparator.reverseOrder()), skip(1), mapToInt(i -> i), findFirst()
    static OptionalInt secondHighest(int[] a) {
        return Arrays.stream(a).distinct().boxed()
                .sorted(Comparator.reverseOrder())
                .skip(1)
                .mapToInt(i -> i)
                .findFirst();
    }

    // TODO 5: the set of values that occur more than once.
    // Hint: groupingBy(identity(), counting()), then stream the entries, filter count > 1, map to key, toSet()
    static Set<Integer> findDuplicates(List<Integer> xs) {
        return xs.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    // TODO 6: all index pairs [i, j] with i < j and nums[i] + nums[j] == target, ordered by i then j.
    // Hint: IntStream.range(0, n).boxed().flatMap(i -> IntStream.range(i + 1, n).filter(j -> ..).mapToObj(j -> List.of(i, j)))
    static List<List<Integer>> twoSumIndexPairs(int[] nums, int target) {
        return IntStream.range(0, nums.length).boxed()
                .flatMap(i -> IntStream.range(i + 1, nums.length)
                        .filter(j -> nums[i] + nums[j] == target)
                        .mapToObj(j -> List.of(i, j)))
                .toList();
    }

    // TODO 7: the array holds the numbers 0..n except ONE missing; find it ({3,0,1,5,2} -> 4).
    // Hint: expected sum n(n+1)/2 minus Arrays.stream(a).sum()
    static int missingNumber(int[] a) {
        int n = a.length;
        return n * (n + 1) / 2 - Arrays.stream(a).sum();
    }

    // TODO 8: running totals: {1,2,3,4} -> {1,3,6,10}.
    // Hint: IntStream.rangeClosed(1, a.length).map(k -> Arrays.stream(a).limit(k).sum()).toArray()
    //       (O(n^2), fine here. Streams have no built-in "scan"; you'd use a loop or Arrays.parallelPrefix for big data.)
    static int[] prefixSums(int[] a) {
        return IntStream.rangeClosed(1, a.length).map(k -> Arrays.stream(a).limit(k).sum()).toArray();
    }

    // TODO 9: reverse a list into a new list without Collections.reverse.
    // Hint: IntStream.range(0, n).mapToObj(i -> list.get(n - 1 - i)).toList()
    static List<String> reversed(List<String> list) {
        int n = list.size();
        return IntStream.range(0, n).mapToObj(i -> list.get(n - 1 - i)).toList();
    }

    // TODO 10: n! as a long.   Hint: LongStream.rangeClosed(1, n).reduce(1L, (a, b) -> a * b)
    static long factorial(int n) {
        return LongStream.rangeClosed(1, n).reduce(1L, (a, b) -> a * b);
    }

    // TODO 11: sum of the decimal digits of n (9875 -> 29).   Hint: String.valueOf(n).chars().map(c -> c - '0').sum()
    static int digitSum(int n) {
        return String.valueOf(n).chars().map(c -> c - '0').sum();
    }

    // TODO 12: the most frequent value; on a tie, the SMALLEST value ({4,1,2,2,3,3} -> 2).
    // Hint: groupingBy(identity(), counting()) then max over entries with
    //       Map.Entry.<Integer, Long>comparingByValue().thenComparing(Map.Entry.comparingByKey(Comparator.reverseOrder()))
    //       (a smaller key must count as "greater", hence reverseOrder on the key)
    static int mostFrequentSmallest(int[] a) {
        return Arrays.stream(a).boxed()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.<Integer, Long>comparingByValue()
                        .thenComparing(Map.Entry.comparingByKey(Comparator.reverseOrder())))
                .orElseThrow()
                .getKey();
    }

    // TODO 13: row n of Pascal's triangle: n = 5 -> [1, 5, 10, 10, 5, 1].
    // Hint: C(n, k+1) = C(n, k) * (n - k) / (k + 1). Iterate over pairs {k, C(n, k)} starting at {0, 1},
    //       limit(n + 1), then map to the second component.
    static List<Long> pascalRow(int n) {
        return Stream.iterate(new long[] {0, 1}, p -> new long[] {p[0] + 1, p[1] * (n - p[0]) / (p[0] + 1)})
                .limit(n + 1L)
                .map(p -> p[1])
                .toList();
    }
}
