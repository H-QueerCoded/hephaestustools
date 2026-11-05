package com.titammods.hephaestus_tools.materials.tier;

import com.titammods.hephaestus_tools.HephaestusTools;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

public final class BuiltInTiers {

    public static final ResourceLocation WOOD = id("wood");
    public static final ResourceLocation STONE = id("stone");
    public static final ResourceLocation IRON = id("iron");
    public static final ResourceLocation DIAMOND = id("diamond");
    public static final ResourceLocation GOLD = id("gold");
    public static final ResourceLocation NETHERITE = id("netherite");
    public static final ResourceLocation ADAMANTITE = id("adamantite");
    public static final ResourceLocation AURICHALCUM = id("aurichalcum");

    private static final List<ResourceLocation> POSITIONS =
            List.of(WOOD, STONE, IRON, DIAMOND, GOLD, NETHERITE, ADAMANTITE, AURICHALCUM);

    private static final Map<ResourceLocation, TierDefinition> DEFINITIONS = build();

    private BuiltInTiers() {}

    public static Map<ResourceLocation, TierDefinition> definitions() {
        return DEFINITIONS;
    }

    public static Optional<TierDefinition> get(ResourceLocation id) {
        return Optional.ofNullable(DEFINITIONS.get(id));
    }

    public static ResourceLocation byPosition(int position) {
        return position < 0 || position >= POSITIONS.size() ? WOOD : POSITIONS.get(position);
    }

    public static OptionalInt position(ResourceLocation id) {
        int position = POSITIONS.indexOf(id);
        return position < 0 ? OptionalInt.empty() : OptionalInt.of(position);
    }

    private static Map<ResourceLocation, TierDefinition> build() {
        Map<ResourceLocation, TierDefinition> out = new LinkedHashMap<>();
        out.put(WOOD, new TierDefinition(0, BlockTags.INCORRECT_FOR_WOODEN_TOOL));
        out.put(STONE, new TierDefinition(1, BlockTags.INCORRECT_FOR_STONE_TOOL));
        out.put(IRON, new TierDefinition(2, BlockTags.INCORRECT_FOR_IRON_TOOL));
        out.put(DIAMOND, new TierDefinition(3, BlockTags.INCORRECT_FOR_DIAMOND_TOOL));
        out.put(GOLD, new TierDefinition(0, BlockTags.INCORRECT_FOR_GOLD_TOOL));
        out.put(NETHERITE, new TierDefinition(4, BlockTags.INCORRECT_FOR_NETHERITE_TOOL));
        out.put(ADAMANTITE, new TierDefinition(5, modTag("incorrect_for_adamantite_tool")));
        out.put(AURICHALCUM, new TierDefinition(6, modTag("incorrect_for_aurichalcum_tool")));
        return Collections.unmodifiableMap(out);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(HephaestusTools.MOD_ID, path);
    }

    private static TagKey<Block> modTag(String path) {
        return TagKey.create(Registries.BLOCK, id(path));
    }
}
