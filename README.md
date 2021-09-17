# Intro
Applications work with domain models that are usually very specific to their target domain. Yet, they
often lack generic APIs. This is pretty natural, since the closer the model is to a problem domain, 
the harder it is to keep it generic.
The project serves as a playground to work out a design that supports a number of features along a
simple sample model. By model, we mean **object graph**.
Ultimately the building blocks will be combined with code generators.

Of course the design is very opinionated as features always come on a price!

## Getting started
- You need JDK >= v11
- Clone this repo
- Current status: very raw yet but concepts start to emerge

## Cool features
- Define arbitrary types and use them as node values - as long as they are immutable.
- Collections of Node elements can form sub-tree as separate nodes. 

## Who might be interested?
In the first place it is a personal experimental ground. You can borrow ideas and concepts but 
nothing is "ready-as-a-lib".

In case you have a rather sophisticated (acyclic) graph-like data model and need to:
- track changes
- support diffing between versions
- want to serialize your (sub)graph without resorting to reflection
- compress your graph to the min attribute set
- support transactional semantic on your graph with commit/rollback
- describe it by a meta-model
- want a generic structure complementing your domain model
- Simple! It's just a tree! No annotations, reflection, byte code manipulation.

## Typical use cases
- model manipulated in state engines
- model derived from industry specifications
- central governance of models in a structured format (e.g. xml, json, xls)
- if you prefer design patterns over annotations and reflections ;)

## Constraints
- every graph has 1 controlling root
- associations between multiple roots have to be resolved externally (e.g. via repository lookup)
- every aggregate is self-contained: no sharing of nodes across aggregates - except immutable leaf values
- is an acyclic tree

## Sample domain model
The goal is to show-case standard requirements and operations. 
Such as:
- values ... primitives and specific types. Leaves in a graph, not components and not groups.
- components (container) ... static combination of values and other containers. Similar to a class.
- group (container) ... dyn combination of other containers 
- associations ... form a graph of containers
- idiomatic ... the model should feel natural and not impaired by an underlying framework

### Everyone knows "Person-has" stuff
The sample domain makes use of basic attributes (String, BigDecimal, long, enums) and other immutable complex value types (Money, Collection).
 
![Sample model entities](./doc/pics/1_small.png)

### Relationship types
- 0..1 ... optional association by unique reference from 1 container to another. Default name is type name (Person -> Dog)
- 0..n ... from 1 container to (potentially empty) collection of containers by default name (Person -> Books) 
- 0..1 ... different semantic containers type by reference (Person -> home; Person -> work)
- 0..n ... different semantic collections type by reference (Person -> leisureShoes; Person -> businessShoes)
- 0..n ... recursive relation to collection of same type (Person -> family)

![Relationships](./doc/pics/2_small.png)

### Backing data structures

The semantic wrappers of the sample model all delegate to a few data data structures. 
- value is represented by **Leaf** -> matching fields in classes
- values are contained in **Node**. A node contain contained other nodes. -> matching classes
- **Group** is the collection to reflect 0..n relationships. A group can be made up of nodes.
- **Ref** serve as a named (and uniquely tagged) alias to other primitives (leaf, node, group).

![Backing data structures](./doc/pics/3_small.png)


## Features
The domain model is a set of data classes. Support non-cyclical object graphs where classes are 
declared in a generic way to allow code generation.

- [x] The model is described by a meta model which is generated as well. The meta model 
    enables generic access and manipulation of the target model. The meta model will be generated as 
    well.

- [ ] Values are reflected by **leaves** in the graph. 'Person.name' is obviously different from 
    'Person.Dog.name' (Person.name vs. Dog.name). Their identity is determined by their path in the 
    graph. 

- [x] All access to the proprietary domain model is strongly typed.

- [x] Values are atomic whereas **Containers** are a collection of values or nodes.

- [ ] Domain objects offer different types of access:
    - random semantic access ... e.g. Person.setName("Joe")
    - generic access ... Person.getLeaf(MetaPerson.NAME).setValue("Joe")
    - sequential generic access ... iterate (recursively) over all properties of Person

- [ ] Every node/leaf in the graph can be addressed by forming a path of the meta-model and it's 
    location in the graph.

- [ ] Minimize memory footprint. Empty containers can be discarded - including 
    their references. If 'Person' has just the attribute 'name' set, no memory will be allocated for 
    any other domain attribute. Be aware there is a certain overhead in this approach, so small containers might not pay off

- [ ] Provide NullPointer safety by lazy initialization. Traversing 'Person.Dog.name' will create an 
    empty 'Dog' node, if it does not exist yet. This applies to all kind of nodes. If node is left 
    empty it will be automatically removed. (Might be configurable behaviour to avoid garbage) 
 
- All nodes/leaves support recursive operations as outlined below. Access is provided as described in 
    "types of access"
    - [x] empty ... does not carry domain data
    - [x] changed ... data was un/set (a.k.a "dirty flag")
    - [x] commit/rollback ... to apply/revert recent model changes in memory
    - [ ] diff ... extract changed structures
    - [x] freeze ... prevent a node/leaf to be mutated
    - [ ] copy constructors ... to create im/mutable clones
    
 
## Optional features
... more unlikely to be addressed here, since this is more application specific.
- [ ] Pending values ... are of interest in scenarios that involve review cycles.
    E.g. a requested 'Person.name' change might involve an approval / reject step. So until the name 
    change is approved the new name is pending (= inactive). On approval - the current name is replaced 
    with the new one. If rejected - the new name value is discarded and the current one is preserved. 
    If multiple such fields exist, a generic API activates/discards all pending values.

- [ ] Rippling attributes (or complete structures) ... in case attributes are duplicated across nodes
    in a tree, they have to be kept in sync. E.g. 'last name' needs to be propagated across all persons
    in this tree. Such functionality could also be kept separate from core model. Having a good concept
    of "pseudo inheritance" of attributes (leaf/node) offers potentially great performance improvements 
    for deeper but uniform trees. Left open:
    - ripple up/down
    - link instead of copy
    - define ripple boundaries
    - reflect in declarative model to allow generation
   

## Optimization options
- propagate dirty flags up to root to mark dirty paths. Consider mark dirty and clean
- cache predictable objects (deterministic paths (everything that does not involve 'groups'))
- init maps on demand: e.g. DataContainer, Group

## Gaps in model design
- make it usable as library vs. framework

# Test cases
## Group
- [ ] create C1 and add to G1 twice. Expect 2 elements added in G1. Expect both elements are equal (probably not)
- [ ] create C1, C2. Add them to G1. Remove C1 and C2 from G1. Expect C1, C2 to be reverted to initial state. 
- [ ] deep tree operations. Group of groups: G1 ->* G2 -> C1. G1 -> C1 -> C2 & A1. Check Consistency compared to POJO.
- [ ] a container can only be added once to the graph. Adding the same node to another level, will change its path (and contained attributes path). Adding a container (that is already part of the group) to a group twice should be prevented.

## Performance
- [ ] CRUD down to certain depth
- check if inplace update of map is possible avoid recreation of Map.Entry