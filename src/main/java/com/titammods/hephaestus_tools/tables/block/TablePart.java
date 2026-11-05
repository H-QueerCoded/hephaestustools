package com.titammods.hephaestus_tools.tables.block;

import net.minecraft.util.StringRepresentable;

public enum TablePart implements StringRepresentable {
    MAIN("main"), SECOND("second");
    private final String name;
    TablePart(String n) { this.name = n; }
    @Override public String getSerializedName() { return name; }
}
