package com.titammods.hephaestus_tools.client.workbench;

import com.titammods.hephaestus_tools.HephaestusTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

public final class AnimHammer {
    public static final ModelResourceLocation MODEL =
            ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(HephaestusTools.MOD_ID, "special/anim_hammer"));

    private AnimHammer() {}

    public static BakedModel baked() { return Minecraft.getInstance().getModelManager().getModel(MODEL); }
}
