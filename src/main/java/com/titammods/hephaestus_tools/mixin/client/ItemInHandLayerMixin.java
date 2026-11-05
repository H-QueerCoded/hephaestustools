package com.titammods.hephaestus_tools.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.titammods.hephaestus_tools.client.workbench.RemoteCraft;
import com.titammods.hephaestus_tools.client.workbench.WorkbenchSession;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public abstract class ItemInHandLayerMixin {
    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At("HEAD"), cancellable = true)
    private void hephaestusTools$hideInScene(PoseStack pose, MultiBufferSource buffer, int light, LivingEntity entity,
                                             float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                                             float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (entity == Minecraft.getInstance().player && WorkbenchSession.isRunning()) ci.cancel();
        else if (RemoteCraft.isActive(entity.getId())) ci.cancel();
    }
}
