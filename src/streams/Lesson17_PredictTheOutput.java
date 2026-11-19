package streams;

import static streams.Check.that;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * LESSON 17 - Predict the output (a quiz)
 *
 * <h2>How this lesson works</h2>
 * Each question has a pipeline {@code qN()} that really runs. Read it, <b>work out the result in your head
 * (or on paper) BEFORE running anything</b>, and type your prediction as the return value of {@code aN()}.
 * The checker runs the real pipeline and compares it with your answer.
 * <p>
 * Do not just run the file and copy the "expected" value from a FAIL message: that teaches nothing.
 * Predicting execution order is the single best way to build a correct mental model of streams.
 * <p>
 * Concepts tested: vertical (element-by-element) processing, barriers like {@code sorted}, short-circuiting,
 * {@code count()} shortcuts, reuse errors, HashMap ordering, empty-stream collectors, null hostility,
 * immutability of {@code Stream.toList()}, {@code reduce} identities, {@code takeWhile}/{@code dropWhile} vs {@code filter}.
 *
 * Run:  ./run.sh 17        (answers: ./run.sh --solutions 17)
 */
public class Lesson17_PredictTheOutput {

    public static void main(String[] args) {
        Check.section("Lesson 17 - Predict the output");
        that("1  vertical processing", Lesson17_PredictTheOutput::a1, q1());
        that("2  sorted is a barrier", Lesson17_PredictTheOutput::a2, q2());
        that("3  short-circuit", Lesson17_PredictTheOutput::a3, q3());
        that("4  count() shortcut (sizes of both logs)", Lesson17_PredictTheOutput::a4, q4());
        that("5  infinite + limit", Lesson17_PredictTheOutput::a5, q5());
        that("6  toMap duplicates", Lesson17_PredictTheOutput::a6, q6());
        that("7  reusing a stream", Lesson17_PredictTheOutput::a7, q7());
        that("8  groupingBy order", Lesson17_PredictTheOutput::a8, q8());
        that("9  empty collectors", Lesson17_PredictTheOutput::a9, q9());
        that("10 flatMap distinct sorted", Lesson17_PredictTheOutput::a10, q10());
        that("11 reduce identity", Lesson17_PredictTheOutput::a11, q11());
        that("12 takeWhile/dropWhile/filter", Lesson17_PredictTheOutput::a12, q12());
        that("13 nulls and map", Lesson17_PredictTheOutput::a13, q13());
        that("14 toList() vs collect(toList())", Lesson17_PredictTheOutput::a14, q14());
        Check.summary();
    }

    // ---------------------------------------------------------------- Q1
    // What is the log, in order?
    static List<String> q1() {
        List<String> log = new ArrayList<>();
        Stream.of("a", "b", "c")
                .peek(s -> log.add("p:" + s))
                .map(String::toUpperCase)
                .peek(s -> log.add("m:" + s))
                .forEach(s -> log.add("f:" + s));
        return log;
    }

    // TODO 1: return the list q1() will return, e.g. List.of("p:a", ...)
    static List<String> a1() {
        throw Check.todo();
    }

    // ---------------------------------------------------------------- Q2
    // sorted() needs to see everything before it can emit anything.
    static List<String> q2() {
        List<String> log = new ArrayList<>();
        Stream.of(3, 1, 2)
                .peek(x -> log.add("a" + x))
                .sorted()
                .peek(x -> log.add("b" + x))
                .forEach(x -> { });
        return log;
    }

    // TODO 2: the log of q2()
    static List<String> a2() {
        throw Check.todo();
    }

    // ---------------------------------------------------------------- Q3
    // findFirst() is short-circuiting. How far does the stream get?
    static List<String> q3() {
        List<String> log = new ArrayList<>();
        Stream.of(1, 2, 3, 4, 5)
                .peek(x -> log.add("p" + x))
                .filter(x -> x % 2 == 0)
                .findFirst();
        return log;
    }

    // TODO 3: the log of q3()
    static List<String> a3() {
        throw Check.todo();
    }

    // ---------------------------------------------------------------- Q4
    // Since JDK 9, count() may skip the pipeline when the size is known up front.
    // (This is an implementation detail: never rely on peek() for side effects that must happen.)
    static List<Integer> q4() {
        List<Integer> logA = new ArrayList<>();
        List<Integer> logB = new ArrayList<>();
        long countA = List.of(1, 2, 3).stream().peek(logA::add).count();
        long countB = List.of(1, 2, 3).stream().filter(x -> true).peek(logB::add).count();
        // both counts are 3. How many times did each peek run?
        return List.of(logA.size(), logB.size());
    }

    // TODO 4: [sizeOfLogA, sizeOfLogB]
    static List<Integer> a4() {
        throw Check.todo();
    }

