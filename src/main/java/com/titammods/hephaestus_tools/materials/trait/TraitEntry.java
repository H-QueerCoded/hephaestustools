package com.titammods.hephaestus_tools.materials.trait;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.hephaestus_tools.materials.DefinitionId;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record TraitEntry(ResourceLocation id, int level) {

    public static final Codec<TraitEntry> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    DefinitionId.CODEC.fieldOf("id").forGetter(TraitEntry::id),
                    Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("level", 1).forGetter(TraitEntry::level)
            ).apply(instance, TraitEntry::new));

    public static final StreamCodec<ByteBuf, TraitEntry> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, TraitEntry::id,
            ByteBufCodecs.VAR_INT, TraitEntry::level,
            TraitEntry::new
    );
}
