package net.styx.model_v1.tree.traverse;

import net.styx.model_v1.meta.NodeID;
import net.styx.model_v1.tree.Group;
import net.styx.model_v1.tree.Node;
import net.styx.model_v1.tree.StatefulNode;
import net.styx.model_v1.tree.TreeWalker;

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
public class ImmutableGroup<E extends StatefulNode> extends ImmutableNode<Group<E>> implements Group<E> {

    private final Collection<E> immutable;

    /**
     * @param node mutable node
     * @param immutableNodes wrapped immutable nodes (and sub nodes)
     */
    public ImmutableGroup(Group<E> node, Collection<E> immutableNodes) {
        super(node);
        immutable = Collections.unmodifiableCollection(immutableNodes);
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
        return prevent();
    }

    @Override
    public boolean remove(Object o) {
        return prevent();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return immutable.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return prevent();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return prevent();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return prevent();
    }

    @Override
    public void clear() {
        prevent();
    }

    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        return immutable.toArray(generator);
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return prevent();
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

    @Override
    public boolean remove(NodeID childNodeID) {
        return prevent();
    }

    @Override
    public Iterator<StatefulNode> children() {
        @SuppressWarnings("unchecked")
        Iterator<StatefulNode> iter = (Iterator<StatefulNode>) iterator();
        return iter;
    }

    /**
     * Pass immutable version to prevent leaking mutable node.
     *
     * @param treeWalker Visitor
     */
    @Override
    public void traverse(TreeWalker treeWalker) {
        treeWalker.onEnter(this);
    }
}
