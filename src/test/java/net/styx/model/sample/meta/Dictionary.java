package net.styx.model.sample.meta;

import net.styx.model.meta.AttrDef;
import net.styx.model.sample.Sex;

import java.time.LocalDateTime;
import java.util.List;

public class Dictionary {

    // ------------------------------------------ declarations ---------------------------------------------------------

    static AttrDef<String> NAME = new AttrDef<>(100, "name", String.class);
    static AttrDef<LocalDateTime> BIRTHDAY = new AttrDef<>(101, "birthDay", LocalDateTime.class);
    static AttrDef<Sex> SEX = new AttrDef<>(102, "sex", Sex.class);
    static AttrDef<List<String>> ACCOUNTS = new AttrDef(103, "accounts", List.class);
    static AttrDef<String> STREET = new AttrDef<>(110, "street", String.class);
}
