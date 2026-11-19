package streams;

import static streams.Check.that;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * LESSON 13 - Strings and text processing
 *
 * <h2>Theory</h2>
 * Text is the most common stream source in real code. The tools you need:
 * <pre>
 *   str.chars()                       IntStream of UTF-16 chars (an IntStream, NOT a Stream&lt;Character&gt;!)
 *   str.codePoints()                  IntStream of Unicode code points (use for emoji / non-BMP text)
 *   str.lines()                       Stream&lt;String&gt; split on \n, \r\n, \r
 *   Pattern.splitAsStream(str)        lazy split by regex
 *   matcher.results()                 Stream&lt;MatchResult&gt; of every match (Java 9+)
 *   Arrays.stream(str.split(" "))     eager split into an array first
 * </pre>
 * Gotchas:
 * <ul>
 *   <li>{@code chars()} gives ints. To get characters, {@code mapToObj(c -> (char) c)}.</li>
 *   <li>To build a String from an IntStream use {@code mapToObj(c -> String.valueOf((char) c)).collect(joining())}
 *       or the 3-arg {@code collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)}.</li>
 *   <li>{@code "a,b,,".split(",")} drops trailing empty strings; {@code split(",", -1)} keeps them.</li>
 * </ul>
 *
 * Run:  ./run.sh 13        (answers: ./run.sh --solutions 13)
 */
public class Lesson13_StringsAndText {

    static final String TEXT = "the quick brown fox jumps over the lazy dog the end";

    public static void main(String[] args) {
        demo();

        Check.section("Lesson 13 - Strings and text");
        that("1 wordCount", () -> wordCount(TEXT), Map.of(
                "the", 3L, "quick", 1L, "brown", 1L, "fox", 1L, "jumps", 1L,
                "over", 1L, "lazy", 1L, "dog", 1L, "end", 1L));
        that("2 longestWord (first one wins a tie)", () -> longestWord(TEXT), "quick");
        that("3 titleCase", () -> titleCase("hello streams world"), "Hello Streams World");
        that("4 isPalindrome", () -> List.of(
                isPalindrome("A man, a plan, a canal: Panama"),
                isPalindrome("streams")), List.of(true, false));
        that("5 vowelCount", () -> vowelCount("Streams are awesome"), 8L);
        that("6 charFrequency", () -> charFrequency("mississippi").toString(), "{i=4, m=1, p=2, s=4}");
        that("7 firstNonRepeated", () -> List.of(firstNonRepeated("swiss"), firstNonRepeated("aabb")),
                List.of(Optional.of('w'), Optional.empty()));
        that("8 sortedLetters", () -> sortedLetters("Listen!"), "eilnst");
        that("9 groupAnagrams", () -> groupAnagrams(List.of("eat", "tea", "tan", "ate", "nat", "bat")), Set.of(
                Set.of("eat", "tea", "ate"), Set.of("tan", "nat"), Set.of("bat")));
        that("10 parseKeyValues", () -> parseKeyValues("name=Ann;age=30;city=Paris"),
                Map.of("name", "Ann", "age", "30", "city", "Paris"));
        that("11 runLengthEncode", () -> runLengthEncode("aaabccdddd"), "a3b1c2d4");
        Check.summary();
    }

    static void demo() {
        System.out.println("chars() are ints: " + "abc".chars().boxed().toList());
        System.out.println("as chars:         " + "abc".chars().mapToObj(c -> (char) c).toList());
        System.out.println("lines():          " + "one\ntwo\r\nthree".lines().toList());
        System.out.println("regex matches:    "
                + Pattern.compile("\\d+").matcher("a1b22c333").results().map(r -> r.group()).toList());
    }

    /** Provided: lazily split on whitespace. */
    static Stream<String> words(String s) {
        return Pattern.compile("\\s+").splitAsStream(s.trim());
    }

    // TODO 1: word -> number of occurrences.   Hint: words(s).collect(groupingBy(identity(), counting()))
    static Map<String, Long> wordCount(String text) {
        throw Check.todo();
    }

    // TODO 2: the longest word. If several share the longest length, the FIRST one wins.
    // Hint: max(comparingInt(String::length)). Stream.max keeps the earlier element on a tie. Look at why.
    static String longestWord(String text) {
        throw Check.todo();
    }

    // TODO 3: "hello streams world" -> "Hello Streams World".
    // Hint: map each word to Character.toUpperCase(w.charAt(0)) + w.substring(1), then joining(" ")
    static String titleCase(String sentence) {
        throw Check.todo();
    }

    // TODO 4: palindrome check that ignores case and everything that is not a letter or digit.
    // Hint: first build a "clean" String from chars() (filter Character::isLetterOrDigit, map Character::toLowerCase),
    //       then IntStream.range(0, clean.length() / 2).allMatch(i -> compare i-th char with its mirror).
    static boolean isPalindrome(String s) {
        throw Check.todo();
    }

    // TODO 5: how many vowels (a e i o u, any case)?   Hint: chars().filter(c -> "aeiou".indexOf(c) >= 0).count()
    static long vowelCount(String s) {
        throw Check.todo();
    }

    // TODO 6: char -> frequency, keys SORTED.   Hint: chars().mapToObj(c -> (char) c) then
    //         groupingBy(identity(), TreeMap::new, counting())
    static TreeMap<Character, Long> charFrequency(String s) {
        throw Check.todo();
    }

    // TODO 7: the first character that appears exactly once ("swiss" -> 'w'); empty if none.
    // Hint: count into a LinkedHashMap (keeps insertion order!) with groupingBy(.., LinkedHashMap::new, counting()),
    //       then stream its entries, filter count == 1, map to key, findFirst().
    static Optional<Character> firstNonRepeated(String s) {
        throw Check.todo();
    }

    // TODO 8: keep only the letters, lower-case them, sort them, join into a String ("Listen!" -> "eilnst").
    // This is the "signature" two anagrams share.
    static String sortedLetters(String s) {
        throw Check.todo();
    }

    // TODO 9: group words that are anagrams of each other. Result: a set of groups.
    // Hint: groupingBy(Lesson13_StringsAndText::sortedLetters, toSet()), then .values().stream().collect(toSet())
    static Set<Set<String>> groupAnagrams(List<String> words) {
        throw Check.todo();
    }

    // TODO 10: "name=Ann;age=30" -> {name=Ann, age=30}.
    // Hint: Pattern.compile(";").splitAsStream(s), then map(p -> p.split("=", 2)), then toMap(a -> a[0], a -> a[1])
    static Map<String, String> parseKeyValues(String s) {
        throw Check.todo();
    }

    // TODO 11: run-length encoding: "aaabccdddd" -> "a3b1c2d4".
    // Hint: the regex "(.)\\1*" matches a run of one repeated char. Pattern.compile(..).matcher(s).results()
    //       gives each run; map r -> r.group(1) + r.group().length() and join.
    static String runLengthEncode(String s) {
        throw Check.todo();
    }
}
