package streams;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Tiny self-check helper so the course needs no test framework.
 *
 * <pre>
 *   ✅ PASS  your code returned the expected value
 *   ❌ FAIL  wrong value or an exception
 *   ⬜ TODO  you have not implemented it yet (the stub throws Check.todo())
 * </pre>
 */
public final class Check {
    private static int passed;
    private static int failed;
    private static int todo;

    private static final class NotImplemented extends RuntimeException {
        private static final long serialVersionUID = 1L;

        NotImplemented() {
            super("TODO", null, false, false);
        }
    }

    private Check() {}

    /** Body of every unfinished exercise. Replace it with your solution. */
    public static RuntimeException todo() {
        return new NotImplemented();
    }

    public static void section(String title) {
        System.out.println("\n=== " + title + " ===");
    }

    public static void that(String name, Supplier<?> actual, Object expected) {
        try {
            Object a = actual.get();
            if (Objects.deepEquals(a, expected)) {
                passed++;
                System.out.println("  ✅ PASS  " + name);
            } else {
                failed++;
                System.out.println("  ❌ FAIL  " + name);
                System.out.println("           expected: " + show(expected));
                System.out.println("           actual:   " + show(a));
            }
        } catch (NotImplemented e) {
            todo++;
            System.out.println("  ⬜ TODO  " + name);
        } catch (Throwable t) {
            failed++;
            System.out.println("  ❌ FAIL  " + name + "  (threw " + t + ")");
        }
    }

    public static void summary() {
        System.out.printf("%nResult: %d passed, %d failed, %d todo%n", passed, failed, todo);
        if (failed == 0 && todo == 0) {
            System.out.println("🎉 Lesson complete - move on to the next one!");
        }
    }

    private static String show(Object o) {
        if (o != null && o.getClass().isArray()) {
            return Arrays.deepToString(new Object[] {o}).replaceAll("^\\[|\\]$", "");
        }
        return String.valueOf(o);
    }
}
