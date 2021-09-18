package net.styx.model.sample.meta;

import net.styx.model.meta.ComponentType;
import net.styx.model.meta.NodeType;
import net.styx.model.sample.Sex;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class Dictionary {

    // ------------------------------------------ declarations ---------------------------------------------------------

    public static NodeType<String> NAME = new ComponentType<>(100, "name");
    static NodeType<LocalDateTime> BIRTHDAY = new ComponentType<>(101, "birthDay");
    static NodeType<Sex> SEX = new ComponentType<>(102, "sex");

    static NodeType<List<String>> ACCOUNTS = new ComponentType<>(103, "accounts") {
        @Override
        public List<String> normalize(List<String> value) {
            return value != null ? Collections.unmodifiableList(value) : null;
        }
    };

    static NodeType<String> STREET = new ComponentType<>(110, "street");
    static NodeType<String> DESCRIPTION = new ComponentType<>(111, "description");

    static NodeType<Integer> ZIP = new ComponentType<>(112, "zip");
}
