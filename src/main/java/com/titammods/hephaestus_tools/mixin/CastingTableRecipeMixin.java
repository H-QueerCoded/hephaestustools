package com.titammods.hephaestus_tools.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.titammods.hephaestus_tools.HephaestusTools;
import com.titammods.hephaestus_tools.materials.MaterialId;
import com.titammods.hephaestus_tools.registry.ModComponents;
import com.titammods.hephaestus_tools.tools.part.ToolPartItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.titammods.setup.ModRecipes$CastingTableRecipe", remap = false)
public abstract class CastingTableRecipeMixin {

    private static final String MOLTEN_PREFIX = "molten_";

    @Shadow public abstract FluidStack fluid();

    @ModifyReturnValue(method = "result", at = @At("RETURN"))
    private ItemStack hephaestusTools$applyPartMaterial(ItemStack result) {
        if (!(result.getItem() instanceof ToolPartItem)) return result;
        FluidStack fluid = this.fluid();
        if (fluid == null || fluid.isEmpty()) return result;
        ResourceLocation fluidId = BuiltInRegistries.FLUID.getKey(fluid.getFluid());
        if (fluidId == null) return result;

        String path = fluidId.getPath();
        if (path.startsWith(MOLTEN_PREFIX)) path = path.substring(MOLTEN_PREFIX.length());

        ItemStack copy = result.copy();
        copy.set(ModComponents.PART_MATERIAL.get(), MaterialId.of(HephaestusTools.MOD_ID, path));
        return copy;
    }
}
