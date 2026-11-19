package streams;

import static streams.Check.that;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * LESSON 14 - Streaming over Maps
 *
 * <h2>Theory</h2>
 * A {@code Map} is not a {@code Collection}, so it has no {@code stream()}. You stream one of its views:
 * <pre>
 *   map.entrySet().stream()    Stream&lt;Map.Entry&lt;K,V&gt;&gt;   (the one you want 90% of the time)
 *   map.keySet().stream()      Stream&lt;K&gt;
 *   map.values().stream()      Stream&lt;V&gt;
 * </pre>
 * Useful entry helpers:
 * <pre>
 *   Map.Entry.comparingByKey()            Map.Entry.comparingByValue()
 *   Map.Entry.comparingByValue(Comparator.reverseOrder())      sort by value, descending
 *   Map.Entry::getKey   Map.Entry::getValue                     method references for map(..)
 *   Map.entry(k, v)                                             create an entry (immutable, null-hostile)
 * </pre>
 * Turning a stream back into a Map:
 * <ul>
 *   <li>{@code toMap(keyFn, valueFn)} throws on duplicate keys. Pass a merge function as the 3rd argument.</li>
 *   <li>{@code toMap} returns a {@code HashMap}: iteration order is arbitrary. To keep order, use the 4-arg form
 *       with {@code LinkedHashMap::new}.</li>
 *   <li>{@code sorted(..)} before collecting into a plain HashMap is pointless: the order is lost again.</li>
 * </ul>
 * Tip: {@code Map.Entry.comparingByValue().reversed()} often fails to compile because of type inference.
 * Use {@code comparingByValue(Comparator.reverseOrder())} instead, or add explicit type witnesses
 * {@code Map.Entry.<String, Integer>comparingByValue().reversed()}.
 *
 * Run:  ./run.sh 14        (answers: ./run.sh --solutions 14)
 */
public class Lesson14_MapsAndEntries {

    /** Provided: name -> score, in this insertion order (a LinkedHashMap, so iteration order is predictable). */
    static Map<String, Integer> scores() {
        Map<String, Integer> m = new LinkedHashMap<>();
        m.put("alice", 82);
        m.put("bob", 91);
        m.put("carol", 77);
        m.put("dave", 91);
        m.put("eve", 65);
        return m;
    }

    public static void main(String[] args) {
        demo();

        Check.section("Lesson 14 - Maps and entries");
        that("1 rankedNames", Lesson14_MapsAndEntries::rankedNames, List.of("bob", "dave", "alice", "carol", "eve"));
        that("2 top2AsMap (order matters)", () -> top2AsMap().toString(), "{bob=91, dave=91}");
        that("3 namesByScore", () -> namesByScore().toString(), "{65=[eve], 77=[carol], 82=[alice], 91=[bob, dave]}");
        that("4 passing (>= 80)", Lesson14_MapsAndEntries::passing, Set.of("alice", "bob", "dave"));
        that("5 curved (+5, max 100)", Lesson14_MapsAndEntries::curved,
                Map.of("alice", 87, "bob", 96, "carol", 82, "dave", 96, "eve", 70));
        that("6 mergeCounts", () -> mergeCounts(Map.of("x", 1, "y", 2), Map.of("y", 3, "z", 4)),
                Map.of("x", 1, "y", 5, "z", 4));
        that("7 keyWithMaxValue", () -> keyWithMaxValue(Map.of("apple", 3, "pear", 7, "fig", 5)), "pear");
        that("8 totalScore", Lesson14_MapsAndEntries::totalScore, 406);
        that("9 mostCommonScore", Lesson14_MapsAndEntries::mostCommonScore, 91);
        that("10 histogram", () -> histogram().toString(), "{60=1, 70=1, 80=1, 90=2}");
        that("11 leaderboard", Lesson14_MapsAndEntries::leaderboard, List.of(
                "1. bob (91)", "2. dave (91)", "3. alice (82)", "4. carol (77)", "5. eve (65)"));
        that("12 asQueryString", () -> asQueryString(scores()), "alice=82&bob=91&carol=77&dave=91&eve=65");
        Check.summary();
    }

