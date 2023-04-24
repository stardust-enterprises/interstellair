package enterprises.stardust.interstellair.api;

import enterprises.stardust.interstellair.impl.InterstellairBurst;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.*;
import java.util.stream.BaseStream;
import java.util.stream.Collector;

/**
 * @param <T> the objects type
 * @author xtrm
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public abstract class Burst<T> implements BaseStream<T, Burst<T>>, Iterable<T> {
    public abstract <N> Burst<N> map(Function<? super T, ? extends N> mapper);

    public abstract Burst<T> filter(Predicate<? super T> filter);

    public abstract Burst<T> peek(Consumer<? super T> action);

    public abstract void forEach(Consumer<? super T> action);

    public abstract long count();

    public abstract Optional<T> min(Comparator<? super T> comparator);

    public abstract Optional<T> max(Comparator<? super T> comparator);

    public abstract <C extends Collection<? super T>> C collect(C collection);

    public abstract <R, A> R collect(Collector<? super T, A, R> collector);

    public abstract <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner);

    public abstract <C extends Collection<T>> C collect(C... reified);

    public abstract T[] toArray();

    public abstract Optional<T> first();

    public abstract Optional<T> last();

    public Optional<T> findFirst() {
        return first();
    }

    @NotNull
    @Override
    public Burst<T> sequential() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @NotNull
    @Override
    public Burst<T> parallel() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @NotNull
    @Override
    public Burst<T> unordered() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean isParallel() {
        return false; //TODO
    }

    @NotNull
    @Override
    public abstract Burst<T> onClose(Runnable closeHandler);

    @Override
    public abstract void close();

    @NotNull
    @Override
    public abstract Iterator<T> iterator();

    @NotNull
    @Override
    public abstract Spliterator<T> spliterator();

    public static <T> Burst<T> ofArray(T[] objects) {
        return new InterstellairBurst<>(objects);
    }

    public static <T> Burst<T> of(Collection<? extends T> collection) {
        return new InterstellairBurst<T>((T[]) collection.toArray());
    }

    @SafeVarargs
    public static <T> Burst<T> of(T... objects) {
        return new InterstellairBurst<>(objects);
    }
}
