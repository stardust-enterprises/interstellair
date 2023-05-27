package enterprises.stardust.interstellair.impl;

import enterprises.stardust.interstellair.api.Burst;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;

/**
 * @param <T> the objects type
 * @author xtrm
 */
@SuppressWarnings({"unchecked", "ForLoopReplaceableByForEach"})
public class InterstellairBurst<T> extends Burst<T> {
    private static final Map<Class<?>, Supplier<Collection<?>>>
        ALLOCATION_TABLE = new HashMap<>();
    public static final Object NULL_HOLDER = new Object();
    private final Object[] objects;
    private final List<Runnable> closeRunnables = new ArrayList<>();
    private Class<?> type;
    private boolean used;

    public InterstellairBurst(T[] objects) {
        this.type = objects.getClass().getComponentType();
        if (type.isPrimitive()) {
            throw new IllegalArgumentException("Primitive arrays are not supported yet");
        } else {
            if (type == Object.class) {
                this.objects = objects;
            } else {
                // recontextualize
                this.objects = new Object[objects.length];
                System.arraycopy(objects, 0, this.objects, 0, objects.length);
            }
        }
    }

    @Override
    public <N> Burst<N> map(Function<? super T, ? extends N> mapper) {
        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (object == NULL_HOLDER) {
                objects[i] = NULL_HOLDER;
            } else {
                objects[i] = mapper.apply((T) object);
                this.type = objects[i].getClass();
            }
        }
        // This is a hack, basically a reinterpretation cast from T to N.
        // This is safe because the array is in fact of type N after mapping.
        return (Burst<N>) this;
    }

    @Override
    public Burst<T> filter(Predicate<? super T> filter) {
        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (object == NULL_HOLDER) continue;
            if (!filter.test((T) object)) {
                objects[i] = NULL_HOLDER;
            }
        }
        return this;
    }

    @Override
    public Burst<T> peek(Consumer<? super T> action) {
        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (object != NULL_HOLDER) {
                action.accept((T) object);
            }
        }
        return this;
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        terminalOperation();

        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (object != NULL_HOLDER) {
                action.accept((T) object);
            }
        }
    }

    @Override
    public long count() {
        terminalOperation();

        return internalCount();
    }

    private long internalCount() {
        long count = 0L;
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] != NULL_HOLDER) {
                count++;
            }
        }
        return count;
    }

    @Override
    public Optional<T> min(Comparator<? super T> comparator) {
        terminalOperation();

        T min = null;
        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (object != NULL_HOLDER) {
                if (min == null) {
                    min = (T) object;
                } else {
                    if (comparator.compare(min, (T) object) > 0) {
                        min = (T) object;
                    }
                }
            }
        }
        return Optional.ofNullable(min);
    }

    @Override
    public Optional<T> max(Comparator<? super T> comparator) {
        terminalOperation();

        T max = null;
        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (object != NULL_HOLDER) {
                if (max == null) {
                    max = (T) object;
                } else {
                    if (comparator.compare(max, (T) object) < 0) {
                        max = (T) object;
                    }
                }
            }
        }
        return Optional.ofNullable(max);
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
        T[] array = (T[]) Array.newInstance(type, (int) newArrayLength);
        int i = 0;
        for (int j = 0; j < objects.length; j++) {
            Object object = objects[j];
            if (object != NULL_HOLDER) {
                array[i++] = (T) object;
            }
        }
        return array;
    }

    @Override
    public final <C extends Collection<? super T>> C collect(C collection) {
        terminalOperation();

        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (object != NULL_HOLDER) {
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

    @Override
    public <R, A> R collect(Collector<? super T, A, R> collector) {
        terminalOperation();

        A container = collector.supplier().get();
        BiConsumer<A, ? super T> accumulator = collector.accumulator();
        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (object != NULL_HOLDER) {
                accumulator.accept(container, (T) object);
            }
        }
        return collector.finisher().apply(container);
    }

    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
        terminalOperation();

        R container = supplier.get();
        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (object != NULL_HOLDER) {
                accumulator.accept(container, (T) object);
            }
        }
        return container;
    }

    @Override
    public Optional<T> first() {
        terminalOperation();

        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (object != NULL_HOLDER) {
                return Optional.of((T) object);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<T> last() {
        terminalOperation();

        Object last = null;
        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (object != NULL_HOLDER) {
                last = object;
            }
        }
        return Optional.ofNullable((T) last);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        // Terminal operation (by toArray() in BurstIterator)
        return new BurstIterator();
    }

    @NotNull
    @Override
    public Spliterator<T> spliterator() {
        // Terminal operation (by toArray in iterator())
        return Spliterators.spliterator(iterator(), internalCount(),
            Spliterator.ORDERED | Spliterator.IMMUTABLE);
    }

    @NotNull
    @Override
    public Burst<T> onClose(Runnable closeHandler) {
        closeRunnables.add(closeHandler);
        return this;
    }

    @Override
    public void close() {
        terminalOperation();
    }

    private void terminalOperation() {
        if (used) {
            throw new IllegalStateException("Burst has already been used");
        }
        this.used = true;
        closeRunnables.forEach(Runnable::run);
    }

    static {
        ALLOCATION_TABLE.put(Set.class, HashSet::new);
        ALLOCATION_TABLE.put(List.class, ArrayList::new);
        ALLOCATION_TABLE.put(Queue.class, LinkedList::new);
    }

    public class BurstIterator implements Iterator<T> {
        private int index = 0;
        private final Object[] objects =
            InterstellairBurst.this.toArray();

        @Override
        public boolean hasNext() {
            return index < objects.length;
        }

        @Override
        public T next() {
            return (T) objects[index++];
        }
    }
}
