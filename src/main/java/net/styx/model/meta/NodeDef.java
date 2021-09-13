package net.styx.model.meta;

/**
 * @param <E> business entity type
 */
public interface NodeDef<E> {
    /**
     * Anonymous NodeID to form the lower bound in a tree.
     */
    NodeDef<?> ANY_LOWER_BOUND = new Any(0, "*");

    /**
     * Anonymous NodeID to form the upper bound in a tree.
     */
    NodeDef<?> ANY_UPPER_BOUND = new Any(Integer.MAX_VALUE, "*");

    //------------------------------------------------------------------------------------------------------------------
    // Behaviour & properties available by this interface
    //------------------------------------------------------------------------------------------------------------------

    int getID();

    String getDefaultName();

    /**
     * Nodes might be normalized before added to tree and change log.
     * @param value org
     * @return the normalized value
     */
    default E normalize(E value) {
        return value;
    }

    //------------------------------------------------------------------------------------------------------------------
    // inner classes
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Unnamed generic def
     */
    final class Any implements NodeDef<Object> {
        private final int id;
        private final String name;

        public Any(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public int getID() {
            return id;
        }

        @Override
        public String getDefaultName() {
            return name;
        }
    }
}
