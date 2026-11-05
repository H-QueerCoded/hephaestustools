package com.titammods.hephaestus_tools.tools.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public abstract class ModifiableSwordItem extends ModifiableItem {

    public ModifiableSwordItem(Properties properties) {
        super(properties);
    }

    @Override
    public Set<ToolCategory> categories() {
        return Set.of(ToolCategory.SWORD);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }
}