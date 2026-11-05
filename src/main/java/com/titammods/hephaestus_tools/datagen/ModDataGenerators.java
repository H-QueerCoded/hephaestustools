package com.titammods.hephaestus_tools.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class ModDataGenerators {

    private ModDataGenerators() {}

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ModDataGenerators::onGatherData);
    }

    private static void onGatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        gen.addProvider(event.includeServer(), new ModBlockDataProvider(output));
        gen.addProvider(event.includeServer(), new ModRecipeProvider(output, event.getLookupProvider()));
        gen.addProvider(event.includeServer(), new PartCastingMeltingProvider(output));
        gen.addProvider(event.includeServer(), new TraitProvider(output));
        gen.addProvider(event.includeServer(), new TierProvider(output));
    }
}
