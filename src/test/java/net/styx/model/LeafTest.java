package net.styx.model;

import net.styx.model.tree.leaf.StringLeaf;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class LeafTest {

    @Test
    void isDirtyOnConstructor() {
        assertThat(new StringLeaf().isChanged()).isFalse();
        assertThat(new StringLeaf("Frank").isChanged()).isTrue();
        assertThat(new StringLeaf("Frank", false).isChanged()).isFalse();
        assertThat(new StringLeaf("Frank", true).isChanged()).isTrue();
    }

    @DisplayName("!empty && !dirty -> newly instantiated (with markDirty=false) or after commit/rollback to value")
    @Test
    void isNotEmptyAndNotDirty() {
        var leaf = new StringLeaf("Frank", false);
        assertThat(leaf.isEmpty()).isFalse();
        assertThat(leaf.isChanged()).isFalse();

        leaf.setValueString("Laeti");
        assertThat(leaf.isChanged()).isTrue();

        leaf.commit();
        assertThat(leaf.isEmpty()).isFalse();
        assertThat(leaf.isChanged()).isFalse();
    }

    @DisplayName("empty && !dirty -> newly instantiated or after commit/rollback to $UNSET value")
    @Test
    void isEmptyAndNotDirty() {
        var leaf = new StringLeaf("Frank", true);
        assertThat(leaf.isEmpty()).isFalse();
        assertThat(leaf.isChanged()).isTrue();

        leaf.rollback();

        assertThat(leaf.isEmpty()).isTrue();
        assertThat(leaf.isChanged()).isFalse();
    }

    @DisplayName("!empty && dirty -> after new value was set")
    @Test
    void isNotEmptyAndDirty() {
        var leaf = new StringLeaf();
        assertThat(leaf.isEmpty()).isTrue();
        assertThat(leaf.isChanged()).isFalse();

        leaf.setValueString("Frank");

        assertThat(leaf.isEmpty()).isFalse();
        assertThat(leaf.isChanged()).isTrue();
    }

    @DisplayName("empty && dirty -> after $UNSET operation applied")
    @Test
    void isEmptyAndDirty() {
        var leaf = new StringLeaf("Frank", false);
        assertThat(leaf.isEmpty()).isFalse();
        assertThat(leaf.isChanged()).isFalse();

        leaf.setValueString(null);

        assertThat(leaf.isEmpty()).isTrue();
        assertThat(leaf.isChanged()).isTrue();
    }

    @Test
    void isDirtyOnMod() {
        var leaf = new StringLeaf("Frank", false);

        leaf.setValueString("Frank");
        assertThat(leaf.isChanged()).isFalse();

        leaf.setValueString("Laeti");
        assertThat(leaf.isChanged()).isTrue();

        leaf.setValueString("Frank");
        assertThat(leaf.isChanged()).isFalse();

        leaf.setValueString("Maria");
        assertThat(leaf.isChanged()).isTrue();

        leaf.setValueString("Frank");
        assertThat(leaf.isChanged()).isFalse();
    }


    @Test
    void isEmpty() {
        var leaf = new StringLeaf("Frank", false);
        assertThat(leaf.isEmpty()).isFalse();

        leaf.setValueString("Isabelle");
        assertThat(leaf.isEmpty()).isFalse();
        assertThat(leaf.isChanged()).isTrue();

        leaf.setValueString(null);
        assertThat(leaf.isEmpty()).isTrue();
        assertThat(leaf.isChanged()).isTrue();
    }

    @Test
    void commitFromNullStart() {
        var leaf = new StringLeaf("Frank");
        assertThat(leaf.isChanged()).isTrue();
        leaf.commit();
        assertThat(leaf.isChanged()).isFalse();
        assertThat(leaf.getValueString()).isEqualTo("Frank");
        assertThat(leaf.isEmpty()).isFalse();
    }

    @Test
    void commitAfterMod() {
        var leaf = new StringLeaf("Frank", false);
        assertThat(leaf.isChanged()).isFalse();
        leaf.setValueString("Laeti");
        assertThat(leaf.isChanged()).isTrue();

        leaf.commit();

        assertThat(leaf.isChanged()).isFalse();
        assertThat(leaf.getValueString()).isEqualTo("Laeti");
        assertThat(leaf.isEmpty()).isFalse();
    }


    @Test
    void rollbackFromNullStart() {
        var leaf = new StringLeaf("Frank");
        assertThat(leaf.isChanged()).isTrue();
        leaf.rollback();
        assertThat(leaf.isChanged()).isFalse();
        assertThat(leaf.getValueString()).isNull();
        assertThat(leaf.isEmpty()).isTrue();
    }

    @Test
    void rollbackAfterMod() {
        var leaf = new StringLeaf("Frank", false);
        assertThat(leaf.isChanged()).isFalse();
        leaf.setValueString("Laeti");
        assertThat(leaf.isChanged()).isTrue();

        leaf.rollback();

        assertThat(leaf.isChanged()).isFalse();
        assertThat(leaf.getValueString()).isEqualTo("Frank");
        assertThat(leaf.isEmpty()).isFalse();
    }
}
