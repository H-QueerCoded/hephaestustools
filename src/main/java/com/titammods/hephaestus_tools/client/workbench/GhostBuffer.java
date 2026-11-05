package com.titammods.hephaestus_tools.client.workbench;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.inventory.InventoryMenu;

final class GhostBuffer implements MultiBufferSource {
    private final MultiBufferSource delegate;
    private final float alpha, red, green, blue;

    GhostBuffer(MultiBufferSource delegate, float alpha, float red, float green, float blue) {
        this.delegate = delegate;
        this.alpha = alpha;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    public VertexConsumer getBuffer(RenderType ignored) {
        return new Tint(delegate.getBuffer(RenderType.entityTranslucentCull(InventoryMenu.BLOCK_ATLAS)));
    }

    private final class Tint implements VertexConsumer {
        private final VertexConsumer out;

        Tint(VertexConsumer out) { this.out = out; }

        @Override public VertexConsumer addVertex(float x, float y, float z) { out.addVertex(x, y, z); return this; }

        @Override public VertexConsumer setColor(int r, int g, int b, int a) {
            out.setColor(Math.round(r * red), Math.round(g * green), Math.round(b * blue), Math.round(a * alpha));
            return this;
        }

        @Override public VertexConsumer setUv(float u, float v) { out.setUv(u, v); return this; }
        @Override public VertexConsumer setUv1(int u, int v) { out.setUv1(u, v); return this; }
        @Override public VertexConsumer setUv2(int u, int v) { out.setUv2(u, v); return this; }
        @Override public VertexConsumer setNormal(float x, float y, float z) { out.setNormal(x, y, z); return this; }
    }
}
