package streams;

import static streams.Check.that;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import streams.Data.Employee;

/**
 * LESSON 07 - Collectors, part 2: groupingBy, partitioningBy and downstream collectors
 *
 * <h2>Theory</h2>
 * <pre>
 *   groupingBy(classifier)                          Map<K, List<T>>
 *   groupingBy(classifier, downstream)              Map<K, whatever downstream makes>
 *   groupingBy(classifier, mapFactory, downstream)  choose HashMap / TreeMap / LinkedHashMap
 *   partitioningBy(predicate[, downstream])         Map<Boolean, ...> with BOTH keys always present
 * </pre>
 * A <b>downstream collector</b> processes the elements of each group:
 * <pre>
 *   counting()                      mapping(fn, downstream)          filtering(pred, downstream)
 *   summingX / averagingX           flatMapping(fn, downstream)      collectingAndThen(c, finisher)
 *   minBy / maxBy                   teeing(c1, c2, merger)           (two collectors in one pass)
 * </pre>
 * Mental model: groupingBy is SQL's {@code GROUP BY}; the downstream collector is the aggregate function.
 * Groups can be nested: {@code groupingBy(a, groupingBy(b, counting()))}.
 *
 * Run:  ./run.sh 07        (answers: ./run.sh --solutions 07)
 */
public class Lesson07_Grouping {

    public static void main(String[] args) {
        demo();

        Check.section("Lesson 07 - Grouping and partitioning");
        that("1 namesByDept", Lesson07_Grouping::namesByDept, Map.of(
                "ENG", List.of("Alice", "Bob", "Carol", "Heidi"),
                "HR", List.of("Dave", "Eve"),
                "SALES", List.of("Frank", "Grace")));
        that("2 headcountByDept", Lesson07_Grouping::headcountByDept, Map.of("ENG", 4L, "HR", 2L, "SALES", 2L));
        that("3 avgSalaryByDept", Lesson07_Grouping::avgSalaryByDept,
                Map.of("ENG", 118_750.0, "HR", 66_000.0, "SALES", 85_500.0));
        that("4 topEarnerByDept", Lesson07_Grouping::topEarnerByDept,
                Map.of("ENG", "Carol", "HR", "Eve", "SALES", "Grace"));
        that("5 partitionBySalary", Lesson07_Grouping::partitionBySalary, Map.of(
                true, List.of("Alice", "Carol", "Heidi"),
                false, List.of("Bob", "Dave", "Eve", "Frank", "Grace")));
        that("6 skillsByDept", Lesson07_Grouping::skillsByDept, Map.of(
                "ENG", Set.of("java", "sql", "react", "kotlin", "aws", "go"),
                "HR", Set.of("excel", "sql"),
                "SALES", Set.of("crm", "sql")));
        that("7 payrollByDept (sorted keys)", () -> payrollByDept().toString(),
                "{ENG=475000.0, HR=132000.0, SALES=171000.0}");
        that("8 skillFrequency", Lesson07_Grouping::skillFrequency, Map.of(
                "java", 3L, "sql", 3L, "aws", 2L, "excel", 2L, "crm", 2L, "react", 1L, "kotlin", 1L, "go", 1L));
        that("9 salarySpread", Lesson07_Grouping::salarySpread, 90_000.0);
        that("10 seniorCountByDept", Lesson07_Grouping::seniorCountByDept, Map.of(
                "ENG", Map.of(true, 1L, false, 3L),
                "HR", Map.of(true, 1L, false, 1L),
                "SALES", Map.of(true, 1L, false, 1L)));
        Check.summary();
    }

    static void demo() {
        System.out.println(Data.employees().stream()
                .collect(Collectors.groupingBy(Employee::dept, Collectors.counting())));
        System.out.println(Data.employees().stream()
                .collect(Collectors.partitioningBy(e -> e.age() >= 35, Collectors.counting())));
    }

    // TODO 1: dept -> names of its employees (in original order).
    // Hint: groupingBy(Employee::dept, mapping(Employee::name, toList()))
    static Map<String, List<String>> namesByDept() {
        throw Check.todo();
    }

    // TODO 2: dept -> number of employees.  Hint: counting() returns Long.
    static Map<String, Long> headcountByDept() {
        throw Check.todo();
    }

    // TODO 3: dept -> average salary.  Hint: averagingDouble
    static Map<String, Double> avgSalaryByDept() {
        throw Check.todo();
    }

    // TODO 4: dept -> NAME of the top earner.
    // Hint: collectingAndThen(maxBy(comparingDouble(..)), opt -> opt.orElseThrow().name())
    static Map<String, String> topEarnerByDept() {
        throw Check.todo();
    }

    // TODO 5: Split names into earning >= 100_000 (true) and the rest (false).
    // Hint: partitioningBy(pred, mapping(Employee::name, toList()))
    static Map<Boolean, List<String>> partitionBySalary() {
        throw Check.todo();
    }

    // TODO 6: dept -> set of all skills in that dept.
    // Hint: flatMapping(e -> e.skills().stream(), toSet())
    static Map<String, Set<String>> skillsByDept() {
        throw Check.todo();
    }

    // TODO 7: dept -> total payroll, with the keys SORTED alphabetically.
    // Hint: the 3-arg groupingBy(classifier, TreeMap::new, summingDouble(...))
    static TreeMap<String, Double> payrollByDept() {
        throw Check.todo();
    }

    // TODO 8: skill -> how many employees have it.
    // Hint: flatMap the skills first, then groupingBy(Function.identity(), counting())
    static Map<String, Long> skillFrequency() {
        throw Check.todo();
    }

    // TODO 9: highest salary minus lowest salary, computed in ONE pass over the stream.
    // Hint: Collectors.teeing(maxBy(..), minBy(..), (max, min) -> ...)
    static double salarySpread() {
        throw Check.todo();
    }

    // TODO 10: Nested! dept -> (isSenior (age >= 35) -> count).
    // Hint: groupingBy(dept, partitioningBy(age >= 35, counting()))
    static Map<String, Map<Boolean, Long>> seniorCountByDept() {
        throw Check.todo();
    }
}
