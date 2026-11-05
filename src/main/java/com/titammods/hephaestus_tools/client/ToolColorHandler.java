package com.titammods.hephaestus_tools.client;

import com.titammods.hephaestus_tools.materials.Material;
import com.titammods.hephaestus_tools.materials.MaterialAnimation;
import com.titammods.hephaestus_tools.materials.MaterialId;
import com.titammods.hephaestus_tools.materials.MaterialManager;
import com.titammods.hephaestus_tools.tools.nbt.ToolStack;
import net.minecraft.Util;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public final class ToolColorHandler implements ItemColor {

    public static final ToolColorHandler INSTANCE = new ToolColorHandler();

    private static final int NO_TINT = 0xFFFFFFFF;

    private ToolColorHandler() {}

    public static float animationTicks() {
        return Util.getMillis() / 50f;
    }

    private static @Nullable Material material(ItemStack stack, int tintIndex) {
        if (tintIndex < 0 || !ToolStack.isInitialized(stack)) return null;
        int materialIndex = ToolLayerMap.materialIndexForLayer(stack.getItem(), tintIndex);
        if (materialIndex < 0) return null;
        MaterialId materialId = ToolStack.getMaterial(stack, materialIndex);
        if (materialId == null || materialId.isEmpty()) return null;
        return MaterialManager.getInstance().getMaterial(materialId);
    }

    public @Nullable MaterialAnimation getAnimation(ItemStack stack, int tintIndex) {
        Material material = material(stack, tintIndex);
        return material == null ? null : material.animation().orElse(null);
    }

    public int getAnimatedColor(ItemStack stack, int tintIndex) {
        int color = getColor(stack, tintIndex);
        MaterialAnimation animation = getAnimation(stack, tintIndex);
        return animation == null ? color : animation.uniform(color, animationTicks());
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        Material material = material(stack, tintIndex);
        if (material == null) {
            return NO_TINT;
        }

        int color = material.color();
        if ((color & 0xFF000000) == 0) {
            color |= 0xFF000000;
        }
        return color;
    }
}