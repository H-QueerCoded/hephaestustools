package com.titammods.hephaestus_tools.registry;

import com.titammods.hephaestus_tools.HephaestusTools;
import com.titammods.hephaestus_tools.tables.block.ArsenalTableBlock;
import com.titammods.hephaestus_tools.tables.blockentity.ArsenalTableBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(HephaestusTools.MOD_ID);

    public static final DeferredRegister<net.minecraft.world.level.block.entity.BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE, HephaestusTools.MOD_ID);

    private static BlockBehaviour.Properties anvilProps() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_GRAY)
                .sound(SoundType.ANVIL)
                .pushReaction(PushReaction.BLOCK)
                .requiresCorrectToolForDrops()
                .strength(5.0f, 1200.0f)
                .noOcclusion();
    }

    private static BlockBehaviour.Properties woodTableProps() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .sound(SoundType.WOOD)
                .strength(1.0f, 5.0f)
                .noOcclusion();
    }

    public static final DeferredBlock<ArsenalTableBlock> ARSENAL_TABLE =
            BLOCKS.register("arsenal_table", () -> new ArsenalTableBlock(anvilProps()));

    static {
        registerBlockItem(ARSENAL_TABLE);
    }

    private static <T extends Block> void registerBlockItem(DeferredBlock<T> block) {
        ModItems.ITEMS.register(block.getId().getPath(),
                () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static final Supplier<net.minecraft.world.level.block.entity.BlockEntityType<ArsenalTableBlockEntity>> ARSENAL_TABLE_BE =
            BLOCK_ENTITIES.register("arsenal_table",
                    () -> net.minecraft.world.level.block.entity.BlockEntityType.Builder
                            .of(ArsenalTableBlockEntity::new, ARSENAL_TABLE.get())
                            .build(null));
}
