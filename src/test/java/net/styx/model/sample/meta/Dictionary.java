package net.styx.model.sample.meta;

import net.styx.model.meta.ComponentDef;
import net.styx.model.meta.NodeDef;
import net.styx.model.sample.Sex;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class Dictionary {

    // ------------------------------------------ declarations ---------------------------------------------------------

    public static NodeDef<String> NAME = new ComponentDef<>(100, "name");
    static NodeDef<LocalDateTime> BIRTHDAY = new ComponentDef<>(101, "birthDay");
    static NodeDef<Sex> SEX = new ComponentDef<>(102, "sex");

    static NodeDef<List<String>> ACCOUNTS = new ComponentDef<>(103, "accounts") {
        @Override
        public List<String> normalize(List<String> value) {
            return value != null ? Collections.unmodifiableList(value) : null;
        }
    };

    static NodeDef<String> STREET = new ComponentDef<>(110, "street");
    static NodeDef<String> DESCRIPTION = new ComponentDef<>(111, "description");

    static NodeDef<Integer> ZIP = new ComponentDef<>(112, "zip");
}
