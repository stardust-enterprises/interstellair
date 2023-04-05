package enterprises.stardust.interstellair.jmh;

import enterprises.stardust.interstellair.Burst;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ForLoopReplaceableByForEach")
@Fork(value = 1, warmups = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class GlobalExamples {
    @Benchmark
    public void filterEvenThenDouble(Blackhole b) {
        Integer[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Integer[] actual = Burst.ofArray(ints)
            .filter(i -> i % 2 == 0)
            .map(i -> i * 2)
            .toArray();
        b.consume(actual);
    }

    @Benchmark
    public void filterEvenThenDoubleThenFilter(Blackhole b) {
        Integer[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Integer[] actual = Burst.ofArray(ints)
            .filter(i -> i % 2 == 0)
            .map(i -> i * 2)
            .filter(i -> i > 10)
            .toArray();
        b.consume(actual);
    }

    @Benchmark
    public void filterEvenThenDoubleStream(Blackhole b) {
        Integer[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Integer[] actual = Arrays.stream(ints)
            .filter(i -> i % 2 == 0)
            .map(i -> i * 2)
            .toArray(Integer[]::new);
        b.consume(actual);
    }

    @Benchmark
    public void filterEvenThenDoubleThenFilterStream(Blackhole b) {
        Integer[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Integer[] actual = Arrays.stream(ints)
            .filter(i -> i % 2 == 0)
            .map(i -> i * 2)
            .filter(i -> i > 10)
            .toArray(Integer[]::new);
        b.consume(actual);
    }

    @Benchmark
    public void filterEvenThenDoubleLoop(Blackhole b) {
        Integer[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Integer[] actual = new Integer[5];
        int j = 0;
        for (int i = 0; i < ints.length; i++) {
            if (ints[i] % 2 == 0) {
                int doubled = ints[i] * 2;
                actual[j++] = doubled;
            }
        }
        b.consume(actual);
    }

    @Benchmark
    public void filterEvenThenDoubleThenFilterLoop(Blackhole b) {
        Integer[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Integer[] actual = new Integer[5];
        int j = 0;
        for (int i = 0; i < ints.length; i++) {
            if (ints[i] % 2 == 0) {
                int doubled = ints[i] * 2;
                if (doubled > 10) {
                    actual[j++] = doubled;
                }
            }
        }
        b.consume(actual);
    }

    @Benchmark
    public void filterEvenThenDoubleLoopEnhanced(Blackhole b) {
        Integer[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Integer[] actual = new Integer[5];
        int j = 0;
        for (Integer i : ints) {
            if (i % 2 == 0) {
                int doubled = i * 2;
                actual[j++] = doubled;
            }
        }
        b.consume(actual);
    }

    @Benchmark
    public void filterEvenThenDoubleThenFilterLoopEnhanced(Blackhole b) {
        Integer[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Integer[] actual = new Integer[5];
        int j = 0;
        for (Integer i : ints) {
            if (i % 2 == 0) {
                int doubled = i * 2;
                if (doubled > 10) {
                    actual[j++] = doubled;
                }
            }
        }
        b.consume(actual);
    }
}
