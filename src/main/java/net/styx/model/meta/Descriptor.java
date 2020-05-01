package net.styx.model.meta;

import net.styx.model.*;

import java.math.BigDecimal;
import java.util.*;

// TODO (FRa) : (FRa): encapsulate techn. Attributes in component? entities with
// TODO (FRa) : (FRa): can we remove Clazz param?
public enum Descriptor implements NodeID {

    /**
     * default unset value
     */
    UNDEF(-1, "undefinedNode", NodeType.LEAF, DataType.UNDEF, Integer.class),

    // technical fields
    VERSION(6, "version", NodeType.LEAF, DataType.INT, Integer.class),

    // business fields fields
    NAME(10, "name", NodeType.LEAF, DataType.STRING, String.class),
    AGE(11, "age", NodeType.LEAF, DataType.INT, Integer.class),
    INCOME(12, "income", NodeType.LEAF, DataType.BIG_DECIMAL, BigDecimal.class),
    GENDER(13, "gender", NodeType.LEAF, DataType.ENUM, Gender.class),

    STREET(14, "street", NodeType.LEAF, DataType.STRING, String.class),
    ZIP(15, "zip", NodeType.LEAF, DataType.INT, Integer.class),
    CITY(16, "city", NodeType.LEAF, DataType.STRING, String.class),

    ISBN(20, "isbn", NodeType.LEAF, DataType.STRING, String.class),
    TITLE(21, "title", NodeType.LEAF, DataType.STRING, String.class),

    SIZE(30, "size", NodeType.LEAF, DataType.STRING, Integer.class),
    COLOR(31, "color", NodeType.LEAF, DataType.ENUM, Integer.class),


    // components
    DOG(1001, "dog", NodeType.CONTAINER, DataType.UNDEF, Dog.class,
            Set.of(NAME, AGE)),

    ADDRESS(1002, "address", NodeType.CONTAINER, DataType.UNDEF, Address.class,
            Set.of(STREET, ZIP, CITY)),

    BOOK(1003, "book", NodeType.CONTAINER, DataType.UNDEF, Book.class,
            Set.of(ISBN, TITLE)),

    SHOE(1004, "shoe", NodeType.CONTAINER, DataType.UNDEF, Shoe.class,
            Set.of(SIZE, COLOR)),

    // groups
    ADDRESS_GRP(2001, "addresses", NodeType.GROUP, DataType.UNDEF, Collection.class,
            Set.of(ADDRESS)),

    FAMILY_GRP(2002, "familyMembers", NodeType.GROUP, DataType.UNDEF, Collection.class,
            new int[]{3001}),

    BOOK_GRP(2003, "books", NodeType.GROUP, DataType.UNDEF, Collection.class,
            Set.of(BOOK)),

    SHOE_GRP(2004, "shoes", NodeType.GROUP, DataType.UNDEF, Collection.class,
            Set.of(SHOE)),


    // domain roots => component
    PERSON(3001, "person", NodeType.CONTAINER, DataType.UNDEF, Person.class,
            Set.of(NAME, AGE, INCOME, GENDER, DOG, ADDRESS_GRP, BOOK_GRP, FAMILY_GRP));


    //---------------------------------------------------------------------------
    // attributes
    //---------------------------------------------------------------------------

    private final int tagNumber;
    private final String propName;
    private final NodeType nodeType;
    private final DataType dataType;
    private final Class<?> definingClass;
    private Set<Descriptor> children;
    private int[] childTags = new int[0];


    //---------------------------------------------------------------------------
    // constructors
    //---------------------------------------------------------------------------
    // TODO (FRa) : (FRa): consolidate constructors
    Descriptor(int tagNumber, String propName, NodeType nodeType, DataType type, Class<?> clazz,
               Set<Descriptor> children) {
        this.tagNumber = tagNumber;
        this.propName = propName;
        this.dataType = type;
        this.nodeType = nodeType;
        this.definingClass = clazz;
        this.children = Collections.unmodifiableSet(children);
    }

    Descriptor(int tagNumber, String propName, NodeType nodeType, DataType dataType, Class<?> clazz) {
        this(tagNumber, propName, nodeType, dataType, clazz, Collections.emptySet());
    }

    Descriptor(int tagNumber, String propName, NodeType nodeType, DataType dataType, Class<?> clazz, int[] childTags) {
        this(tagNumber, propName, nodeType, dataType, clazz);
        this.childTags = childTags;
    }


    //---------------------------------------------------------------------------
    // API
    //---------------------------------------------------------------------------


    private static Set<Descriptor> resolve(int[] childTag) {
        Map<Integer, Descriptor> all = new HashMap<>();
        for (Descriptor value : values()) {
            all.put(value.tagNumber, value);
        }

        Set<Descriptor> descriptors = new HashSet<>();
        for (int tag : childTag) {
            descriptors.add(all.get(tag));
        }

        return descriptors;
    }


    //---------------------------------------------------------------------------
    // API
    //---------------------------------------------------------------------------

    public int getTagNumber() {
        return tagNumber;
    }

    public String getPropName() {
        return propName;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public DataType getDataType() {
        return dataType;
    }

    public Set<Descriptor> getChildren() {
        if (children == null && childTags.length > 0) {
            children = resolve(childTags);
        }

        return children;
    }

    public Class<?> getDefiningClass() {
        return definingClass;
    }

    @Override
    public Descriptor getDescriptor() {
        return this;
    }
}
