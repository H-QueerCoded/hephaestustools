package com.titammods.hephaestus_tools.table;

public final class ArsenalTableLayout {
    private ArsenalTableLayout() {}

    public static final int IMAGE_WIDTH = 384, IMAGE_HEIGHT = 256;
    public static final int INV_X = 112, INV_Y = 158;
    public static final int SLOT_W = 22, SLOT_H = 24, SLOT = SLOT_W;
    public static final int HEX_X = 118, HEX_Y = 56, HEX_W = 70, HEX_H = 79;
    public static final int CENTER_X = 153, CENTER_Y = 96;
    public static final int UPGRADE_SLOT_X = CENTER_X - SLOT_W / 2;
    public static final int UPGRADE_SLOT_Y = CENTER_Y - SLOT_H / 2;
    public static final int OUTPUT_X = 225, OUTPUT_Y = 83, OUTPUT_SIZE = 26;
    public static final int SLOT_ITEM_INSET = (SLOT_W - 16) / 2;
    public static final int SLOT_ITEM_INSET_Y = (SLOT_H - 16) / 2 - 1;
    public static final int OUTPUT_ITEM_INSET = (OUTPUT_SIZE - 16) / 2;

    public static int[][] hexSlotPositions() {
        return new int[][] {{142, 52}, {142, 116}, {110, 84}, {174, 84}};
    }
}
