package streams;

import static streams.Check.that;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * LESSON 18 - Capstone 2: a university gradebook
 *
 * <h2>Theory</h2>
 * Same method as lesson 12, new domain. A reminder of the three shapes of problem you will keep seeing:
 * <ol>
 *   <li><b>Per-student</b> question: stream {@code STUDENTS}, use each student's own {@code grades()} list.</li>
 *   <li><b>Per-course / per-grade</b> question: first FLATTEN to one row per (student, course, score) with
 *       {@code rows()}, then group or filter. Flattening is the key move: it turns a nested structure into a flat one.</li>
 *   <li><b>Cross-cutting</b> question ("who took both A and B"): group rows by student, then test the group.</li>
 * </ol>
 * Students without grades exist on purpose (Gus): decide what each question should do with them.
 *
 * <pre>
 *   id name major year  grades
 *   1  Ana  CS    2     algo 90, db 85, os 78            total 253
 *   2  Ben  CS    3     algo 70, db 65                   total 135
 *   3  Cleo MATH  2     calc 95, algo 88, stats 92       total 275
 *   4  Dan  MATH  1     calc 60, stats 72                total 132
 *   5  Eli  CS    1     algo 82, os 91                   total 173
 *   6  Fay  BIO   3     bio 88, stats 79, db 84          total 251
 *   7  Gus  BIO   2     (none)                           total 0
 * </pre>
 * Letter bands: A &gt;= 90, B &gt;= 80, C &gt;= 70, otherwise D. "Passing" means a score &gt;= 70.
 *
 * Run:  ./run.sh 18        (answers: ./run.sh --solutions 18)
 */
public class Lesson18_CapstoneStudents {

    record Grade(String course, int score) {}

    record Student(int id, String name, String major, int year, List<Grade> grades) {}

    /** One flattened row per grade. */
    record Row(String student, String major, String course, int score) {}

    static final List<Student> STUDENTS = List.of(
            new Student(1, "Ana", "CS", 2, List.of(new Grade("algo", 90), new Grade("db", 85), new Grade("os", 78))),
            new Student(2, "Ben", "CS", 3, List.of(new Grade("algo", 70), new Grade("db", 65))),
            new Student(3, "Cleo", "MATH", 2,
                    List.of(new Grade("calc", 95), new Grade("algo", 88), new Grade("stats", 92))),
            new Student(4, "Dan", "MATH", 1, List.of(new Grade("calc", 60), new Grade("stats", 72))),
            new Student(5, "Eli", "CS", 1, List.of(new Grade("algo", 82), new Grade("os", 91))),
            new Student(6, "Fay", "BIO", 3,
                    List.of(new Grade("bio", 88), new Grade("stats", 79), new Grade("db", 84))),
            new Student(7, "Gus", "BIO", 2, List.of()));

    // Helpers provided for you - reuse them.
    static Stream<Row> rows() {
        return STUDENTS.stream().flatMap(s -> s.grades().stream()
                .map(g -> new Row(s.name(), s.major(), g.course(), g.score())));
    }

    static int total(Student s) {
        return s.grades().stream().mapToInt(Grade::score).sum();
    }

    static OptionalDouble average(Student s) {
        return s.grades().stream().mapToInt(Grade::score).average();
    }

    static String letter(int score) {
        return score >= 90 ? "A" : score >= 80 ? "B" : score >= 70 ? "C" : "D";
    }

    public static void main(String[] args) {
        Check.section("Lesson 18 - Capstone: gradebook");
        that("1 totalByStudent (Gus = 0)", Lesson18_CapstoneStudents::totalByStudent, Map.of(
                "Ana", 253, "Ben", 135, "Cleo", 275, "Dan", 132, "Eli", 173, "Fay", 251, "Gus", 0));
        that("2 honorRoll (average >= 85)", Lesson18_CapstoneStudents::honorRoll, List.of("Cleo", "Eli"));
        that("3 bestAverageByMajor", Lesson18_CapstoneStudents::bestAverageByMajor,
                Map.of("CS", "Eli", "MATH", "Cleo", "BIO", "Fay"));
        that("4 courseAverages", Lesson18_CapstoneStudents::courseAverages, Map.of(
                "algo", 82.5, "db", 78.0, "os", 84.5, "calc", 77.5, "stats", 81.0, "bio", 88.0));
        that("5 enrollment", Lesson18_CapstoneStudents::enrollment,
                Map.of("algo", 4L, "db", 3L, "os", 2L, "calc", 2L, "stats", 3L, "bio", 1L));
        that("6 hardestCourse", Lesson18_CapstoneStudents::hardestCourse, "calc");
        that("7 takingBoth algo+db", () -> takingBoth("algo", "db"), List.of("Ana", "Ben"));
        that("8 topScorerPerCourse", Lesson18_CapstoneStudents::topScorerPerCourse, Map.of(
                "algo", "Ana", "db", "Ana", "os", "Eli", "calc", "Cleo", "stats", "Cleo", "bio", "Fay"));
        that("9 gradeDistribution", () -> gradeDistribution().toString(), "{A=4, B=5, C=4, D=2}");
        that("10 majorsByYear", Lesson18_CapstoneStudents::majorsByYear, Map.of(
                1, Set.of("CS", "MATH"), 2, Set.of("BIO", "CS", "MATH"), 3, Set.of("BIO", "CS")));
        that("11 report", Lesson18_CapstoneStudents::report, List.of(
                "Cleo: 275 pts, avg 91.7", "Ana: 253 pts, avg 84.3", "Fay: 251 pts, avg 83.7",
                "Eli: 173 pts, avg 86.5", "Ben: 135 pts, avg 67.5", "Dan: 132 pts, avg 66.0"));
        that("12 passRate", Lesson18_CapstoneStudents::passRate, "86.7%");
        Check.summary();
    }

