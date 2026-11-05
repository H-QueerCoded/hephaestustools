package com.titammods.hephaestus_tools.tools.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record EnchantmentLimit(int maxLevel) {

    public static final Codec<EnchantmentLimit> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.intRange(0, 255).fieldOf("max_level").forGetter(EnchantmentLimit::maxLevel)
            ).apply(instance, EnchantmentLimit::new));
}
