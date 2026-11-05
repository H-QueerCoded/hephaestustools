package com.titammods.hephaestus_tools.registry;

import com.titammods.hephaestus_tools.HephaestusTools;
import com.titammods.hephaestus_tools.tools.helper.EnchantmentLimit;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

public final class ModDataMaps {

    public static final DataMapType<Enchantment, EnchantmentLimit> ENCHANTMENT_LIMITS = DataMapType.builder(
                    ResourceLocation.fromNamespaceAndPath(HephaestusTools.MOD_ID, "enchantment_limits"),
                    Registries.ENCHANTMENT, EnchantmentLimit.CODEC)
            .synced(EnchantmentLimit.CODEC, false)
            .build();

    private ModDataMaps() {}

    public static void register(RegisterDataMapTypesEvent event) {
        event.register(ENCHANTMENT_LIMITS);
    }
}
