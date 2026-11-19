package streams;

import static streams.Check.that;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * LESSON 12 - Capstone: order analytics (everything combined)
 *
 * <h2>Theory</h2>
 * Real work is a pipeline of 3-6 steps. A reliable way to design one:
 * <ol>
 *   <li>Say the question in plain words ("revenue per customer, ignoring cancelled orders").</li>
 *   <li>Pick the <b>source</b> and the <b>shape</b> you need at the end (a number? a Map? a List?).</li>
 *   <li>Filter early, flatten when you need the inner items, then group/aggregate.</li>
 *   <li>Extract repeated lambdas into small named methods ({@code total(order)}, {@code isActive(order)}).</li>
 * </ol>
 * "Active" order below means status != CANCELLED.  An order's total = sum(qty * unitPrice) of its lines.
 *
 * <pre>
 *   id  customer  status     lines                                    total
 *   1   alice     PAID       laptop 1 @1000, mouse 2 @25               1050
 *   2   bob       PAID       keyboard 1 @75, mouse 1 @25                100
 *   3   alice     CANCELLED  monitor 2 @200                             400
 *   4   carol     PAID       laptop 2 @1000                            2000
 *   5   bob       SHIPPED    monitor 1 @200, keyboard 2 @75             350
 *   6   dave      PAID       mouse 4 @25                                100
 *   7   carol     SHIPPED    keyboard 1 @75                              75
 * </pre>
 *
 * Run:  ./run.sh 12        (answers: ./run.sh --solutions 12)
 */
public class Lesson12_CapstoneOrders {

    record Line(String product, int qty, double unitPrice) {
        double amount() {
            return qty * unitPrice;
        }
    }

    record Order(int id, String customer, String status, List<Line> lines) {}

    static final List<Order> ORDERS = List.of(
            new Order(1, "alice", "PAID", List.of(new Line("laptop", 1, 1000), new Line("mouse", 2, 25))),
            new Order(2, "bob", "PAID", List.of(new Line("keyboard", 1, 75), new Line("mouse", 1, 25))),
            new Order(3, "alice", "CANCELLED", List.of(new Line("monitor", 2, 200))),
            new Order(4, "carol", "PAID", List.of(new Line("laptop", 2, 1000))),
            new Order(5, "bob", "SHIPPED", List.of(new Line("monitor", 1, 200), new Line("keyboard", 2, 75))),
            new Order(6, "dave", "PAID", List.of(new Line("mouse", 4, 25))),
            new Order(7, "carol", "SHIPPED", List.of(new Line("keyboard", 1, 75))));

    // Helpers provided for you - reuse them.
    static boolean isActive(Order o) {
        return !o.status().equals("CANCELLED");
    }

    static double total(Order o) {
        return o.lines().stream().mapToDouble(Line::amount).sum();
    }

    public static void main(String[] args) {
        Check.section("Lesson 12 - Capstone: order analytics");
        that("1 revenue", Lesson12_CapstoneOrders::revenue, 3675.0);
        that("2 spendByCustomer", Lesson12_CapstoneOrders::spendByCustomer,
                Map.of("alice", 1050.0, "bob", 450.0, "carol", 2075.0, "dave", 100.0));
        that("3 bestCustomer", Lesson12_CapstoneOrders::bestCustomer, "carol");
        that("4 unitsByProduct", Lesson12_CapstoneOrders::unitsByProduct,
                Map.of("laptop", 3, "mouse", 7, "keyboard", 4, "monitor", 1));
        that("5 topProductsByRevenue(2)", () -> topProductsByRevenue(2), List.of("laptop", "keyboard"));
        that("6 orderCountByStatus", Lesson12_CapstoneOrders::orderCountByStatus,
                Map.of("PAID", 4L, "SHIPPED", 2L, "CANCELLED", 1L));
        that("7 averageOrderValue", Lesson12_CapstoneOrders::averageOrderValue, 612.5);
        that("8 customersWhoBought(mouse)", () -> customersWhoBought("mouse"), Set.of("alice", "bob", "dave"));
        that("9 orderIdsByCustomer", Lesson12_CapstoneOrders::orderIdsByCustomer,
                Map.of("alice", List.of(1, 3), "bob", List.of(2, 5), "carol", List.of(4, 7), "dave", List.of(6)));
        that("10 biggestOrderId", Lesson12_CapstoneOrders::biggestOrderId, 4);
        Check.summary();
    }

    // TODO 1: Total revenue of ACTIVE orders.  Hint: filter(isActive) + mapToDouble(total) + sum
    static double revenue() {
        throw Check.todo();
    }

    // TODO 2: customer -> total spend over ACTIVE orders.
    // Hint: groupingBy(Order::customer, summingDouble(Lesson12_CapstoneOrders::total))
    static Map<String, Double> spendByCustomer() {
        throw Check.todo();
    }

    // TODO 3: The customer with the highest spend (reuse spendByCustomer()).
    // Hint: entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElseThrow()
    static String bestCustomer() {
        throw Check.todo();
    }

    // TODO 4: product -> total units sold over ACTIVE orders.
    // Hint: flatMap(o -> o.lines().stream()) then groupingBy(Line::product, summingInt(Line::qty))
    static Map<String, Integer> unitsByProduct() {
        throw Check.todo();
    }

    // TODO 5: The n products with the highest revenue (qty * price) over ACTIVE orders, best first.
    // Steps: flatMap lines -> groupingBy(product, summingDouble(Line::amount)) -> sort entries by value desc
    //        -> limit(n) -> keys.
    static List<String> topProductsByRevenue(int n) {
        throw Check.todo();
    }

    // TODO 6: status -> number of orders (ALL orders).
    static Map<String, Long> orderCountByStatus() {
        throw Check.todo();
    }

    // TODO 7: Average total of ACTIVE orders (0.0 if there are none).
    static double averageOrderValue() {
        throw Check.todo();
    }

    // TODO 8: Set of customers with an ACTIVE order containing `product`.
    // Hint: filter active, filter orders where lines().stream().anyMatch(...), map customer, toSet
    static Set<String> customersWhoBought(String product) {
        throw Check.todo();
    }

    // TODO 9: customer -> list of ALL their order ids (cancelled included), in id order.
    static Map<String, List<Integer>> orderIdsByCustomer() {
        throw Check.todo();
    }

    // TODO 10: Id of the order with the largest total (any status).
    // Hint: max(Comparator.comparingDouble(Lesson12_CapstoneOrders::total)).map(Order::id).orElseThrow()
    static int biggestOrderId() {
        throw Check.todo();
    }
}
