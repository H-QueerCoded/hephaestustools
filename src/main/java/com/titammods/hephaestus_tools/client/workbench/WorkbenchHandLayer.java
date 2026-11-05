package com.titammods.hephaestus_tools.client.workbench;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class WorkbenchHandLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public WorkbenchHandLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffer, int light, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        Minecraft mc = Minecraft.getInstance();
        ItemStack stack;
        boolean hammer;
        if (player == mc.player) {
            if (!WorkbenchSession.isRunning()) return;
            stack = WorkbenchCraft.handItem();
            hammer = WorkbenchCraft.holdsHammer();
        } else {
            stack = RemoteCraft.handItem(player.getId());
            hammer = !stack.isEmpty() && stack.getItem() == com.titammods.hephaestus_tools.registry.ModItems.SLEDGE_HAMMER.get();
        }
        if (stack.isEmpty()) return;

        pose.pushPose();

        getParentModel().translateToHand(HumanoidArm.RIGHT, pose);
        pose.mulPose(Axis.XP.rotationDegrees(-90.0f));
        pose.mulPose(Axis.YP.rotationDegrees(180.0f));
        pose.translate(1.0f / 16.0f, 0.125f, -0.625f);
        float turn = player == mc.player ? WorkbenchCraft.admireTurn() : 0.0f;
        if (turn != 0.0f) pose.mulPose(Axis.YP.rotationDegrees(turn));
        if (hammer) {
            mc.getItemRenderer().render(stack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, pose, buffer, light,
                    OverlayTexture.NO_OVERLAY, AnimHammer.baked());
        } else {
            mc.getItemRenderer().renderStatic(player, stack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false,
                    pose, buffer, player.level(), light, OverlayTexture.NO_OVERLAY, player.getId());
        }
        pose.popPose();
    }
}
