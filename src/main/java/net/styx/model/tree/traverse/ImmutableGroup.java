package net.styx.model.tree.traverse;

import net.styx.model.tree.Group;
import net.styx.model.tree.Node;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Inherit the immutability properties of {@link Collections#unmodifiableCollection(Collection)}
 * 
 * @param <E> sub class of a {@link Node}
 */
public class ImmutableGroup<E extends Node> extends ImmutableNode<Group<E>> implements Group<E> {

    private final Collection<E> immutable;
    
    public ImmutableGroup(Group<E> node) {
        super(node);
        immutable = Collections.unmodifiableCollection(node);
    }

    @Override
    public int size() {
        return immutable.size();
    }

    @Override
    public boolean contains(Object o) {
        return immutable.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return immutable.iterator();
    }

    @Override
    public Object[] toArray() {
        return immutable.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return immutable.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return immutable.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return immutable.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return immutable.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return immutable.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return immutable.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return immutable.retainAll(c);
    }

    @Override
    public void clear() {
        immutable.clear();
    }

    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        return immutable.toArray(generator);
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return immutable.removeIf(filter);
    }

    @Override
    public Spliterator<E> spliterator() {
        return immutable.spliterator();
    }

    @Override
    public Stream<E> stream() {
        return immutable.stream();
    }

    @Override
    public Stream<E> parallelStream() {
        return immutable.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        immutable.forEach(action);
    }
}
