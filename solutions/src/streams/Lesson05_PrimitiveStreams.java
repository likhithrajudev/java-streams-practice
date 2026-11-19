package streams;

import static streams.Check.that;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.IntStream;
import streams.Data.Employee;

/**
 * LESSON 05 - Primitive streams: IntStream, LongStream, DoubleStream
 *
 * <h2>Theory</h2>
 * {@code Stream<Integer>} boxes every number into an object (slow, memory heavy).
 * {@code IntStream} works on raw {@code int}s and adds numeric terminals:
 * <pre>
 *   sum()  average() -> OptionalDouble   min()/max() -> OptionalInt
 *   summaryStatistics() -> count, sum, min, max, average in ONE pass
 * </pre>
 * Converting between worlds:
 * <pre>
 *   Stream<T>  --mapToInt / mapToDouble / mapToLong-->  IntStream / DoubleStream / LongStream
 *   IntStream  --boxed()-->           Stream<Integer>
 *   IntStream  --mapToObj(i -> ..)--> Stream<R>
 *   IntStream  --asDoubleStream()-->  DoubleStream
 *   IntStream  --toArray()-->         int[]
 * </pre>
 * {@code IntStream.range(a, b)} excludes b; {@code rangeClosed(a, b)} includes it.
 * {@code average()} returns an OptionalDouble because an empty stream has no average.
 * Beware: {@code IntStream.sum()} overflows silently. Use {@code mapToLong} for big sums.
 *
 * Run:  ./run.sh 05        (answers: ./run.sh --solutions 05)
 */
public class Lesson05_PrimitiveStreams {

    public static void main(String[] args) {
        demo();

        Check.section("Lesson 05 - Primitive streams");
        that("1 sumOfSquares(4)", () -> sumOfSquares(4), 30);
        that("2 average", () -> average(new int[] {1, 2, 3, 4}), 2.5);
        that("2b average(empty)", () -> average(new int[] {}), 0.0);
        that("3 stats", () -> {
            IntSummaryStatistics s = stats(new int[] {2, 4, 9});
            return List.of(s.getMin(), s.getMax(), s.getSum(), s.getAverage());
        }, List.of(2, 9, 15L, 5.0));
        that("4 squares", () -> squares(new int[] {1, 2, 3}), new int[] {1, 4, 9});
        that("5 evensUpTo(7)", () -> evensUpTo(7), List.of(2, 4, 6));
        that("6 digitSum", () -> digitSum("a1b22c3"), 8);
        that("7 averageSalary(ENG)", () -> averageSalary("ENG"), 118_750.0);
        that("7b averageSalary(OPS)", () -> averageSalary("OPS"), 0.0);
        that("8 isPrime", () -> List.of(isPrime(1), isPrime(2), isPrime(15), isPrime(17)),
                List.of(false, true, false, true));
        that("9 primesUpTo(20)", () -> primesUpTo(20), List.of(2, 3, 5, 7, 11, 13, 17, 19));
        Check.summary();
    }

    static void demo() {
        System.out.println(IntStream.rangeClosed(1, 5).sum());
        System.out.println(IntStream.of(3, 1, 2).summaryStatistics());
        System.out.println(Arrays.toString(IntStream.range(0, 5).map(i -> i * i).toArray()));
    }

    // TODO 1: Sum of squares 1^2 + 2^2 + ... + n^2.  Hint: IntStream.rangeClosed + map + sum
    static int sumOfSquares(int n) {
        return IntStream.rangeClosed(1, n).map(x -> x * x).sum();
    }

    // TODO 2: Average of the array, 0.0 when it is empty.  Hint: Arrays.stream(a).average().orElse(0)
    static double average(int[] a) {
        return Arrays.stream(a).average().orElse(0);
    }

    // TODO 3: Min, max, sum and average in ONE pass.  Hint: summaryStatistics()
    static IntSummaryStatistics stats(int[] a) {
        return Arrays.stream(a).summaryStatistics();
    }

    // TODO 4: Square every element and return an int[]  (no boxing!).  Hint: map + toArray
    static int[] squares(int[] a) {
        return Arrays.stream(a).map(x -> x * x).toArray();
    }

    // TODO 5: Even numbers from 1..max as a List<Integer>.  Hint: filter + boxed + toList
    static List<Integer> evensUpTo(int max) {
        return IntStream.rangeClosed(1, max).filter(n -> n % 2 == 0).boxed().toList();
    }

    // TODO 6: Sum of the digit characters in a string: "a1b22c3" -> 1+2+2+3 = 8.
    // Hint: s.chars().filter(Character::isDigit).map(c -> c - '0').sum()
    static int digitSum(String s) {
        return s.chars().filter(Character::isDigit).map(c -> c - '0').sum();
    }

    // TODO 7: Average salary of a department, 0.0 if the department has nobody.
    // Hint: mapToDouble(Employee::salary).average().orElse(0)
    static double averageSalary(String dept) {
        return Data.employees().stream()
                .filter(e -> e.dept().equals(dept))
                .mapToDouble(Employee::salary)
                .average()
                .orElse(0);
    }

    // TODO 8: Primality test using a stream: n > 1 and no divisor in [2, sqrt(n)].
    // Hint: IntStream.rangeClosed(2, (int) Math.sqrt(n)).noneMatch(d -> n % d == 0)
    static boolean isPrime(int n) {
        return n > 1 && IntStream.rangeClosed(2, (int) Math.sqrt(n)).noneMatch(d -> n % d == 0);
    }

    // TODO 9: All primes from 2..n. Reuse isPrime (finish TODO 8 first).
    static List<Integer> primesUpTo(int n) {
        return IntStream.rangeClosed(2, n).filter(Lesson05_PrimitiveStreams::isPrime).boxed().toList();
    }
}