    // ---------------------------------------------------------------- Q5
    static List<Integer> q5() {
        return Stream.iterate(1, x -> x + 1)
                .map(x -> x * x)
                .filter(x -> x % 2 == 0)
                .limit(3)
                .toList();
    }

    // TODO 5: the list q5() returns
    static List<Integer> a5() {
        throw Check.todo();
    }

    // ---------------------------------------------------------------- Q6
    // Which exception (simple class name) is thrown?
    static String q6() {
        try {
            Stream.of("a", "b", "a").collect(Collectors.toMap(s -> s, s -> 1));
            return "no exception";
        } catch (RuntimeException e) {
            return e.getClass().getSimpleName();
        }
    }

    // TODO 6: the String q6() returns
    static String a6() {
        throw Check.todo();
    }

    // ---------------------------------------------------------------- Q7
    // A second terminal operation on the same stream. What is the exception MESSAGE?
    static String q7() {
        Stream<Integer> s = Stream.of(1, 2);
        s.count();
        try {
            s.count();
            return "no exception";
        } catch (IllegalStateException e) {
            return e.getMessage();
        }
    }

    // TODO 7: the String q7() returns (word for word)
    static String a7() {
        throw Check.todo();
    }

    // ---------------------------------------------------------------- Q8
    static String q8() {
        return Stream.of("banana", "apple", "cherry")
                .collect(Collectors.groupingBy(String::length))
                .toString();
    }

    // TODO 8: the String q8() returns. Think: which Map implementation does groupingBy use, and how does it order small Integer keys?
    static String a8() {
        throw Check.todo();
    }

    // ---------------------------------------------------------------- Q9
    // Two collectors over an EMPTY stream. Which keys exist?
    static List<String> q9() {
        String partition = Stream.<Integer>empty().collect(Collectors.partitioningBy(x -> x > 0)).toString();
        String grouping = Stream.<Integer>empty().collect(Collectors.groupingBy(x -> x > 0)).toString();
        return List.of(partition, grouping);
    }

    // TODO 9: [partitionString, groupingString]
    static List<String> a9() {
        throw Check.todo();
    }

    // ---------------------------------------------------------------- Q10
    static List<Integer> q10() {
        return Stream.of(List.of(3, 1), List.of(2, 3))
                .flatMap(List::stream)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    // TODO 10: the list q10() returns
    static List<Integer> a10() {
        throw Check.todo();
    }

    // ---------------------------------------------------------------- Q11
    // Subtraction is NOT associative; a sequential stream just happens to fold left to right.
    static List<Integer> q11() {
        int plus = Stream.of(1, 2, 3, 4).reduce(10, (a, b) -> a + b);
        int minus = Stream.of(1, 2, 3, 4).reduce(0, (a, b) -> a - b);
        return List.of(plus, minus);
    }

    // TODO 11: [plus, minus]
    static List<Integer> a11() {
        throw Check.todo();
    }

    // ---------------------------------------------------------------- Q12
    static List<List<Integer>> q12() {
        List<Integer> data = List.of(1, 2, 3, 10, 4, 5);
        List<Integer> take = data.stream().takeWhile(x -> x < 5).toList();
        List<Integer> drop = data.stream().dropWhile(x -> x < 5).toList();
        List<Integer> filter = data.stream().filter(x -> x < 5).toList();
        return List.of(take, drop, filter);
    }

    // TODO 12: [takeWhile result, dropWhile result, filter result]
    static List<List<Integer>> a12() {
        throw Check.todo();
    }

    // ---------------------------------------------------------------- Q13
    // A null sits in the middle of the stream. Which exception (simple name) comes out?
    static String q13() {
        try {
            Stream.of("a", null, "c").map(String::toUpperCase).toList();
            return "no exception";
        } catch (RuntimeException e) {
            return e.getClass().getSimpleName();
        }
    }

    // TODO 13: the String q13() returns
    static String a13() {
        throw Check.todo();
    }

    // ---------------------------------------------------------------- Q14
    // Both lists are then modified with add(..). What happens in each case?
    static List<String> q14() {
        String fromToList;
        try {
            Stream.of(1, 2).toList().add(3);
            fromToList = "ok";
        } catch (RuntimeException e) {
            fromToList = e.getClass().getSimpleName();
        }
        String fromCollect;
        try {
            Stream.of(1, 2).collect(Collectors.toList()).add(3);
            fromCollect = "ok";
        } catch (RuntimeException e) {
            fromCollect = e.getClass().getSimpleName();
        }
        return List.of(fromToList, fromCollect);
    }

    // TODO 14: [result for Stream.toList(), result for collect(Collectors.toList())]
    static List<String> a14() {
        throw Check.todo();
    }
}
