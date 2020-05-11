package net.styx.model.tree.traverse;

import net.styx.model.tree.Leaf;

import java.math.BigDecimal;

public class ImmutableLeaf extends ImmutableNode<Leaf> implements Leaf {

    public ImmutableLeaf(Leaf leaf) {
        super(leaf);
    }

    @Override
    public void setValueString(String value) {
        prevent();
    }

    @Override
    public String getValueString() {
        return node.getValueString();
    }

    @Override
    public void setValueInt(int value) {
        prevent();
    }

    @Override
    public int getValueInt() {
        return node.getValueInt();
    }

    @Override
    public void setValueBigDec(BigDecimal value) {
        prevent();
    }

    @Override
    public BigDecimal getValueBigDec() {
        return node.getValueBigDec();
    }

    @Override
    public <T extends Enum<T>> void setValueEnum(T value) {
        prevent();
    }

    @Override
    public <T extends Enum<T>> T getValueEnum() {
        return node.getValueEnum();
    }

    @Override
    public void setValueLong(Long value) {
        prevent();
    }

    @Override
    public Long getValueLong() {
        return node.getValueLong();
    }

    @Override
    public void setValueLeaf(Leaf from) {
        prevent();
    }
}
