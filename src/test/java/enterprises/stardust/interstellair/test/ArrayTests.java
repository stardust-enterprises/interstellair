package enterprises.stardust.interstellair.test;

import enterprises.stardust.interstellair.Burst;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ArrayTests {
    @Test
    public void filterEvenThenDouble() {
        Integer[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Integer[] actual = Burst.ofArray(ints)
                .filter(i -> i % 2 == 0)
                .map(i -> i * 2)
                .toArray();
        Integer[] expected = {4, 8, 12, 16, 20};

        assertArrayEquals(actual, expected);
    }

    @Test
    public void filterEvenThenDoubleThenFilter() {
        Integer[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Integer[] actual = Burst.ofArray(ints)
                .filter(i -> i % 2 == 0)
                .map(i -> i * 2)
                .filter(i -> i > 10)
                .toArray();
        Integer[] expected = {12, 16, 20};

        assertArrayEquals(actual, expected);
    }
}
