package com.titammods.hephaestus_tools.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PartCastingMeltingProvider implements DataProvider {

    private static final String TOOLS = "hephaestus_tools";
    private static final String FORGE = "hephaestus";

    private final PackOutput.PathProvider recipePath;

    public PartCastingMeltingProvider(PackOutput output) {
        this.recipePath = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
    }

    private record Part(String name, int cost) {}

    private record Metal(String name, int temp, String fluid, String bucket, boolean compat, List<JsonObject> conditions) {}

    private static final List<Part> PARTS = List.of(
            new Part("pick_head", 180),
            new Part("hammer_head", 720),
            new Part("small_axe_head", 180),
            new Part("broad_axe_head", 720),
            new Part("adze_head", 180),
            new Part("large_plate", 360),
            new Part("small_blade", 180),
            new Part("large_blade", 720),
            new Part("tool_handle", 90),
            new Part("tough_handle", 270),
            new Part("tool_binding", 90),
            new Part("tough_binding", 270)
    );

    private static List<Metal> metals() {
        List<Metal> out = new ArrayList<>();
        vanilla(out);
        foundry(out);
        commonMetals(out);
        sharedMaterials(out);
        ftbArmory(out);
        allTheModium(out);
        ftbMaterials(out);
        occultism(out);
        ltxi(out);
        enderIo(out);
        enderIoEvolution(out);
        energizedPower(out);
        stellaris(out);
        justDireThings(out);
        replication(out);
        thaumaturge(out);
        neoVitae(out);
        nautec(out);
        iceAndFire(out);
        oritech(out);
        ironsJewelry(out);
        ae2(out);
        extendedAe(out);
        xyCraft(out);
        evilCraft(out);
        nodeWorks(out);
        mysticalAgradditions(out);
        colossalReactors(out);
        return out;
    }

    private static void vanilla(List<Metal> out) {
        out.add(new Metal("iron", 900, molten("iron"), null, false, List.of()));
        out.add(new Metal("copper", 900, molten("copper"), null, false, List.of()));
        out.add(new Metal("gold", 1060, molten("gold"), null, false, List.of()));
        out.add(new Metal("netherite", 2000, molten("netherite"), null, false, List.of()));
    }

    private static void foundry(List<Metal> out) {
        out.add(forgeMetal("cobalt", 1100));
        out.add(forgeMetal("steel", 1370, ingots("steel")));
    }

    private static void commonMetals(List<Metal> out) {
        out.add(forgeMetal("aluminum", 660, ingots("aluminum")));
        out.add(forgeMetal("brass", 930, ingots("brass")));
        out.add(forgeMetal("bronze", 950, ingots("bronze")));
        out.add(forgeMetal("constantan", 1220, ingots("constantan")));
        out.add(forgeMetal("electrum", 1000, ingots("electrum")));
        out.add(forgeMetal("enderium", 1450, ingots("enderium")));
        out.add(forgeMetal("invar", 1420, ingots("invar")));
        out.add(forgeMetal("lead", 327, ingots("lead")));
        out.add(forgeMetal("lumium", 1000, ingots("lumium")));
        out.add(forgeMetal("nickel", 1450, ingots("nickel")));
        out.add(forgeMetal("osmium", 3000, ingots("osmium")));
        out.add(forgeMetal("platinum", 1768, ingots("platinum")));
        out.add(forgeMetal("signalum", 1000, ingots("signalum")));
        out.add(forgeMetal("silver", 960, ingots("silver")));
        out.add(forgeMetal("tin", 230, ingots("tin")));
        out.add(forgeMetal("uranium", 1130, ingots("uranium")));
        out.add(forgeMetal("zinc", 419, ingots("zinc")));
        out.add(forgeMetal("titanium", 1000, ingots("titanium")));
    }

    private static void sharedMaterials(List<Metal> out) {
        out.add(forgeMetal("iridium", 2440, anyMod("ftbmaterials", "ftbic")));
        out.add(forgeMetal("chromium", 1000, anyMod("ftbmaterials", "colossal_reactors")));
        out.add(forgeMetal("redstone_alloy", 800, anyMod("enderio", "energizedpower")));
        out.add(forgeMetal("advanced_alloy", 1000, anyMod("energizedpower", "ftbic")));
        out.add(forgeMetal("ruby", 1000, anyMod("ftbmaterials", "irons_jewelry")));
        out.add(forgeMetal("sapphire", 1000, anyMod("ftbmaterials", "iceandfire", "irons_jewelry")));
        out.add(forgeMetal("topaz", 1000, gems("topaz")));
        out.add(forgeMetal("moonstone", 800, anyMod("irons_jewelry", "arsmagicalegacy")));
    }

    private static void ftbArmory(List<Metal> out) {
        out.add(forgeMetal("adamantite", 2000, modLoaded("ftbarmory")));
        out.add(forgeMetal("aeternium", 2000, modLoaded("ftbarmory")));
        out.add(forgeMetal("aurichalcum", 2000, modLoaded("ftbarmory")));
    }

    private static void allTheModium(List<Metal> out) {
        out.add(forgeMetal("allthemodium", 2000, modLoaded("allthemodium")));
        out.add(forgeMetal("vibranium", 2000, modLoaded("allthemodium")));
        out.add(forgeMetal("unobtainium", 2000, modLoaded("allthemodium")));
    }

    private static void ftbMaterials(List<Metal> out) {
        out.add(forgeMetal("dimensional_shard", 1000, modLoaded("ftbmaterials")));
    }

    private static void occultism(List<Metal> out) {
        out.add(forgeMetal("iesnium", 1200, modLoaded("occultism"), ingots("iesnium")));
        out.add(forgeMetal("spirit_attuned", 1000, modLoaded("occultism")));
    }

    private static void ltxi(List<Metal> out) {
        out.add(forgeMetal("niobium", 1000, modLoaded("ltxi")));
        out.add(forgeMetal("slatesteel", 1000, modLoaded("ltxi")));
        out.add(forgeMetal("tungsten_slatesteel", 1600, modLoaded("ltxi"), ingots("tungsten_slatesteel")));
        out.add(forgeMetal("olivine", 800, modLoaded("ltxi")));
    }

    private static void enderIo(List<Metal> out) {
        out.add(forgeMetal("conductive_alloy", 800, modLoaded("enderio")));
        out.add(forgeMetal("energetic_alloy", 800, modLoaded("enderio"), ingots("energetic_alloy")));
        out.add(forgeMetal("pulsating_alloy", 800, modLoaded("enderio")));
        out.add(forgeMetal("soularium", 800, modLoaded("enderio")));
        out.add(forgeMetal("vibrant_alloy", 1000, modLoaded("enderio"), ingots("vibrant_alloy")));
        out.add(forgeMetal("dark_steel", 1000, modLoaded("enderio")));
        out.add(forgeMetal("end_steel", 1600, modLoaded("enderio"), ingots("end_steel")));
    }

    private static void enderIoEvolution(List<Metal> out) {
        out.add(forgeMetal("energetic_silver", 800, modLoaded("enderio_evolution"), ingots("energetic_silver")));
        out.add(forgeMetal("vivid_alloy", 1000, modLoaded("enderio_evolution"), ingots("vivid_alloy")));
    }

    private static void energizedPower(List<Metal> out) {
        out.add(forgeMetal("energized_copper", 800, modLoaded("energizedpower"), ingots("energized_copper")));
        out.add(forgeMetal("energized_gold", 800, modLoaded("energizedpower"), ingots("energized_gold")));
        out.add(forgeMetal("energized_alloy", 1000, modLoaded("energizedpower"), ingots("energized_alloy")));
        out.add(forgeMetal("crystallized_alloy", 1000, modLoaded("energizedpower")));
    }

    private static void stellaris(List<Metal> out) {
        out.add(forgeMetal("desh", 1000, modLoaded("stellaris")));
    }

    private static void justDireThings(List<Metal> out) {
        out.add(forgeMetal("ferricore", 800, modLoaded("justdirethings"), ingots("ferricore")));
        out.add(forgeMetal("blazegold", 1000, modLoaded("justdirethings")));
        out.add(forgeMetal("eclipse_alloy", 1600, modLoaded("justdirethings")));
    }

    private static void replication(List<Metal> out) {
        out.add(forgeMetal("replica", 1000, modLoaded("replication")));
    }

    private static void thaumaturge(List<Metal> out) {
        out.add(forgeMetal("thaumium", 1000, modLoaded("thaumaturge"), ingots("thaumium")));
        out.add(forgeMetal("void_metal", 1600, modLoaded("thaumaturge"), ingots("void_metal")));
    }

    private static void neoVitae(List<Metal> out) {
        out.add(forgeMetal("hellforged", 1600, modLoaded("neovitae"), ingots("hellforged")));
    }

    private static void nautec(List<Metal> out) {
        out.add(forgeMetal("atlantic_gold", 1200, modLoaded("nautec")));
        out.add(forgeMetal("aquarine_steel", 1200, modLoaded("nautec")));
    }

    private static void iceAndFire(List<Metal> out) {
        out.add(forgeMetal("dragonsteel_fire", 1600, modLoaded("iceandfire")));
        out.add(forgeMetal("dragonsteel_ice", 1600, modLoaded("iceandfire")));
        out.add(forgeMetal("dragonsteel_lightning", 1600, modLoaded("iceandfire"), itemExists("iceandfire:dragonsteel_lightning_ingot")));
    }

    private static void oritech(List<Metal> out) {
        out.add(forgeMetal("adamant", 1000, modLoaded("oritech")));
        out.add(forgeMetal("duratium", 1600, modLoaded("oritech")));
        out.add(forgeMetal("prometheum", 1600, modLoaded("oritech")));
        out.add(forgeMetal("fluxite", 1000, modLoaded("oritech"), gems("fluxite")));
    }

    private static void ironsJewelry(List<Metal> out) {
        out.add(forgeMetal("peridot", 1000, modLoaded("irons_jewelry")));
        out.add(forgeMetal("onyx", 1000, modLoaded("irons_jewelry")));
        out.add(forgeMetal("garnet", 1000, modLoaded("irons_jewelry"), gems("garnet")));
    }

    private static void ae2(List<Metal> out) {
        out.add(forgeMetal("certus_quartz", 800, modLoaded("ae2")));
        out.add(forgeMetal("fluix", 1000, modLoaded("ae2"), gems("fluix")));
    }

    private static void extendedAe(List<Metal> out) {
        out.add(forgeMetal("entro", 1000, modLoaded("extendedae"), gems("entro")));
    }

    private static void xyCraft(List<Metal> out) {
        out.add(forgeMetal("xychorium", 800, modLoaded("xycraft_world"), gems("xychorium")));
    }

    private static void evilCraft(List<Metal> out) {
        out.add(forgeMetal("dark_gem", 800, modLoaded("evilcraft")));
    }

    private static void nodeWorks(List<Metal> out) {
        out.add(forgeMetal("celestine", 800, modLoaded("nodeworks")));
    }

    private static void mysticalAgradditions(List<Metal> out) {
        String mod = "mysticalagradditions";
        out.add(externalMetal(mod, "tertium", 800, true, modLoaded(mod), gems("tertium")));
        out.add(externalMetal(mod, "prudentium", 800, true, modLoaded(mod)));
        out.add(externalMetal(mod, "prosperity", 800, true, modLoaded(mod)));
        out.add(externalMetal(mod, "soulium", 800, true, modLoaded(mod), gems("soulium")));
        out.add(externalMetal(mod, "imperium", 1000, true, modLoaded(mod)));
        out.add(externalMetal(mod, "supremium", 1000, true, modLoaded(mod), gems("supremium")));
        out.add(externalMetal(mod, "awakened_supremium", 1600, true, modLoaded(mod)));
        out.add(externalMetal(mod, "insanium", 1600, true, modLoaded(mod)));
    }

    private static void colossalReactors(List<Metal> out) {
        String mod = "colossal_reactors";
        out.add(externalMetal(mod, "stainless_steel", 1000, false, modLoaded(mod), ingots("stainless_steel")));
        out.add(externalMetal(mod, "tough_alloy", 1000, false, modLoaded(mod), ingots("tough_alloy")));
    }

    private static Metal forgeMetal(String name, int temp, JsonObject... conditions) {
        return new Metal(name, temp, molten(name), molten(name) + "_bucket", true, List.of(conditions));
    }

    private static Metal externalMetal(String modid, String name, int temp, boolean checkBucket, JsonObject... conditions) {
        String fluid = modid + ":molten_" + name;
        return new Metal(name, temp, fluid, checkBucket ? fluid + "_bucket" : null, true, List.of(conditions));
    }

    private static String molten(String name) {
        return FORGE + ":molten_" + name;
    }

    private static int timeFor(int cost) {
        return 80 + (cost * 2) / 9;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        List<Metal> metals = metals();
        for (Part part : PARTS) {
            futures.add(DataProvider.saveStable(cache, castCreation(part),
                    recipePath.json(id(TOOLS, "casting/casts/" + part.name() + "_cast"))));
            for (Metal metal : metals) {
                String folder = metal.compat() ? "compat/" + metal.name() : metal.name();

                futures.add(DataProvider.saveStable(cache, casting(part, metal),
                        recipePath.json(id(TOOLS, "casting/" + folder + "/" + part.name()))));

                futures.add(DataProvider.saveStable(cache, melting(part, metal),
                        recipePath.json(id(TOOLS, "melting/" + folder + "/" + part.name()))));
            }
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private JsonObject casting(Part part, Metal metal) {
        JsonObject json = new JsonObject();
        addConditions(json, metal);
        json.addProperty("type", FORGE + ":casting_table");

        json.add("cast", item(TOOLS + ":" + part.name() + "_cast"));
        json.addProperty("cast_consumed", false);
        json.add("fluid", fluid(metal.fluid(), part.cost()));
        json.addProperty("result", TOOLS + ":" + part.name());
        json.addProperty("count", 1);
        json.addProperty("cooling_time", timeFor(part.cost()));
        return json;
    }

    private JsonObject castCreation(Part part) {
        JsonObject json = new JsonObject();
        JsonArray conditions = new JsonArray();
        conditions.add(modLoaded(FORGE));
        json.add("neoforge:conditions", conditions);
        json.addProperty("type", FORGE + ":casting_table");

        json.add("cast", item(TOOLS + ":" + part.name()));
        json.addProperty("cast_consumed", true);
        json.add("fluid", fluid(molten("copper"), 90));
        json.addProperty("result", TOOLS + ":" + part.name() + "_cast");
        json.addProperty("count", 1);
        json.addProperty("cooling_time", 100);
        return json;
    }

    private JsonObject melting(Part part, Metal metal) {
        JsonObject json = new JsonObject();
        addConditions(json, metal);
        json.addProperty("type", FORGE + ":melting");

        JsonObject ingredient = new JsonObject();
        ingredient.addProperty("type", "neoforge:components");
        JsonObject components = new JsonObject();
        components.addProperty(TOOLS + ":part_material", TOOLS + ":" + metal.name());
        ingredient.add("components", components);
        ingredient.addProperty("items", TOOLS + ":" + part.name());
        ingredient.addProperty("strict", false);
        json.add("ingredient", ingredient);

        json.add("result", fluid(metal.fluid(), part.cost()));
        json.add("fuel", fuelForTemp(metal.temp()));
        json.addProperty("temperature", metal.temp());
        json.addProperty("time", timeFor(part.cost()));
        return json;
    }

    private static JsonObject item(String id) {
        JsonObject i = new JsonObject();
        i.addProperty("item", id);
        return i;
    }

    private static JsonObject fluid(String id, int amount) {
        JsonObject f = new JsonObject();
        f.addProperty("id", id);
        f.addProperty("amount", amount);
        return f;
    }

    private static JsonObject fuelForTemp(int temp) {
        return temp <= 1000 ? fluid("minecraft:lava", 50) : fluid(molten("blaze"), 50);
    }

    private static void addConditions(JsonObject json, Metal metal) {
        JsonArray conditions = new JsonArray();
        conditions.add(modLoaded(FORGE));
        for (JsonObject condition : metal.conditions()) {
            conditions.add(condition);
        }
        if (metal.bucket() != null) {
            conditions.add(itemExists(metal.bucket()));
        }
        json.add("neoforge:conditions", conditions);
    }

    private static JsonObject modLoaded(String modid) {
        JsonObject c = new JsonObject();
        c.addProperty("type", "neoforge:mod_loaded");
        c.addProperty("modid", modid);
        return c;
    }

    private static JsonObject anyMod(String... modids) {
        JsonArray values = new JsonArray();
        for (String modid : modids) {
            values.add(modLoaded(modid));
        }
        JsonObject c = new JsonObject();
        c.addProperty("type", "neoforge:or");
        c.add("values", values);
        return c;
    }

    private static JsonObject itemExists(String id) {
        JsonObject c = new JsonObject();
        c.addProperty("type", "neoforge:item_exists");
        c.addProperty("item", id);
        return c;
    }

    private static JsonObject ingots(String name) {
        return tagPresent("c:ingots/" + name);
    }

    private static JsonObject gems(String name) {
        return tagPresent("c:gems/" + name);
    }

    private static JsonObject tagPresent(String tag) {
        JsonObject empty = new JsonObject();
        empty.addProperty("type", "neoforge:tag_empty");
        empty.addProperty("tag", tag);
        JsonObject c = new JsonObject();
        c.addProperty("type", "neoforge:not");
        c.add("value", empty);
        return c;
    }

    private static ResourceLocation id(String ns, String path) {
        return ResourceLocation.fromNamespaceAndPath(ns, path);
    }

    @Override
    public String getName() {
        return "Hephaestus Tools - Part Casting & Melting";
    }
}