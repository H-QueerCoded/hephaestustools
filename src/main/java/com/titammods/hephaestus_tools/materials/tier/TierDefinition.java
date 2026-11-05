package com.titammods.hephaestus_tools.materials.tier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public record TierDefinition(int miningLevel, TagKey<Block> incorrectFor) {

    public static final Codec<TierDefinition> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.optionalFieldOf("mining_level", 0).forGetter(TierDefinition::miningLevel),
                    TagKey.codec(Registries.BLOCK).fieldOf("incorrect_for").forGetter(TierDefinition::incorrectFor)
            ).apply(instance, TierDefinition::new));

    public static final StreamCodec<ByteBuf, TierDefinition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, TierDefinition::miningLevel,
            ResourceLocation.STREAM_CODEC.map(id -> TagKey.create(Registries.BLOCK, id), TagKey::location), TierDefinition::incorrectFor,
            TierDefinition::new
    );
}