    // TODO 1: student name -> total points. Students with no grades must appear with 0.
    // Hint: toMap(Student::name, Lesson18_CapstoneStudents::total)
    static Map<String, Integer> totalByStudent() {
        return STUDENTS.stream().collect(Collectors.toMap(Student::name, Lesson18_CapstoneStudents::total));
    }

    // TODO 2: names of students whose AVERAGE is >= 85, sorted alphabetically. No grades = not on the roll.
    // Hint: average(s).orElse(0) >= 85
    static List<String> honorRoll() {
        return STUDENTS.stream()
                .filter(s -> average(s).orElse(0) >= 85)
                .map(Student::name)
                .sorted()
                .toList();
    }

    // TODO 3: major -> NAME of the student with the best average (no grades counts as average 0).
    // Hint: groupingBy(major, collectingAndThen(maxBy(comparingDouble(..)), opt -> opt.orElseThrow().name()))
    static Map<String, String> bestAverageByMajor() {
        return STUDENTS.stream().collect(Collectors.groupingBy(
                Student::major,
                Collectors.collectingAndThen(
                        Collectors.maxBy(Comparator.comparingDouble(s -> average(s).orElse(0))),
                        opt -> opt.orElseThrow().name())));
    }

    // TODO 4: course -> average score across all students.   Hint: flatten with rows(), groupingBy(Row::course, averagingInt(..))
    static Map<String, Double> courseAverages() {
        return rows().collect(Collectors.groupingBy(Row::course, Collectors.averagingInt(Row::score)));
    }

    // TODO 5: course -> number of students enrolled (who have a grade in it).
    static Map<String, Long> enrollment() {
        return rows().collect(Collectors.groupingBy(Row::course, Collectors.counting()));
    }

    // TODO 6: the course with the LOWEST average.   Hint: reuse courseAverages(), then min over entries by value.
    static String hardestCourse() {
        return courseAverages().entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .orElseThrow()
                .getKey();
    }

    // TODO 7: names (sorted) of students who have a grade in BOTH courses.
    // Hint: keep rows whose course is a or b, groupingBy(Row::student, counting()), keep those with count 2.
    //       (Stuck? "who took both" = "appears twice among the matching rows".)
    static List<String> takingBoth(String a, String b) {
        return rows()
                .filter(r -> r.course().equals(a) || r.course().equals(b))
                .collect(Collectors.groupingBy(Row::student, Collectors.counting()))
                .entrySet().stream()
                .filter(e -> e.getValue() == 2)
                .map(Map.Entry::getKey)
                .sorted()
                .toList();
    }

    // TODO 8: course -> name of the student with the highest score in it.
    static Map<String, String> topScorerPerCourse() {
        return rows().collect(Collectors.groupingBy(
                Row::course,
                Collectors.collectingAndThen(
                        Collectors.maxBy(Comparator.comparingInt(Row::score)),
                        opt -> opt.orElseThrow().student())));
    }

    // TODO 9: how many grades fall in each letter band (use the provided letter(..)), keys sorted A..D.
    static TreeMap<String, Long> gradeDistribution() {
        return rows().collect(Collectors.groupingBy(
                r -> letter(r.score()), TreeMap::new, Collectors.counting()));
    }

    // TODO 10: year -> the set of majors present in that year (every student counts, even without grades).
    // Hint: groupingBy(Student::year, mapping(Student::major, toCollection(TreeSet::new)))
    static Map<Integer, Set<String>> majorsByYear() {
        return STUDENTS.stream().collect(Collectors.groupingBy(
                Student::year, Collectors.mapping(Student::major, Collectors.toCollection(TreeSet::new))));
    }

    // TODO 11: one line per student WITH grades, best total first: "Cleo: 275 pts, avg 91.7".
    // Hint: filter(!grades().isEmpty()), sorted(comparingInt(total).reversed()),
    //       String.format(Locale.ROOT, "%s: %d pts, avg %.1f", ...)
    static List<String> report() {
        return STUDENTS.stream()
                .filter(s -> !s.grades().isEmpty())
                .sorted(Comparator.comparingInt(Lesson18_CapstoneStudents::total).reversed())
                .map(s -> String.format(Locale.ROOT, "%s: %d pts, avg %.1f", s.name(), total(s), average(s).orElse(0)))
                .toList();
    }

    // TODO 12: percentage of ALL grades that are passing (>= 70), formatted "86.7%" - in ONE pass.
    // Hint: rows().map(Row::score).collect(teeing(filtering(s -> s >= 70, counting()), counting(),
    //           (pass, all) -> String.format(Locale.ROOT, "%.1f%%", 100.0 * pass / all)))
    static String passRate() {
        return rows().map(Row::score).collect(Collectors.teeing(
                Collectors.filtering(s -> s >= 70, Collectors.counting()),
                Collectors.counting(),
                (pass, all) -> String.format(Locale.ROOT, "%.1f%%", 100.0 * pass / all)));
    }
}
