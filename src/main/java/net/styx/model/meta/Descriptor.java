package net.styx.model.meta;

import net.styx.model.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toUnmodifiableSet;

// TODO (FRa) : (FRa): ID should be immutable in certain ctx
// TODO (FRa) : (FRa): encapsulate techn. Attributes in component? entities with
// TODO (FRa) : (FRa): ID must have?! version?
public enum Descriptor {

    /**
     * default unset value
     */
    UNDEF(-1, "undefinedNode", DataType.INT, Integer.class),

    // technical fields
    ID(5, "id", DataType.LONG, Long.class),
    VERSION(6, "version", DataType.INT, Integer.class),

    // business fields fields
    NAME(10, "name", DataType.STRING, String.class),
    AGE(11, "age", DataType.INT, Integer.class),
    INCOME(12, "income", DataType.BIG_DECIMAL, BigDecimal.class),
    GENDER(13, "gender", DataType.ENUM, Gender.class),

    STREET(14, "street", DataType.STRING, String.class),
    ZIP(15, "zip", DataType.INT, Integer.class),
    CITY(16, "city", DataType.STRING, String.class),

    ISBN(20, "isbn", DataType.STRING, String.class),
    TITLE(21, "title", DataType.STRING, String.class),

    SIZE(30, "size", DataType.STRING, Integer.class),
    COLOR(31, "color", DataType.ENUM, Integer.class),


    // components
    DOG(1001, "dog", DataType.COMPONENT, Dog.class,
            Set.of(NAME, AGE)),

    ADDRESS(1002, "address", DataType.COMPONENT, Address.class,
            Set.of(STREET, ZIP, CITY), ID),

    BOOK(1003, "book", DataType.COMPONENT, Book.class,
            Set.of(ISBN, TITLE), ID),

    SHOE(1004, "shoe", DataType.COMPONENT, Shoe.class,
            Set.of(ID, SIZE, COLOR)),

    // groups
    ADDRESS_GRP(2001, "addresses", DataType.GROUP, Collection.class,
            Set.of(ADDRESS)),

    FAMILY_GRP(2002, "familyMembers", DataType.GROUP, Collection.class,
            new int[]{3001}),

    BOOK_GRP(2003, "books", DataType.GROUP, Collection.class,
            Set.of(BOOK)),

    SHOE_GRP(2004, "shoes", DataType.GROUP, Collection.class,
            Set.of(SHOE)),


    // domain roots => component
    PERSON(3001, "person", DataType.COMPONENT, Person.class,
            Set.of(NAME, AGE, INCOME, GENDER, DOG, ADDRESS_GRP, BOOK_GRP, FAMILY_GRP));


    //---------------------------------------------------------------------------
    // attributes
    //---------------------------------------------------------------------------

    private final int tagNumber;
    private final String propName;
    private final DataType dataType;
    private final Class<?> definingClass;
    private Set<Descriptor> children;
    private int[] childTags = new int[0];
    private final Optional<Descriptor> idKey;


    //---------------------------------------------------------------------------
    // constructors
    //---------------------------------------------------------------------------
    // TODO (FRa) : (FRa): consolidate constructors
    Descriptor(int tagNumber, String propName, DataType type, Class<?> clazz, Set<Descriptor> children) {
        this.tagNumber = tagNumber;
        this.propName = propName;
        this.dataType = type;
        this.definingClass = clazz;
        this.children = Collections.unmodifiableSet(children);
        this.idKey = Optional.empty();
    }

    Descriptor(int tagNumber, String propName, DataType type, Class<?> clazz) {
        this(tagNumber, propName, type, clazz, Collections.emptySet());
    }

    Descriptor(int tagNumber, String propName, DataType type, Class<?> clazz, int[] childTags) {
        this(tagNumber, propName, type, clazz);
        this.childTags = childTags;
    }

    Descriptor(int tagNumber, String propName, DataType type, Class<?> clazz, Set<Descriptor> children,
               Descriptor idKey) {
        this.tagNumber = tagNumber;
        this.propName = propName;
        this.dataType = type;
        this.definingClass = clazz;
        this.idKey = Optional.ofNullable(idKey);
        this.children = Stream.concat(children.stream(), this.idKey.stream()).collect(toUnmodifiableSet());
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

    public Optional<Descriptor> getIDKey() {
        return idKey;
    }
}
