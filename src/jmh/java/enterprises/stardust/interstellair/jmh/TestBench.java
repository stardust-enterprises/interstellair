package enterprises.stardust.interstellair.jmh;

import enterprises.stardust.interstellair.Burst;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;

public class TestBench {
    @Benchmark
    @Fork(value = 1, warmups = 1)
    public void filterEvenThenDouble(Blackhole b) {
        Integer[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Integer[] actual = Burst.ofArray(ints)
                .filter(i -> i % 2 == 0)
                .map(i -> i * 2)
                .toArray();
        b.consume(actual);
    }

    @Benchmark
    @Fork(value = 1, warmups = 1)
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
    @Fork(value = 1, warmups = 1)
    public void filterEvenThenDoubleStream(Blackhole b) {
        Integer[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Integer[] actual = Arrays.stream(ints)
                .filter(i -> i % 2 == 0)
                .map(i -> i * 2)
                .toArray(Integer[]::new);
        b.consume(actual);
    }

    @Benchmark
    @Fork(value = 1, warmups = 1)
    public void filterEvenThenDoubleThenFilterStream(Blackhole b) {
        Integer[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Integer[] actual = Arrays.stream(ints)
                .filter(i -> i % 2 == 0)
                .map(i -> i * 2)
                .filter(i -> i > 10)
                .toArray(Integer[]::new);
        b.consume(actual);
    }
}
