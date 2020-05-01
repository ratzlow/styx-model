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

// TODO (FRa) : (FRa): perf: try to avoid garbage for rollback by just dealing with dirty, added, missing objects
// TODO (FRa) : (FRa): diff check effectively different, same no elements, same instances of elements, change of instance

/**
 * No duplicates are allowed since every element needs to be identifiable by unique ID.
 * Order is preserved by default but can be overridden with alternative Map implementation.
 *
 * @param <E> the elements stored, which can be identified by their key
 */
public class DefaultGroup<E extends Node> implements Group<E> {

    private final NodeID nodeID;
    private final Supplier<Map<NodeID, E>> generator;

    /**
     * Elements are mutable but will remain in collection. Access to previous element state
     * possible available via Node API
     */
    private Map<NodeID, E> backup;
    private final Map<NodeID, E> col;

    //------------------------------------------------------------------
    // constructors
    //------------------------------------------------------------------

    public DefaultGroup(NodeID nodeID) {
        this(nodeID, LinkedHashMap::new);
    }

    public DefaultGroup(NodeID nodeID, Supplier<Map<NodeID, E>> generator) {
        this(nodeID, generator, Collections.emptyList());
    }

    public DefaultGroup(NodeID nodeID, Collection<E> col) {
        this(nodeID, LinkedHashMap::new, col);
    }

    public DefaultGroup(NodeID nodeID, Supplier<Map<NodeID, E>> generator,
                        Collection<E> col) {
        this.nodeID = nodeID;
        this.generator = generator;
        this.col = toMap(col);
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
        checkBackup();
        NodeID key = element.getNodeID();
        Objects.requireNonNull(key, () -> "No key assigned to node! " + element);
        E prev = col.put(key, element);
        return !Objects.equals(prev, element);
    }

    @Override
    public boolean remove(Object element) {
        checkBackup();
        E value = typeCheck(element);
        NodeID key = value.getNodeID();
        E previousValue = col.remove(key);
        return previousValue != null;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return col.values().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        checkBackup();
        boolean changed = false;
        for (E e : c) {
            NodeID key = e.getNodeID();
            E prev = col.put(key, e);
            changed = !Objects.equals(prev, e) || changed;
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        checkBackup();
        boolean changed = false;
        for (Object o : c) {
            E elem = typeCheck(o);
            E prev = col.remove(elem.getNodeID());
            changed = prev != null || changed;
        }
        return changed;
    }


    @Override
    public boolean retainAll(Collection<?> c) {
        Set<NodeID> keysToRetain = c.stream()
                .map(e -> typeCheck(e).getNodeID())
                .collect(toSet());
        Set<NodeID> keysToRemove = new HashSet<>(col.keySet());
        keysToRemove.removeAll(keysToRetain);

        return removeElements(keysToRemove);
    }


    @Override
    public void clear() {
        checkBackup();
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
        Set<NodeID> keysToRemove = col.values().stream()
                .filter(filter)
                .map(Node::getNodeID)
                .collect(toSet());

        return removeElements(keysToRemove);
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
    public NodeID getNodeID() {
        return nodeID;
    }

    /**
     * @implNote order of checks matter!
     */
    @Override
    public boolean isChanged() {
        if (backup != null && !col.keySet().equals(backup.keySet())) return true;

        return col.values().stream().anyMatch(Stateful::isChanged);
    }

    @Override
    public void commit() {
        col.values().forEach(Stateful::commit);
        backup = null;
    }

    @Override
    public void rollback() {
        // ensure the same elements are restored
        if (backup != null) {
            col.clear();
            col.putAll(backup);
            backup = null;
        }
        col.values().forEach(Stateful::rollback);
    }

    @Override
    public void accept(TreeWalker treeWalker) {
        treeWalker.onEnter(this);
        col.values().forEach(e -> e.accept(treeWalker));
        treeWalker.onExit(getNodeID());
    }

    //------------------------------------------------------------------
    // implementation details
    //------------------------------------------------------------------

    private boolean removeElements(Set<NodeID> keysToRemove) {
        if (!keysToRemove.isEmpty()) {
            checkBackup();
        }

        for (NodeID removalKey : keysToRemove) {
            col.remove(removalKey);
        }
        return !keysToRemove.isEmpty();
    }

    private void checkBackup() {
        if (backup == null) {
            Map<NodeID, E> temp = generator.get();
            temp.putAll(col);
            backup = Map.copyOf(temp);
        }
    }

    private Map<NodeID, E> toMap(Collection<E> c) {
        Map<NodeID, E> temp = generator.get();
        for (E e : c) {
            temp.put(e.getNodeID(), e);
        }
        return temp;
    }

    private E typeCheck(Object element) {
        if (element == null) {
            throw new NullPointerException("Element must not be null!");
        }

        Descriptor descriptor = nodeID.getDescriptor();
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
