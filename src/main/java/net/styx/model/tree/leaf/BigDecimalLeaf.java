package net.styx.model.tree.leaf;

import net.styx.model.meta.Descriptor;
import net.styx.model.meta.NodeID;
import net.styx.model.tree.Leaf;

import java.math.BigDecimal;

public class BigDecimalLeaf extends AbstractLeaf<BigDecimal> {

    public static final Leaf EMPTY_VAL = new BigDecimalLeaf(Descriptor.UNDEF);

    public BigDecimalLeaf(NodeID nodeID) {
        super(nodeID);
    }

    public BigDecimalLeaf(NodeID nodeID, BigDecimal value) {
        super(nodeID, value);
    }

    @Override
    public void setValueBigDec(BigDecimal val) {
        setValue(val);
    }

    @Override
    protected boolean same(BigDecimal b1, BigDecimal b2) {
        return (b1 == null && b2 == null) ||
                (b1 != null && b2 != null && b1.compareTo(b2) == 0);
    }

    @Override
    public BigDecimal getValueBigDec() {
        return getValue();
    }

    @Override
    public void setValueLeaf(Leaf from) {
        setValueBigDec(from.getValueBigDec());
    }
}
