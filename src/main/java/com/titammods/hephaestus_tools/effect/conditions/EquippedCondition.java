package com.titammods.hephaestus_tools.effect.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.hephaestus_tools.effect.EffectCondition;
import com.titammods.hephaestus_tools.effect.EffectContext;
import net.minecraft.world.entity.EquipmentSlotGroup;

public record EquippedCondition(EquipmentSlotGroup slot) implements EffectCondition {

    public static final MapCodec<EquippedCondition> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    EquipmentSlotGroup.CODEC.fieldOf("slot").forGetter(EquippedCondition::slot)
            ).apply(instance, EquippedCondition::new));

    @Override
    public MapCodec<EquippedCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(EffectContext context) {
        return context.slot() != null && slot.test(context.slot());
    }
}
