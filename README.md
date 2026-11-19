# Java Streams: Zero to Advanced

18 short lessons (185 exercises). Each one has **theory** (in the class Javadoc), a **runnable demo**, and **`// TODO` exercises**
that check themselves. No Maven, no JUnit, no dependencies: just JDK 17+ (built and verified on JDK 21).

## How to learn with it

1. Open `src/streams/Lesson01_StreamBasics.java`. Read the Javadoc at the top (about 3 minutes).
2. Run it: `./run.sh 01`. Read the demo output, and note every exercise shows ⬜ TODO.
3. Do the `// TODO` methods **in order**, then run again. Each exercise gets one of:
   - ✅ PASS: correct
   - ❌ FAIL: shows expected vs actual (or the exception)
   - ⬜ TODO: still the stub `throw Check.todo();`. Replace that line with your code.
4. When the lesson prints `🎉 Lesson complete`, go to the next one.
5. Stuck for more than 10 minutes? Read the hint, then peek at the same method in `solutions/`
   (`./run.sh --solutions 03` runs the answer key). Afterwards re-type it from memory the next day.

```
./run.sh 05              # run one lesson (your code)
./run.sh all             # progress summary for every lesson
./run.sh --solutions 05  # run the answer key
```

## Roadmap

| # | Lesson | You will learn | Exercises |
|---|--------|----------------|-----------|
| 01 | Stream basics | pipeline = source, intermediate ops, terminal op; laziness; single use; ways to create streams | 6 |
| 02 | Intermediate ops | filter, map, distinct, sorted + Comparators, limit/skip, peek, vertical processing | 7 |
| 03 | Terminal ops | reduce, min/max, find*, any/all/noneMatch, count, toArray, Optional basics | 14 |
| 04 | flatMap and Optional | flatMap, takeWhile/dropWhile, mapMulti, Optional.stream | 10 |
| 05 | Primitive streams | IntStream/DoubleStream, range, sum/average, summaryStatistics, boxing cost | 11 |
| 06 | Collectors 1 | toList/Set/Map, joining, counting/summing/averaging, toMap duplicate keys, immutable lists | 10 |
| 07 | Grouping | groupingBy, partitioningBy, downstream collectors, flatMapping, teeing, nested groups | 10 |
| 08 | reduce and custom collectors | 3-arg reduce, associativity, Stream.iterate(seed, hasNext, next), `Collector.of` | 8 |
| 09 | Laziness and pitfalls | execution order, short-circuit, `count()` skipping peek, sorted as a barrier, side effects | 8 |
| 10 | Parallel streams | commonPool, when it helps and when it hurts, races, forEachOrdered, concurrent collectors | 7 |
| 11 | Advanced patterns | zip, sliding window, top-N into a LinkedHashMap, filtering vs filter, null-safety, first-unique | 10 |
| 12 | Capstone | order-analytics pipelines that combine everything | 10 |
| 13 | Strings and text | `chars()`, `lines()`, `splitAsStream`, `Matcher.results()`, palindromes, anagrams, run-length encoding | 11 |
| 14 | Maps and entries | streaming `entrySet()`, sort by value, invert a map, merge maps, histograms, leaderboards with an index | 12 |
| 15 | Stream sources | `iterate`/`generate`, `Stream.builder`, `concat`, `ofNullable`, `BufferedReader.lines`, Iterables, `datesUntil`, enums | 12 |
| 16 | Interview classics | FizzBuzz, primes, two-sum, duplicates, second-highest, prefix sums, Pascal's triangle | 13 |
| 17 | Predict the output | a quiz: predict execution order, exceptions and collector behaviour *before* running | 14 |
| 18 | Capstone 2 | a university gradebook: flatten, group, cross-query, format a report | 12 |

### Suggested order and pacing

- **01-12** are the core course. Do them in order.
- **13-16** are practice packs: they only need lessons 01-07, so do them in any order, one per sitting.
- **17** is a thinking exercise. Work out each answer on paper first. If you run it just to see the output, you lose the point of it.
- **18** is the second capstone. Do it last, without peeking at hints for the first 10 minutes.

## Rules of thumb (the whole course in 10 lines)

1. Nothing runs without a terminal operation.
2. Each element flows through the whole pipeline before the next one, except at barriers like `sorted`.
3. `filter` early, `sorted` late, `limit` as soon as you can.
4. Use `mapToInt/Double/Long` for numeric work: no boxing and you get `sum()`, `average()`.
5. `flatMap` for 1-to-many, `mapMulti` when creating small streams is wasteful.
6. Collect with collectors, never `forEach(list::add)`.
7. `toMap` throws on duplicate keys, so pass a merge function. Prefer `groupingBy` for "many per key".
8. `groupingBy(.., filtering(..))` keeps empty groups. `.filter(..)` before it drops them.
9. `parallel()` is for big, CPU-bound, side-effect-free work. Measure it, and never use it for blocking I/O.
10. If a `for` loop is clearer, use the loop.

## After the course

- Rewrite a real loop from one of your own projects as a stream, then decide honestly which is more readable.
- Look at `java.util.stream.Gatherers` (final in Java 24) for custom intermediate operations such as windowing.
- Read JDK Javadoc for `java.util.stream` ("Reduction", "Side-effects", "Ordering" sections).
- Benchmark one stream against its loop with JMH to build intuition for the overhead.