    static void demo() {
        System.out.println("entries:  " + scores().entrySet().stream().map(Map.Entry::toString).toList());
        System.out.println("by value: " + scores().entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).toList());
        System.out.println("inverted: " + scores().entrySet().stream()
                .collect(Collectors.groupingBy(Map.Entry::getValue, TreeMap::new,
                        Collectors.mapping(Map.Entry::getKey, Collectors.toList()))));
    }

    // TODO 1: names ranked by score DESCENDING; ties broken by name ascending.
    // Hint: entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
    //                                      .thenComparing(Map.Entry.comparingByKey()))
    static List<String> rankedNames() {
        return scores().entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .map(Map.Entry::getKey)
                .toList();
    }

    // TODO 2: the two best entries as a Map that KEEPS the ranking order (use rankedNames' ordering).
    // Hint: sorted, limit(2), then toMap(getKey, getValue, (a, b) -> a, LinkedHashMap::new)
    static Map<String, Integer> top2AsMap() {
        return scores().entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(2)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    // TODO 3: INVERT the map: score -> names with that score, keys sorted.
    // Hint: groupingBy(Map.Entry::getValue, TreeMap::new, mapping(Map.Entry::getKey, toList()))
    static TreeMap<Integer, List<String>> namesByScore() {
        return scores().entrySet().stream()
                .collect(Collectors.groupingBy(Map.Entry::getValue, TreeMap::new,
                        Collectors.mapping(Map.Entry::getKey, Collectors.toList())));
    }

    // TODO 4: the set of names whose score is >= 80.   Hint: filter on e.getValue(), map to key, toSet()
    static Set<String> passing() {
        return scores().entrySet().stream()
                .filter(e -> e.getValue() >= 80)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    // TODO 5: a new map where every score is curved by +5 but never above 100.
    // Hint: toMap(Map.Entry::getKey, e -> Math.min(100, e.getValue() + 5))
    static Map<String, Integer> curved() {
        return scores().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> Math.min(100, e.getValue() + 5)));
    }

    // TODO 6: merge two maps, ADDING the values of equal keys.
    // Hint: Stream.of(a, b).flatMap(m -> m.entrySet().stream()).collect(toMap(getKey, getValue, Integer::sum))
    static Map<String, Integer> mergeCounts(Map<String, Integer> a, Map<String, Integer> b) {
        return Stream.of(a, b)
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum));
    }

    // TODO 7: the key that has the largest value.   Hint: entrySet().stream().max(Map.Entry.comparingByValue())
    static String keyWithMaxValue(Map<String, Integer> m) {
        return m.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElseThrow();
    }

    // TODO 8: sum of all scores.   Hint: values().stream().mapToInt(Integer::intValue).sum()
    static int totalScore() {
        return scores().values().stream().mapToInt(Integer::intValue).sum();
    }

    // TODO 9: the score that appears most often (here it is unique).
    // Hint: count the values with groupingBy(identity(), counting()), then max over the entries by value.
    static int mostCommonScore() {
        return scores().values().stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow()
                .getKey();
    }

    // TODO 10: histogram by decade: 82 -> 80, 91 -> 90 ... count per bucket, keys sorted.
    // Hint: groupingBy(score -> score / 10 * 10, TreeMap::new, counting())
    static TreeMap<Integer, Long> histogram() {
        return scores().values().stream()
                .collect(Collectors.groupingBy(score -> score / 10 * 10, TreeMap::new, Collectors.counting()));
    }

    // TODO 11: "1. bob (91)", "2. dave (91)" ... using the ranking from TODO 1.
    // Hint: collect the sorted entries into a List first, then IntStream.range(0, size).mapToObj(i -> ... list.get(i) ...)
    //       Streams have no built-in index: this "range over a list" trick is the standard answer.
    static List<String> leaderboard() {
        List<Map.Entry<String, Integer>> ranked = scores().entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .toList();
        return IntStream.range(0, ranked.size())
                .mapToObj(i -> (i + 1) + ". " + ranked.get(i).getKey() + " (" + ranked.get(i).getValue() + ")")
                .toList();
    }

    // TODO 12: build a URL query string from a map: {q=streams, page=2} -> "q=streams&page=2" (map order).
    // Hint: map each entry to key + "=" + value, then joining("&")
    static String asQueryString(Map<String, Integer> params) {
        return params.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
    }
}
