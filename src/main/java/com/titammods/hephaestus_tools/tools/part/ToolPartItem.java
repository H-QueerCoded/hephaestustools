package com.titammods.hephaestus_tools.tools.part;

import com.titammods.hephaestus_tools.materials.MaterialId;
import com.titammods.hephaestus_tools.materials.MaterialManager;
import com.titammods.hephaestus_tools.registry.ModComponents;
import com.titammods.hephaestus_tools.materials.Material;
import com.titammods.hephaestus_tools.materials.trait.ToolTraits;
import com.titammods.hephaestus_tools.materials.trait.ToolTraits.TraitInstance;
import com.titammods.hephaestus_tools.tools.helper.ToolTooltipBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ToolPartItem extends Item {

    private final int partSlot;

    public ToolPartItem(Properties props, int partSlot) {
        super(props);
        this.partSlot = partSlot;
    }

    public int getPartSlot() {
        return partSlot;
    }

    public MaterialId getMaterial(ItemStack stack) {
        MaterialId mat = stack.get(ModComponents.PART_MATERIAL.get());
        return mat == null ? MaterialId.EMPTY : mat;
    }

    public ItemStack withMaterial(MaterialId material) {
        ItemStack stack = new ItemStack(this);
        stack.set(ModComponents.PART_MATERIAL.get(), material);
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        MaterialId mat = getMaterial(stack);
        if (mat.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.hephaestus_tools.part.no_material")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }

        Material material = MaterialManager.getInstance().getMaterial(mat);
        int color = material != null ? material.color() & 0xFFFFFF : 0xFFFFFF;
        tooltip.add(Component.translatable("tooltip.hephaestus_tools.material",
                        Component.translatable(mat.translationKey())
                                .withStyle(s -> s.withColor(color)))
                .withStyle(ChatFormatting.GRAY));

        boolean shift = Screen.hasShiftDown();
        for (TraitInstance trait : ToolTraits.ofPart(mat, partSlot)) {
            tooltip.add(ToolTooltipBuilder.traitName(trait)
                    .withStyle(ChatFormatting.DARK_AQUA));
            if (shift) {
                tooltip.add(Component.literal("  ")
                        .append(Component.translatable(ToolTraits.descriptionKey(trait.id()))
                                .withStyle(ChatFormatting.DARK_GRAY)));
            }
        }

        if (shift) {
            tooltip.add(Component.empty());
            ToolTooltipBuilder.partStats(mat, partSlot).forEach(tooltip::add);
        } else {
            tooltip.add(Component.empty());
            tooltip.add(ToolTooltipBuilder.holdShift());
        }
    }
}