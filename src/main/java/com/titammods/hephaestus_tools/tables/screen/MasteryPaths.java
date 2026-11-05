package com.titammods.hephaestus_tools.tables.screen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Map;

public final class MasteryPaths {

    private MasteryPaths() {}

    private static final Map<String, List<String>> PATHS = Map.ofEntries(
            Map.entry("pickaxe", List.of("prospector", "deep_miner", "momentum")),
            Map.entry("sledge_hammer", List.of("demolition", "aftershock", "unstoppable")),
            Map.entry("vein_hammer", List.of("vein_seeker", "chain_reaction", "motherlode")),
            Map.entry("mattock", List.of("groundworker", "cultivator", "homesteader")),
            Map.entry("excavator", List.of("earthmover", "tunnel_shaper", "clean_dig")),
            Map.entry("paxel", List.of("pathfinder", "trailblazer", "frontier")),
            Map.entry("hand_axe", List.of("lumber_rhythm", "precision_felling", "hatchet_master")),
            Map.entry("broad_axe", List.of("timberfall", "falling_giant", "war_axe")),
            Map.entry("kama", List.of("harvest_sweep", "replanter", "green_thumb")),
            Map.entry("scythe", List.of("reaper", "harvest_chain", "death_sweep")),
            Map.entry("dagger", List.of("backstab", "flurry", "assassin")),
            Map.entry("sword", List.of("duelist", "executioner", "blade_dance")),
            Map.entry("cleaver", List.of("butcher", "crushing_blow", "bloodlust"))
    );

    public static String toolId(Item tool) { return BuiltInRegistries.ITEM.getKey(tool).getPath(); }

    public static List<String> forTool(Item tool) { return PATHS.getOrDefault(toolId(tool), List.of()); }
}