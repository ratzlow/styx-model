package net.styx.model.meta;

import java.util.*;
import java.util.stream.Collectors;

// todo: is this collection actually a set?!
public class Group<E extends Node<E, T>, T extends NodeDef<E>> implements Collection<E> {
    private final NodePath<GroupDef<E, T>> path;
    private final StateTracker tracker;

    /**
     * Map filtered down to the direct child nodes of this collection.
     * Does not contain their children (shallow tree, next level)
     */
    private final SortedMap<NodePath<?>, Object> view;

    // todo: track max elementIdx, allow passing this as property!
    /** running index of contained elements */
    private int elementIdx = 0;

    public Group(NodePath<GroupDef<E, T>> path, StateTracker stateTracker) {
        this.path = path;
        this.tracker = stateTracker;

        T elemDef = path.getLeaf().def().getElementDef();
        NodePath<T> fromKey = new NodePath<>(path, new NodeID<>(0, elemDef.getDefaultName(), elemDef));
        NodePath<T> toKey = new NodePath<>(path, new NodeID<>(Integer.MAX_VALUE, elemDef.getDefaultName(), elemDef));

        this.view = tracker.getNodes().subMap(fromKey, toKey);
    }

    @Override
    public int size() {
        return view.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return o instanceof Node && (view.containsKey(((Node<?, ?>) o).getNodePath()));
    }

    @Override
    public Iterator<E> iterator() {
        return new GroupIter<>(view.values().iterator());
    }

    @Override
    public Object[] toArray() {
        return view.values().toArray();
    }

    @Override
    public <E> E[] toArray(E[] a) {
        return view.values().toArray(a);
    }

    @Override
    public boolean add(E e) {
        NodePath<T> indexedPath = new NodePath<>(path, new NodeID<>(elementIdx++, path.getLeaf().def().getElementDef()));
        int before = tracker.changeCount();
        e.connect(indexedPath, tracker);
        return before != tracker.changeCount();
    }

    /**
     * Since we can only add Nodes to this collection. Ergo only Nodes can be removed. Optional operation.
     */
    @Override
    public boolean remove(Object o) {
        boolean changed = false;
        if (o instanceof Node) {
            Node<?, ?> elem = (Node<?, ?>) o;
            int before = tracker.changeCount();
            elem.disconnect();
            changed = before != tracker.changeCount();
        }
        return changed;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        int count = 0;
        for (Object elem : c) {
            count += contains(elem) ? 1 : 0;
        }
        return count == c.size();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean changed = false;
        for (E e : c) {
            changed = add(e) || changed;
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (Object e : c) {
            changed = remove(e) || changed;
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> keep) {
        Set<? extends NodePath<?>> keepKeys = keep.stream()
                .filter(elem -> elem instanceof Node<?, ?>)
                .map(elem -> ((Node<?, ?>) elem).getNodePath())
                .collect(Collectors.toSet());
        // determine entries to rm
        Set<? extends NodePath<?>> removeKeys = new HashSet<>(view.keySet());
        removeKeys.removeAll(keepKeys);
        Collection<Object> elementsToBeRemoved = removeKeys.stream().map(view::get).collect(Collectors.toList());

        return removeAll(elementsToBeRemoved);
    }

    @Override
    public void clear() {
        removeAll(new ArrayList<>(view.values()));
    }


    //------------------------------------------------------------------------------------------------------------------
    // inner classes
    //------------------------------------------------------------------------------------------------------------------

    private static final class GroupIter<E> implements Iterator<E> {
        private final Iterator<Object> iter;

        public GroupIter(Iterator<Object> iter) {
            this.iter = iter;
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @SuppressWarnings("unchecked")
        @Override
        public E next() {
            return (E) iter.next();
        }
    }
}
