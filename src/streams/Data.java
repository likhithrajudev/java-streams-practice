package streams;

import java.util.List;

/** Shared sample data used by several lessons. Do not change it: the expected values depend on it. */
public final class Data {
    private Data() {}

    public record Employee(String name, String dept, double salary, int age, List<String> skills) {}

    /*
     * ENG   : Alice 120000, Bob 95000, Carol 150000, Heidi 110000   (payroll 475000)
     * HR    : Dave   60000, Eve  72000                              (payroll 132000)
     * SALES : Frank  80000, Grace 91000                             (payroll 171000)
     */
    public static List<Employee> employees() {
        return List.of(
                new Employee("Alice", "ENG", 120_000, 34, List.of("java", "sql")),
                new Employee("Bob", "ENG", 95_000, 28, List.of("java", "react")),
                new Employee("Carol", "ENG", 150_000, 45, List.of("java", "kotlin", "aws")),
                new Employee("Dave", "HR", 60_000, 39, List.of("excel")),
                new Employee("Eve", "HR", 72_000, 31, List.of("excel", "sql")),
                new Employee("Frank", "SALES", 80_000, 26, List.of("crm")),
                new Employee("Grace", "SALES", 91_000, 41, List.of("crm", "sql")),
                new Employee("Heidi", "ENG", 110_000, 29, List.of("go", "aws")));
    }
}
