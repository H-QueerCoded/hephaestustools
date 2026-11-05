package com.titammods.hephaestus_tools.datagen;

import com.titammods.hephaestus_tools.HephaestusTools;
import com.titammods.hephaestus_tools.materials.MaterialId;
import com.titammods.hephaestus_tools.registry.ModComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.neoforged.neoforge.common.Tags;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {

    private static final String NS = HephaestusTools.MOD_ID;
    private static final String PATTERN = "pattern";
    private static final String WOOD = "wood";

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    private record Part(String name, int cost) {}

    private static final List<Part> PARTS = List.of(
            new Part("pick_head", 2),
            new Part("hammer_head", 8),
            new Part("small_axe_head", 2),
            new Part("broad_axe_head", 8),
            new Part("adze_head", 2),
            new Part("large_plate", 4),
            new Part("small_blade", 2),
            new Part("large_blade", 8),
            new Part("tool_handle", 1),
            new Part("tough_handle", 3),
            new Part("tool_binding", 1),
            new Part("tough_binding", 3)
    );

    @Override
    protected void buildRecipes(RecipeOutput output) {
        for (Part part : PARTS) {
            output.accept(id("wood_" + part.name()), woodStonecutting(part), null);
        }

        output.accept(id("arsenal_table"), arsenalTable(), null);
        output.accept(id(PATTERN), pattern(), null);
        output.accept(id("forge_key"), forgeKey(), null);
    }

    private StonecutterRecipe woodStonecutting(Part part) {
        ItemStack result = new ItemStack(item(part.name()), 1);
        result.set(ModComponents.PART_MATERIAL.get(), MaterialId.of(NS, WOOD));
        return new StonecutterRecipe("", Ingredient.of(item(PATTERN)), result);
    }

    private ShapedRecipe arsenalTable() {
        Map<Character, Ingredient> key = new LinkedHashMap<>();
        key.put('m', Ingredient.of(Items.ANVIL));
        key.put('a', Ingredient.of(Items.SMITHING_TABLE));
        key.put('s', Ingredient.of(ItemTags.PLANKS));
        return shaped(List.of("m  ", "sas", "sss"), key, item("arsenal_table"), 1);
    }

    private ShapedRecipe pattern() {
        Map<Character, Ingredient> key = new LinkedHashMap<>();
        key.put('m', Ingredient.of(Tags.Items.RODS_WOODEN));
        key.put('s', Ingredient.of(ItemTags.PLANKS));
        return shaped(List.of("ms", "sm"), key, item(PATTERN), 1);
    }

    private ShapelessRecipe forgeKey() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(Ingredient.of(Tags.Items.INGOTS_GOLD));
        ingredients.add(Ingredient.of(Tags.Items.NUGGETS_GOLD));
        ingredients.add(Ingredient.of(Items.TRIPWIRE_HOOK));
        return new ShapelessRecipe("", CraftingBookCategory.MISC, new ItemStack(item("forge_key"), 1), ingredients);
    }

    private static ShapedRecipe shaped(List<String> rows, Map<Character, Ingredient> key, Item result, int count) {
        return new ShapedRecipe("", CraftingBookCategory.MISC,
                ShapedRecipePattern.of(key, rows), new ItemStack(result, count));
    }

    private static Item item(String path) {
        return BuiltInRegistries.ITEM.get(id(path));
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(NS, path);
    }
}
