package net.styx.model.tree;

import net.styx.model.meta.Descriptor;
import net.styx.model.meta.NodeID;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;


/**
 * No duplicates are allowed since every element needs to be identifiable by unique ID.
 * Order is preserved by default but can be overridden with alternative Map implementation.
 *
 * @param <E> the elements stored, which can be identified by their key
 */
public class DefaultGroup<E extends Node> implements Group<E> {

    private final NodeID nodeID;
    private final Descriptor acceptedChildDescriptor;

    private final Supplier<Map<NodeID, E>> generator;

    private final MapStore<E> store;

    //------------------------------------------------------------------
    // constructors
    //------------------------------------------------------------------

    public DefaultGroup(NodeID nodeID) {
        this(nodeID, LinkedHashMap::new);
    }

    public DefaultGroup(NodeID nodeID, Supplier<Map<NodeID, E>> generator) {
        this(nodeID, generator, Collections.emptyList());
    }

    public DefaultGroup(NodeID nodeID, Collection<E> live) {
        this(nodeID, LinkedHashMap::new, live);
    }

    public DefaultGroup(NodeID nodeID, Supplier<Map<NodeID, E>> generator, Collection<E> live) {
        this.nodeID = nodeID;
        this.generator = generator;
        this.acceptedChildDescriptor = getChildDescriptor(nodeID.getDescriptor());
        this.store = new MapStore<>(toMap(live));
    }

    //------------------------------------------------------------------
    // public Collection API overrides
    //------------------------------------------------------------------

    @Override
    public int size() {
        return store.getLive().size();
    }

    /**
     * @return see {@link Collection#isEmpty()}
     */
    @Override
    public boolean isEmpty() {
        return store.getLive().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return store.getLive().containsValue(typeCheck(o));
    }

    @Override
    public Iterator<E> iterator() {
        final Iterator<E> iterator = store.getLive().values().iterator();
        return new Iterator<>() {
            @Override public boolean hasNext() { return iterator.hasNext(); }

            @Override public E next() { return iterator.next(); }

            @Override
            public void remove() {
                throw new IllegalStateException("Remove not supported for Group!");
            }
        };
    }

    @Override
    public Object[] toArray() {
        return store.getLive().values().toArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return store.getLive().values().toArray(array);
    }

    @Override
    public boolean add(E element) {
        store.checkBackup();
        NodeID key = element.getNodeID();
        Objects.requireNonNull(key, () -> "No key assigned to node! " + element);
        E prev = store.getLive().put(key, element);
        return !Objects.equals(prev, element);
    }

    @Override
    public boolean remove(Object element) {
        E value = typeCheck(element);
        NodeID key = value.getNodeID();
        return store.remove(key);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return store.getLive().values().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        store.checkBackup();
        boolean changed = false;
        for (E e : c) {
            NodeID key = e.getNodeID();
            E prev = store.getLive().put(key, e);
            changed = !Objects.equals(prev, e) || changed;
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        store.checkBackup();
        boolean changed = false;
        for (Object o : c) {
            E elem = typeCheck(o);
            E prev = store.getLive().remove(elem.getNodeID());
            changed = prev != null || changed;
        }
        return changed;
    }


    @Override
    public boolean retainAll(Collection<?> c) {
        Set<NodeID> keysToRetain = c.stream()
                .map(e -> typeCheck(e).getNodeID())
                .collect(toSet());
        Set<NodeID> keysToRemove = new HashSet<>(store.getLive().keySet());
        keysToRemove.removeAll(keysToRetain);

        return removeElements(keysToRemove);
    }

    @Override
    public void clear() {
        store.checkBackup();
        store.getLive().clear();
    }

    //--------------------------------------------------------------------------------------
    // java 8 API overrides
    //--------------------------------------------------------------------------------------

    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        return store.getLive().values().toArray(generator);
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        Set<NodeID> keysToRemove = store.getLive().values().stream()
                .filter(filter)
                .map(Node::getNodeID)
                .collect(toSet());

        return removeElements(keysToRemove);
    }


    @Override
    public Spliterator<E> spliterator() {
        return store.getLive().values().spliterator();
    }

    @Override
    public Stream<E> stream() {
        return store.getLive().values().stream();
    }

    @Override
    public Stream<E> parallelStream() {
        return store.getLive().values().parallelStream();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        store.getLive().forEach((key, value) -> action.accept(value));
    }

    //------------------------------------------------------------------
    // generic domain model overrides
    //------------------------------------------------------------------


    @Override
    public NodeID getNodeID() {
        return nodeID;
    }

    @Override
    public boolean isChanged() {
        return store.isChanged();
    }

    @Override
    public void commit() {
        store.commit();
    }

    @Override
    public void rollback() {
        store.rollback();
    }

    @Override
    public void accept(TreeWalker treeWalker) {
        treeWalker.onEnter(this);
    }

    @Override
    public Iterator<Node> children() {
        @SuppressWarnings("unchecked")
        Iterator<Node> iter = (Iterator<Node>) iterator();
        return iter;
    }

    //------------------------------------------------------------------
    // Object overrides
    //------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultGroup<?> that = (DefaultGroup<?>) o;
        return nodeID.equals(that.nodeID) &&
                acceptedChildDescriptor.equals(that.acceptedChildDescriptor) &&
                store.equals(that.store);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeID, acceptedChildDescriptor, store);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DefaultGroup.class.getSimpleName() + "[", "]")
                .add("nodeID=" + nodeID)
                .add("acceptedChildDescriptor=" + acceptedChildDescriptor)
                .add("store=" + store)
                .toString();
    }

    //------------------------------------------------------------------
    // implementation details
    //------------------------------------------------------------------

    private boolean removeElements(Set<NodeID> keysToRemove) {
        if (!keysToRemove.isEmpty()) {
            store.checkBackup();
        }

        for (NodeID removalKey : keysToRemove) {
            store.getLive().remove(removalKey);
        }
        return !keysToRemove.isEmpty();
    }

    private Map<NodeID, E> toMap(Collection<E> c) {
        Map<NodeID, E> temp = generator.get();
        for (E e : c) {
            temp.put(e.getNodeID(), e);
        }
        return temp;
    }

    private Descriptor getChildDescriptor(Descriptor descriptor) {
        if (descriptor.getChildren().size() != 1) {
            String msg = "Only single type acceptable for group descriptor but found " + descriptor;
            throw new IllegalStateException(msg);
        }

        return descriptor.getChildren().stream().findFirst().orElseThrow();
    }

    private E typeCheck(Object element) {
        if (element == null) {
            throw new NullPointerException("Element must not be null!");
        }

        boolean typeOK = ((Node)element).getNodeID().getDescriptor() == acceptedChildDescriptor;

        if (!typeOK) {
            throw new IllegalArgumentException("Argument must be != null and of type=" +
                    acceptedChildDescriptor);
        }

        return (E) element;
    }
}
