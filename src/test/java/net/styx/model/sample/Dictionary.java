package net.styx.model.sample;

import net.styx.model.meta.ComponentType;
import net.styx.model.meta.NodeType;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class Dictionary {

    // ------------------------------------------ declarations ---------------------------------------------------------

    public static NodeType<String> NAME = new ComponentType<>(100, "name");
    public static NodeType<LocalDateTime> BIRTHDAY = new ComponentType<>(101, "birthDay");
    public static NodeType<Sex> SEX = new ComponentType<>(102, "sex");

    public static NodeType<List<String>> ACCOUNTS = new ComponentType<>(103, "accounts") {
        @Override
        public List<String> normalize(List<String> value) {
            return value != null ? Collections.unmodifiableList(value) : null;
        }
    };

    public static NodeType<String> STREET = new ComponentType<>(110, "street");
    public static NodeType<String> DESCRIPTION = new ComponentType<>(111, "description");

    public static NodeType<Integer> ZIP = new ComponentType<>(112, "zip");
}
