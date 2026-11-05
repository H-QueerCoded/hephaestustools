package com.titammods.hephaestus_tools.compat.jei;

import com.titammods.compat.jei.TitamModsJEIPlugin;
import com.titammods.hephaestus_tools.HephaestusTools;
import com.titammods.hephaestus_tools.materials.MaterialId;
import com.titammods.hephaestus_tools.registry.ModComponents;
import com.titammods.hephaestus_tools.registry.ModItems;
import com.titammods.hephaestus_tools.tools.helper.ToolEnchantments;
import com.titammods.hephaestus_tools.tools.item.ModifiableItem;
import com.titammods.hephaestus_tools.tools.part.ToolPartItem;
import com.titammods.setup.ModRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@JeiPlugin
public class HephaestusToolsJeiPlugin implements IModPlugin {

    private static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(HephaestusTools.MOD_ID, "jei_plugin");

    private static final String CAST_SUFFIX = "_cast";

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    private static Set<Item> partItems() {
        return Set.of(
                ModItems.PICK_HEAD.get(), ModItems.HAMMER_HEAD.get(), ModItems.SMALL_AXE_HEAD.get(),
                ModItems.BROAD_AXE_HEAD.get(), ModItems.ADZE_HEAD.get(), ModItems.LARGE_PLATE.get(),
                ModItems.SMALL_BLADE.get(), ModItems.LARGE_BLADE.get(), ModItems.TOOL_HANDLE.get(),
                ModItems.TOUGH_HANDLE.get(), ModItems.TOOL_BINDING.get(), ModItems.TOUGH_BINDING.get());
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration reg) {
        IIngredientSubtypeInterpreter<ItemStack> interp = (stack, ctx) -> {
            MaterialId id = stack.get(ModComponents.PART_MATERIAL.get());
            return id == null ? "none" : id.toString();
        };
        for (Item part : partItems()) reg.registerSubtypeInterpreter(part, interp);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HephaestusToolsJeiPlugin.class);

    @Override
    public void onRuntimeAvailable(IJeiRuntime jei) {
        hideBannedAnvilRecipes(jei.getRecipeManager());
        if (!ModList.get().isLoaded(HephaestusTools.FORGE_MOD_ID)) return;
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            LOGGER.warn("[HephaestusTools/JEI] Receitas de fundicao nao recebidas do servidor, variantes de peca nao registradas");
            return;
        }
        RecipeManager rm = level.getRecipeManager();
        IRecipeManager jeiRm = jei.getRecipeManager();

        List<ItemStack> variants = new ArrayList<>();
        List<ModRecipes.CastingTableRecipe> castGeneric = new ArrayList<>();
        List<ModRecipes.CastingTableRecipe> castWood = new ArrayList<>();

        List<RecipeHolder<ModRecipes.CastingTableRecipe>> casting =
                rm.getAllRecipesFor(ModRecipes.CASTING_TABLE_TYPE.get());

        for (RecipeHolder<ModRecipes.CastingTableRecipe> holder : casting) {
            ModRecipes.CastingTableRecipe r = holder.value();
            ItemStack result = r.result();

            if (result.getItem() instanceof ToolPartItem && result.has(ModComponents.PART_MATERIAL.get())) {
                variants.add(result);
                continue;
            }

            ResourceLocation rid = BuiltInRegistries.ITEM.getKey(result.getItem());
            if (rid == null || !rid.getPath().endsWith(CAST_SUFFIX)) continue;

            Item moldPart = firstItemOf(r.cast());
            if (!(moldPart instanceof ToolPartItem)) continue;

            ItemStack woodPart = new ItemStack(moldPart);
            woodPart.set(ModComponents.PART_MATERIAL.get(), MaterialId.of(HephaestusTools.MOD_ID, "wood"));
            Ingredient woodIng = DataComponentIngredient.of(false, woodPart);

            castGeneric.add(r);
            castWood.add(new ModRecipes.CastingTableRecipe(woodIng, r.castConsumed(), r.fluid(), result, r.coolingTime()));
        }

        int castingTotal = casting.size();
        LOGGER.info("[HephaestusTools/JEI] casting={} variantes={} moldes={}",
                castingTotal, variants.size(), castGeneric.size());

        if (!variants.isEmpty()) {
            try {
                jei.getIngredientManager().addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, variants);
            } catch (Exception e) {
                LOGGER.error("[HephaestusTools/JEI] Falha ao registrar variantes de peca", e);
            }
        }
        if (!castGeneric.isEmpty()) {
            jeiRm.hideRecipes(TitamModsJEIPlugin.CASTING_TABLE_TYPE, castGeneric);
            jeiRm.addRecipes(TitamModsJEIPlugin.CASTING_TABLE_TYPE, castWood);
        }
    }

    private static void hideBannedAnvilRecipes(IRecipeManager jeiRm) {
        try {
            List<IJeiAnvilRecipe> banned = jeiRm.createRecipeLookup(RecipeTypes.ANVIL).get()
                    .filter(HephaestusToolsJeiPlugin::isBannedAnvilRecipe)
                    .toList();
            if (!banned.isEmpty()) jeiRm.hideRecipes(RecipeTypes.ANVIL, banned);
        } catch (RuntimeException e) {
            LOGGER.error("[HephaestusTools/JEI] Falha ao ocultar encantamentos proibidos da bigorna", e);
        }
    }

    private static boolean isBannedAnvilRecipe(IJeiAnvilRecipe recipe) {
        boolean tool = recipe.getLeftInputs().stream().anyMatch(stack -> stack.getItem() instanceof ModifiableItem);
        return tool && recipe.getRightInputs().stream().anyMatch(HephaestusToolsJeiPlugin::hasBannedEnchantment);
    }

    private static boolean hasBannedEnchantment(ItemStack stack) {
        ItemEnchantments stored = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (Holder<Enchantment> enchantment : stored.keySet()) {
            if (ToolEnchantments.isBanned(enchantment)) return true;
        }
        return false;
    }

    private static Item firstItemOf(Ingredient ingredient) {
        if (ingredient == null || ingredient.isEmpty()) return net.minecraft.world.item.Items.AIR;
        ItemStack[] items = ingredient.getItems();
        return items.length == 0 ? net.minecraft.world.item.Items.AIR : items[0].getItem();
    }
}