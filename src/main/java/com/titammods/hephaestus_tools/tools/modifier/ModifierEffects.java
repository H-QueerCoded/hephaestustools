package com.titammods.hephaestus_tools.tools.modifier;

import com.titammods.hephaestus_tools.materials.tier.BuiltInTiers;
import com.titammods.hephaestus_tools.tools.stat.HarvestTier;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ModifierEffects {

    private ModifierEffects() {}

    private static final String NS = "hephaestus_tools";
    private static final Map<ResourceLocation, ModifierEffect> EFFECTS = new LinkedHashMap<>();

    public static final ResourceLocation DIAMOND    = id("diamond");
    public static final ResourceLocation EMERALD    = id("emerald");
    public static final ResourceLocation LAPIS      = id("lapis");
    public static final ResourceLocation NETHERITE  = id("netherite");
    public static final ResourceLocation HASTE      = id("haste");
    public static final ResourceLocation REINFORCED = id("reinforced");
    public static final ResourceLocation FORTUNE    = id("fortune");
    public static final ResourceLocation SHARPNESS  = id("sharpness");
    public static final ResourceLocation LOOTING    = id("looting");
    public static final ResourceLocation SILK_TOUCH = id("silk_touch");
    public static final ResourceLocation FLAME      = id("flame");
    public static final ResourceLocation SWEEPING   = id("sweeping");

    static {
        register(DIAMOND, (stats, level) -> {
            stats.addDurability(500);
            stats.addAttackDamage(0.5f);
            stats.addMiningSpeed(2);
            stats.setHarvestTier(HarvestTier.of(BuiltInTiers.DIAMOND));
        });

        register(EMERALD, (stats, level) -> {
            stats.multiplyDurability(0.5f);
            stats.multiplyAttackDamage(0.25f);
            stats.multiplyMiningSpeed(0.25f);
            stats.setHarvestTier(HarvestTier.of(BuiltInTiers.IRON));
        });

        register(LAPIS, (stats, level) -> {
            int capped = Math.min(level, 3);
            stats.fortune += capped;
            stats.looting += capped;
        });

        register(NETHERITE, (stats, level) -> {
            stats.multiplyDurability(0.2f);
            stats.multiplyAttackDamage(0.2f);
            stats.multiplyMiningSpeed(0.25f);
            stats.setHarvestTier(HarvestTier.of(BuiltInTiers.NETHERITE));
            stats.indestructible = true;
        });

        register(HASTE, (stats, level) -> stats.addMiningSpeed(4.0f * level));

        register(REINFORCED, (stats, level) -> stats.addDurability(250f * level));
        register(FORTUNE,    (stats, level) -> stats.fortune += level);
        register(SHARPNESS,  (stats, level) -> stats.addAttackDamage(1.0f * level));
        register(LOOTING,    (stats, level) -> stats.looting += level);
        register(SILK_TOUCH, (stats, lvl) -> {});
        register(FLAME,      (stats, lvl) -> {});
        register(SWEEPING,   (stats, lvl) -> {});
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(NS, path);
    }

    private static void register(ResourceLocation id, ModifierEffect effect) {
        EFFECTS.put(id, effect);
    }

    public static ModifierEffect get(ResourceLocation id) {
        return EFFECTS.get(id);
    }

    public static boolean isKnown(ResourceLocation id) {
        return EFFECTS.containsKey(id);
    }
}
