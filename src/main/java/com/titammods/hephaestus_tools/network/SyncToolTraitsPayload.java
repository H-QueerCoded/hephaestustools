package com.titammods.hephaestus_tools.network;

import com.titammods.hephaestus_tools.HephaestusTools;
import com.titammods.hephaestus_tools.materials.trait.ToolTraitManager;
import com.titammods.hephaestus_tools.materials.trait.TraitEntry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record SyncToolTraitsPayload(Map<ResourceLocation, List<TraitEntry>> traits) implements CustomPacketPayload {

    public static final Type<SyncToolTraitsPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(HephaestusTools.MOD_ID, "sync_tool_traits")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncToolTraitsPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.<RegistryFriendlyByteBuf, ResourceLocation, List<TraitEntry>, Map<ResourceLocation, List<TraitEntry>>>map(
                    LinkedHashMap::new, ResourceLocation.STREAM_CODEC, TraitEntry.STREAM_CODEC.apply(ByteBufCodecs.list())),
            SyncToolTraitsPayload::traits,
            SyncToolTraitsPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncToolTraitsPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ToolTraitManager.receiveSync(payload.traits()));
    }
}