package enterprises.stardust.interstellair.impl;

import enterprises.stardust.interstellair.Burst;

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
    private final T[] objects;
    private boolean used;

    public InterstellairBurst(T[] objects) {
        this.objects = objects;
    }

    @Override
    public <N> Burst<N> map(Function<T, N> mapper, N... reified) {
        N[] mapped = (N[]) Array.newInstance(
                objects.getClass().getComponentType(),
                objects.length
        );

        for (int i = 0; i < objects.length; i++) {
            T object = objects[i];
            if (object == null) {
                mapped[i] = null;
            } else {
                mapped[i] = mapper.apply(object);
            }
        }
        //FIXME: can we remove this allocation?
        //idea: use unsafe reinterpret cast and make #objects an Object array
        return new InterstellairBurst<>(mapped);
    }

    @Override
    public Burst<T> filter(Predicate<T> filter) {
        for (int i = 0; i < objects.length; i++) {
            T object = objects[i];
            if (object == null) continue;
            if (!filter.test(object)) {
                objects[i] = null;
            }
        }
        return this;
    }

    @Override
    public T[] toArray() {
        terminalOperation();

        T[] array = (T[])
                Array.newInstance(objects.getClass().getComponentType(), internalCount());
        int i = 0;
        for (int j = 0; j < objects.length; j++) {
            T object = objects[j];
            if (object != null) {
                array[i++] = object;
            }
        }
        return array;
    }

    @Override
    public int count() {
        terminalOperation();

        return internalCount();
    }

    private int internalCount() {
        int count = 0;
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
            T object = objects[i];
            if (object != null) {
                collection.add(object);
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
