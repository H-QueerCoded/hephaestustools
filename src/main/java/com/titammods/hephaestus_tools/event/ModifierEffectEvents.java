package com.titammods.hephaestus_tools.event;

import com.titammods.hephaestus_tools.HephaestusTools;
import com.titammods.hephaestus_tools.tools.helper.ToolEnchantments;
import com.titammods.hephaestus_tools.tools.item.ModifiableItem;
import com.titammods.hephaestus_tools.tools.modifier.ModifierEffects;
import com.titammods.hephaestus_tools.tools.nbt.ToolConstructionData;
import com.titammods.hephaestus_tools.tools.nbt.ToolStack;
import com.titammods.hephaestus_tools.tools.helper.ToolCombat;
import com.titammods.hephaestus_tools.tools.stat.ToolStats;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;

import java.util.List;
import java.util.function.IntSupplier;

@EventBusSubscriber(modid = HephaestusTools.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class ModifierEffectEvents {

    private ModifierEffectEvents() {}

    private static int modLevel(ItemStack tool, ResourceLocation id) {
        if (!ToolStack.isUsable(tool)) return 0;
        for (ToolConstructionData.ModifierEntry e : ToolStack.getModifiers(tool)) if (e.id().equals(id)) return e.level();
        return 0;
    }

    @SubscribeEvent
    public static void onHit(LivingDamageEvent.Post e) {
        if (!(e.getSource().getEntity() instanceof Player p)) return;
        if (e.getNewDamage() <= 0) return;
        ItemStack tool = ToolCombat.tool(e.getEntity(), e.getSource());
        LivingEntity target = e.getEntity();

        int flame = modLevel(tool, ModifierEffects.FLAME);
        if (flame > 0) target.igniteForSeconds(3 * flame);

        int sweep = modLevel(tool, ModifierEffects.SWEEPING);
        if (sweep > 0 && p.level() instanceof ServerLevel serverLevel) {
            ToolCombat.secondary(() -> {
                double r = 1.5 + sweep * 0.5;
                float frac = 0.25f * sweep;
                DamageSource src = p.damageSources().playerAttack(p);
                for (LivingEntity m : serverLevel.getEntitiesOfClass(LivingEntity.class,
                        new AABB(target.blockPosition()).inflate(r)))
                    if (m != target && m != p && m.isAlive()) m.hurt(src, e.getOriginalDamage() * frac);
            });
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEnchantments(GetEnchantmentLevelEvent event) {
        ItemStack tool = event.getStack();
        if (!(tool.getItem() instanceof ModifiableItem)) return;
        ItemEnchantments.Mutable enchantments = event.getEnchantments();
        for (Holder<Enchantment> enchantment : List.copyOf(enchantments.keySet())) {
            if (!event.isTargetting(enchantment)) continue;
            int level = enchantments.getLevel(enchantment);
            int limited = ToolEnchantments.limit(enchantment, level);
            if (limited < level) enchantments.set(enchantment, limited);
        }
        grant(event, ToolEnchantments.SILK_TOUCH, () -> modLevel(tool, ModifierEffects.SILK_TOUCH));
        grant(event, ToolEnchantments.FORTUNE, () -> stat(tool, ToolStats.FORTUNE));
        grant(event, ToolEnchantments.LOOTING, () -> stat(tool, ToolStats.LOOTING));
    }

    private static int stat(ItemStack tool, String name) {
        if (!ToolStack.isUsable(tool)) return 0;
        return Math.max(0, (int) ToolStack.getProperties(tool).getStat(name, 0));
    }

    @SubscribeEvent
    public static void onAnvilTake(AnvilRepairEvent event) {
        ItemStack output = event.getOutput();
        if (output.getItem() instanceof ModifiableItem) ToolEnchantments.stripBanned(output);
    }

    private static void grant(GetEnchantmentLevelEvent event, ResourceKey<Enchantment> key, IntSupplier level) {
        if (!event.isTargetting(key)) return;
        int value = level.getAsInt();
        event.getHolder(key).ifPresent(holder -> event.getEnchantments().set(holder, value));
    }
}