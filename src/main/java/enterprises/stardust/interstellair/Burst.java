package enterprises.stardust.interstellair;

import enterprises.stardust.interstellair.impl.InterstellairBurst;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @param <T> the objects type
 * @author xtrm
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public abstract class Burst<T> {
    public abstract <N> Burst<N> map(Function<T, N> mapper);

    public abstract Burst<T> filter(Predicate<T> filter);

    public abstract T[] toArray();

    public abstract long count();

    public abstract <C extends Collection<? super T>> C collect(C collection);

    public abstract <C extends Collection<T>> C collect(C... reified);

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
