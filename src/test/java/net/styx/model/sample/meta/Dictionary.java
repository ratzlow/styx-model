package net.styx.model.sample.meta;

import net.styx.model.meta.AttrDef;
import net.styx.model.sample.Sex;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class Dictionary {

    // ------------------------------------------ declarations ---------------------------------------------------------

    public static AttrDef<String> NAME = new AttrDef<>(100, "name");
    static AttrDef<LocalDateTime> BIRTHDAY = new AttrDef<>(101, "birthDay");
    static AttrDef<Sex> SEX = new AttrDef<>(102, "sex");

    static AttrDef<List<String>> ACCOUNTS = new AttrDef<>(103, "accounts") {
        @Override
        public List<String> normalize(List<String> value) {
            return value != null ? Collections.unmodifiableList(value) : null;
        }
    };

    static AttrDef<String> STREET = new AttrDef<>(110, "street");
    static AttrDef<String> DESCRIPTION = new AttrDef<>(111, "description");
    static AttrDef<Integer> ZIP = new AttrDef<>(112, "zip");
}
