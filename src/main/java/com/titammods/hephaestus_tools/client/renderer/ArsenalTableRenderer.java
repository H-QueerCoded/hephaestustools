package com.titammods.hephaestus_tools.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.titammods.hephaestus_tools.client.workbench.WorkbenchDrawer;
import com.titammods.hephaestus_tools.client.workbench.WorkbenchRenderer;
import com.titammods.hephaestus_tools.config.HephaestusConfig;
import com.titammods.hephaestus_tools.tables.blockentity.ArsenalTableBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.phys.AABB;

public class ArsenalTableRenderer implements BlockEntityRenderer<ArsenalTableBlockEntity> {
    private final ItemRenderer itemRenderer;

    public ArsenalTableRenderer(BlockEntityRendererProvider.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(ArsenalTableBlockEntity be, float partialTick, PoseStack pose,
                       MultiBufferSource buffer, int light, int overlay) {
        if (!HephaestusConfig.modernInterface()) return;
        WorkbenchDrawer.render(be, itemRenderer, pose, buffer, light, overlay);
        WorkbenchRenderer.renderItems(be, itemRenderer, pose, buffer, light, overlay);
    }

    @Override
    public AABB getRenderBoundingBox(ArsenalTableBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(2.0);
    }
}
