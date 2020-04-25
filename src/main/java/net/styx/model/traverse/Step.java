package net.styx.model.traverse;

import net.styx.model.meta.Descriptor;

import java.util.Optional;

public class Step {
    private final Descriptor descriptor;
    private final Object groupElemKey;

    public Step(Descriptor descriptor, Object groupElemKey) {
        this.descriptor = descriptor;
        this.groupElemKey = groupElemKey;
    }

    public Step(Descriptor descriptor) {
        this(descriptor, null);
    }

    public Descriptor getDescriptor() {
        return descriptor;
    }

    public Optional<?> getGroupElemKey() {
        return Optional.ofNullable(groupElemKey);
    }
}
