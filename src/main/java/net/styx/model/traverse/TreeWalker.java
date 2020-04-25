package net.styx.model.traverse;

import net.styx.model.meta.Descriptor;
import net.styx.model.tree.*;
import net.styx.model.tree.leaf.LongLeaf;
import net.styx.model.tree.leaf.StringLeaf;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

// TODO (FRa) : (FRa): make this emit events, so listener could modify items
public class TreeWalker {

    private static final Map<Operation, TraversalHandler> TRAVERSAL_HANDLERS = Map.of(
            Operation.SET, new SetOperation(),
            Operation.REMOVE, new RemoveOperation()
    );

    public Node traverse(DataContainer rootNode, Mutation mutation) {
        TraversalHandler traversalHandler = TRAVERSAL_HANDLERS.get(mutation.operation);
        Vertex iterator = new ContainerVertex(rootNode);
        Step[] fullPath = mutation.getFullPath();
        for (int i = 0; i < fullPath.length && iterator != null; i++) {
            Step nextDescriptor = fullPath[i];
            iterator = traversalHandler.perform(iterator, nextDescriptor, mutation);
        }

        return rootNode;
    }


    static class SetOperation implements TraversalHandler {
        @Override
        public Vertex perform(Vertex parent, Step toChild, Mutation mutation) {
            final NodeFactory nodeFactory = new DefaultNodeFactory(toChild, mutation);
            return parent.getChild(toChild, nodeFactory);
        }
    }


    static class RemoveOperation implements TraversalHandler {

        @Override
        public Vertex perform(Vertex parent, Step toChild, Mutation mutation) {

            final Vertex child;

            if (toChild.getDescriptor() == mutation.getPathEnd().getDescriptor()) {
                parent.removeChild(toChild);
                child = null;
            } else {
                child = parent.getChild(toChild);
            }

            return child;
        }
    }


    interface TraversalHandler {
        Vertex perform(Vertex parent, Step toChild, Mutation mutation);
    }

    // TODO (FRa) : (FRa): create only 1 instance per traversal and provide step on interface!
    static class DefaultNodeFactory implements NodeFactory {
        private final Step toChild;
        private final Mutation mutation;

        public DefaultNodeFactory(Step toChild, Mutation mutation) {
            this.toChild = toChild;
            this.mutation = mutation;
        }

        @Override
        public DataContainer createDataContainer() {
            return new DataContainer(toChild.getDescriptor());
        }

        @Override
        public Leaf createLeaf() {
            return mutation.getLeaf();
        }

        @Override
        public IdentifiableDataContainer<?> createIdentifiableDataContainer() {
            Descriptor childDescriptor = toChild.getDescriptor();
            Descriptor idDescriptor = childDescriptor.getIDKey().orElseThrow();
            Leaf idLeaf = createIDLeaf(idDescriptor, toChild.getGroupElemKey().orElseThrow());
            Function<Leaf, ?> idValueGetter = createIDGetter(idDescriptor);
            return new IdentifiableDataContainer(childDescriptor, idLeaf, idValueGetter);
        }


        private Function<Leaf, ?> createIDGetter(Descriptor idDescriptor) {
            Map<Class<?>, Function<Leaf, ?>> getters = new HashMap<>();
            getters.put(Long.class, Leaf::getValueLong);
            getters.put(String.class, Leaf::getValueString);

            if (!getters.containsKey(idDescriptor.getDefiningClass())) {
                throw new IllegalArgumentException("No getter registered for " + idDescriptor.getDefiningClass());
            }

            return getters.get(idDescriptor.getDefiningClass());
        }


        private Leaf createIDLeaf(Descriptor idDescriptor, Object idValue) {
            Objects.requireNonNull(idValue);

            if (!idValue.getClass().equals(idDescriptor.getDefiningClass())) {
                throw new IllegalArgumentException("ID value and defined class do not match!");
            }

            Class<?> idClazz = idDescriptor.getDefiningClass();
            final Leaf id;
            if (idClazz.equals(Long.class)) {
                id = new LongLeaf(idDescriptor, (Long) idValue, true, true);
            } else if (idClazz.equals(String.class)) {
                id = new StringLeaf(idDescriptor, (String) idValue);
            } else {
                throw new IllegalArgumentException("Unmatched ID type");
            }

            return id;
        }
    }
}
