package net.styx.model.tree;

import net.styx.model.meta.Descriptor;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

// TODO (FRa) : (FRa): Group of Leafs; Group of Groups
// TODO (FRa) : (FRa): optimization: introduce lazy init to save mem consumption
// TODO (FRa) : (FRa): optimization: try to avoid garbage for rollback by just dealing with dirty, added, missing objects

// TODO (FRa) : (FRa): diff check effectively different,
//  same no elements, same instances of elements, change of instance

// TODO (FRa) : (FRa): diff

/**
 * No duplicates are allowed since every element needs to be identifiable by unique ID.
 * Order is preserved by default but can be overridden with alternative Map implementation.
 *
 * @param <K> key of the elements part of the Group, have to be only unique within this group
 * @param <E> the elements stored, which can be identified by their key
 */
public class Group<K, E extends Stateful & Identifiable<K>>
        implements Collection<E>, Stateful, Described {

    private final Descriptor descriptor;
    private final Supplier<Map<K, E>> generator;


    /**
     * Elements are mutable but will remain in collection. Access to previous element state
     * possible available via Node API
     */
    private final Map<K, E> initial;
    private final Map<K, E> col;

    //------------------------------------------------------------------
    // constructors
    //------------------------------------------------------------------

    public Group(Descriptor descriptor) {
        this(descriptor, LinkedHashMap::new);
    }

    public Group(Descriptor descriptor, Supplier<Map<K, E>> generator) {
        this(descriptor, generator, Collections.emptyList());
    }

    public Group(Descriptor descriptor, Collection<E> col) {
        this(descriptor, LinkedHashMap::new, col);
    }

    public Group(Descriptor descriptor, Supplier<Map<K, E>> generator,
                 Collection<E> col) {
        this.descriptor = descriptor;
        this.generator = generator;
        this.col = toMap(col);
        this.initial = backup(col);
    }

    //------------------------------------------------------------------
    // public Collection API overrides
    //------------------------------------------------------------------

    @Override
    public int size() {
        return col.size();
    }

    /**
     * @return see {@link Collection#isEmpty()}
     */
    @Override
    public boolean isEmpty() {
        return col.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return col.containsValue(typeCheck(o));
    }

    @Override
    public Iterator<E> iterator() {
        return col.values().iterator();
    }

    @Override
    public Object[] toArray() {
        return col.values().toArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return col.values().toArray(array);
    }

    @Override
    public boolean add(E element) {
        K key = element.getID();
        Objects.requireNonNull(key, () -> "No key assigned to node! " + element);
        E prev = col.put(key, element);
        return !Objects.equals(prev, element);
    }

    @Override
    public boolean remove(Object element) {
        E value = typeCheck(element);
        K key = value.getID();
        E previousValue = col.remove(key);
        return previousValue != null;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return col.values().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean changed = false;
        for (E e : c) {
            K key = e.getID();
            E prev = col.put(key, e);
            changed = !Objects.equals(prev, e) || changed;
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (Object o : c) {
            E elem = typeCheck(o);
            E prev = col.remove(elem.getID());
            changed = prev != null || changed;
        }
        return changed;
    }


    @Override
    public boolean retainAll(Collection<?> c) {
        Set<K> keysToRetain = c.stream()
                .map(e -> typeCheck(e).getID())
                .collect(toSet());
        Set<K> keysToRemove = new HashSet<>(col.keySet());
        keysToRemove.removeAll(keysToRetain);
        for (K removalKey : keysToRemove) {
            col.remove(removalKey);
        }
        return !keysToRemove.isEmpty();
    }

    @Override
    public void clear() {
        col.clear();
    }

    //--------------------------------------------------------------------------------------
    // java 8 API overrides
    //--------------------------------------------------------------------------------------

    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        return col.values().toArray(generator);
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        Set<K> removalKeys = col.values().stream()
                .filter(filter)
                .map(Identifiable::getID)
                .collect(toSet());

        for (K key : removalKeys) {
            col.remove(key);
        }

        return !removalKeys.isEmpty();
    }

    @Override
    public Spliterator<E> spliterator() {
        return col.values().spliterator();
    }

    @Override
    public Stream<E> stream() {
        return col.values().stream();
    }

    @Override
    public Stream<E> parallelStream() {
        return col.values().parallelStream();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        col.forEach((key, value) -> action.accept(value));
    }

    //------------------------------------------------------------------
    // generic domain model overrides
    //------------------------------------------------------------------

    @Override
    public Descriptor getDescriptor() {
        return descriptor;
    }

    public Collection<E> getInitial() {
        return Collections.unmodifiableCollection(initial.values());
    }

    public E get(K key) {
        return col.get(key);
    }

    /**
     * @implNote order of checks matter!
     */
    @Override
    public boolean isChanged() {
        if (col.size() != initial.size())           return true;
        if (col.isEmpty())                          return false;
        if (!col.keySet().equals(initial.keySet())) return true;

        return col.values().stream().anyMatch(Stateful::isChanged);
    }

    @Override
    public void commit() {
        col.values().forEach(Stateful::commit);
        initial.clear();
        initial.putAll(col);
    }

    @Override
    public void rollback() {
        initial.values().forEach(Stateful::rollback);
        col.clear();
        col.putAll(initial);
    }

    //------------------------------------------------------------------
    // implementation details
    //------------------------------------------------------------------

    private Map<K, E> backup(Collection<E> c) {
        return toMap( c != null ? c : Collections.emptySet() );
    }

    private Map<K, E> toMap(Collection<E> c) {
        Map<K, E> temp = generator.get();
        for (E e : c) {
            temp.put(e.getID(), e);
        }
        return temp;
    }

    private E typeCheck(Object element) {
        if (element == null) {
            throw new NullPointerException("Element must not be null!");
        }

        Class<?> definingClass = descriptor.getDefiningClass();
        boolean typeOK = descriptor.getChildren().stream()
                .map(Descriptor::getDefiningClass)
                .anyMatch(childClazz -> element.getClass().isAssignableFrom(childClazz));

        if (!typeOK) {
            throw new IllegalArgumentException("Argument must be != null and of type=" +
                    definingClass.getSimpleName());
        }

        return (E) element;
    }
}
