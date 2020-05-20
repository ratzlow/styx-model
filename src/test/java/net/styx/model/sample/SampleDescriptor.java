package net.styx.model.sample;

import net.styx.model.meta.DataType;
import net.styx.model.meta.Descriptor;
import net.styx.model.meta.NodeID;
import net.styx.model.meta.NodeType;
import net.styx.model.tree.Container;

import java.util.*;
import java.util.function.Function;

// TODO (FRa) : (FRa): encapsulate techn. Attributes in component?
//  entities with
public enum SampleDescriptor implements NodeID, Descriptor {

    /**
     * default unset value
     */
    UNDEF(-1, "undefinedNode", NodeType.LEAF, DataType.UNDEF),

    // technical fields
    VERSION(6, "version", NodeType.LEAF, DataType.INT),

    // business fields fields
    NAME(10, "name", NodeType.LEAF, DataType.STRING),
    AGE(11, "age", NodeType.LEAF, DataType.INT),
    INCOME(12, "income", NodeType.LEAF, DataType.BIG_DECIMAL),
    GENDER(13, "gender", NodeType.LEAF, DataType.ENUM),

    STREET(14, "street", NodeType.LEAF, DataType.STRING),
    ZIP(15, "zip", NodeType.LEAF, DataType.INT),
    CITY(16, "city", NodeType.LEAF, DataType.STRING),

    ISBN(20, "isbn", NodeType.LEAF, DataType.STRING),
    TITLE(21, "title", NodeType.LEAF, DataType.STRING),

    SIZE(30, "size", NodeType.LEAF, DataType.INT),
    COLOR(31, "color", NodeType.LEAF, DataType.ENUM),


    // components
    DOG(1001, "dog", NodeType.CONTAINER, DataType.UNDEF,
            Set.of(NAME, AGE), Dog::new),

    ADDRESS(1002, "address", NodeType.CONTAINER, DataType.UNDEF,
            Set.of(STREET, ZIP, CITY), Address::new),

    BOOK(1003, "book", NodeType.CONTAINER, DataType.UNDEF,
            Set.of(ISBN, TITLE), Book::new),

    SHOE(1004, "shoe", NodeType.CONTAINER, DataType.UNDEF,
            Set.of(SIZE, COLOR), Shoe::new),

    // groups
    ADDRESS_GRP(2001, "addresses", NodeType.GROUP, DataType.UNDEF,
            Set.of(ADDRESS)),

    FAMILY_GRP(2002, "familyMembers", NodeType.GROUP, DataType.UNDEF,
            new int[]{3001}),

    BOOK_GRP(2003, "books", NodeType.GROUP, DataType.UNDEF,
            Set.of(BOOK)),

    SHOE_GRP(2004, "shoes", NodeType.GROUP, DataType.UNDEF,
            Set.of(SHOE)),


    // domain roots => component
    PERSON(3001, "person", NodeType.CONTAINER, DataType.UNDEF,
            Set.of(NAME, AGE, INCOME, GENDER, DOG, ADDRESS_GRP, BOOK_GRP, FAMILY_GRP),
            Person::new);


    //---------------------------------------------------------------------------
    // attributes
    //---------------------------------------------------------------------------

    private final int tagNumber;
    private final String propName;
    private final NodeType nodeType;
    private final DataType dataType;
    private final Function<Container, Container> modelFactory;
    private Set<Descriptor> children;
    private int[] childTags = new int[0]; // to avoid forward references during compile time


    //---------------------------------------------------------------------------
    // constructors
    //---------------------------------------------------------------------------
    // TODO (FRa) : (FRa): consolidate constructors
    SampleDescriptor(int tagNumber, String propName, NodeType nodeType, DataType type,
                     Set<SampleDescriptor> children,
                     Function<Container, Container> modelFactory) {
        this.tagNumber = tagNumber;
        this.propName = propName;
        this.dataType = type;
        this.nodeType = nodeType;
        this.modelFactory = modelFactory;
        this.children = Collections.unmodifiableSet(children);
    }

    SampleDescriptor(int tagNumber, String propName, NodeType nodeType, DataType type,
                     Set<SampleDescriptor> children) {
        this(tagNumber, propName, nodeType, type, children, null);
    }

    SampleDescriptor(int tagNumber, String propName, NodeType nodeType, DataType dataType) {
        this(tagNumber, propName, nodeType, dataType, Collections.emptySet(), null);
    }

    SampleDescriptor(int tagNumber, String propName, NodeType nodeType, DataType dataType, int[] childTags) {
        this(tagNumber, propName, nodeType, dataType);
        this.childTags = childTags;
    }

    //---------------------------------------------------------------------------
    // API
    //---------------------------------------------------------------------------


    private static Set<Descriptor> resolve(int[] childTag) {
        Map<Integer, Descriptor> all = new HashMap<>();
        for (SampleDescriptor value : values()) {
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

    @Override
    public int getTagNumber() {
        return tagNumber;
    }

    @Override
    public String alias() {
        return propName;
    }

    @Override
    public NodeType getNodeType() {
        return nodeType;
    }

    @Override
    public DataType getDataType() {
        return dataType;
    }

    @Override
    public Set<Descriptor> getChildren() {
        if (children == null && childTags.length > 0) {
            children = resolve(childTags);
        }

        return children;
    }

    @Override
    public Function<Container, Container> getDomainModelFactory() {
        return modelFactory;
    }

    @Override
    public Descriptor getDescriptor() { return this; }

    @Override
    public String shortName() {
        return name();
    }
}
