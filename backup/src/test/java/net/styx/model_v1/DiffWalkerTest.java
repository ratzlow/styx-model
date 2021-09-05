package net.styx.model_v1;

import net.styx.model_v1.sample.SampleDescriptor;
import net.styx.model_v1.tree.Container;
import net.styx.model_v1.tree.DefaultContainer;
import net.styx.model_v1.tree.Nodes;
import net.styx.model_v1.tree.diff.TreeDiff;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class DiffWalkerTest {

    @Test
    void newRoot() {
        Container person = new DefaultContainer(SampleDescriptor.PERSON);
        TreeDiff diff = Nodes.diff(person);
        assertThat(diff).isNotNull();
        assertThat(diff.getRoot().getNodeID()).isEqualTo(SampleDescriptor.PERSON);
        assertThat(diff.getRoot().getOperation()).isEqualTo(TreeDiff.Operation.ADD);

    }
}
