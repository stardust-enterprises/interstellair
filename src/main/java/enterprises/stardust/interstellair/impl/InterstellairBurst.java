package enterprises.stardust.interstellair.impl;

import enterprises.stardust.interstellair.api.Burst;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @param <T> the objects type
 * @author xtrm
 */
@SuppressWarnings({"unchecked", "ForLoopReplaceableByForEach"})
public class InterstellairBurst<T> extends Burst<T> {
    private static final Map<Class<?>, Supplier<Collection<?>>>
            ALLOCATION_TABLE = new HashMap<>();
    private final Object[] objects;
    private boolean used;

    public InterstellairBurst(T[] objects) {
        this.objects = objects;
    }

    @Override
    public <N> Burst<N> map(Function<T, N> mapper) {
        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (object == null) {
                objects[i] = null;
            } else {
                objects[i] = mapper.apply((T) object);
            }
        }
        // This is a hack, basically a reinterpretation cast from T to N.
        // This is safe because the array is in fact of type N after mapping.
        return (Burst<N>) this;
    }

    @Override
    public Burst<T> filter(Predicate<T> filter) {
        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (object == null) continue;
            if (!filter.test((T) object)) {
                objects[i] = null;
            }
        }
        return this;
    }

    @Override
    public T[] toArray() {
        terminalOperation();

        //TODO: find out if reallocating the array is faster than copying
        //existing elements inside #objects
        long newArrayLength = internalCount();
        if (newArrayLength > Integer.MAX_VALUE) {
            throw new IllegalStateException("Array length is too big");
        }
        T[] array = (T[])
                Array.newInstance(objects.getClass().getComponentType(), (int) newArrayLength);
        int i = 0;
        for (int j = 0; j < objects.length; j++) {
            Object object = objects[j];
            if (object != null) {
                array[i++] = (T) object;
            }
        }
        return array;
    }

    @Override
    public long count() {
        terminalOperation();

        return internalCount();
    }

    private long internalCount() {
        long count = 0L;
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] != null) {
                count++;
            }
        }
        return count;
    }

    @Override
    public final <C extends Collection<? super T>> C collect(C collection) {
        terminalOperation();

        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (object != null) {
                collection.add((T) object);
            }
        }
        return collection;
    }

    @SafeVarargs
    @Override
    public final <C extends Collection<T>> C collect(C... reified) {
        terminalOperation();

        Class<? extends Collection<T>> clazz = (Class<? extends Collection<T>>)
                reified.getClass().getComponentType();
        Supplier<? extends Collection<?>> supplier =
                ALLOCATION_TABLE.getOrDefault(clazz, null);
        if (supplier == null) {
            throw new IllegalArgumentException("No supplier found for class " +
                    clazz);
        }
        C collection = (C) supplier.get();
        return collect(collection);
    }

    private void terminalOperation() {
        if (used) {
            throw new IllegalStateException("Burst has already been used");
        }
        this.used = true;
    }

    static {
        ALLOCATION_TABLE.put(Set.class, HashSet::new);
        ALLOCATION_TABLE.put(List.class, ArrayList::new);
        ALLOCATION_TABLE.put(Queue.class, LinkedList::new);
    }
}
