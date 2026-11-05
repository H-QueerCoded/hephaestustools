package com.titammods.hephaestus_tools.tables.blockentity;

import com.titammods.hephaestus_tools.HephaestusTools;
import com.titammods.hephaestus_tools.tools.part.ToolPartItem;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemContainerContents;
import com.titammods.hephaestus_tools.registry.ModBlocks;
import com.titammods.hephaestus_tools.table.TableStyle;
import com.titammods.hephaestus_tools.tables.menu.ArsenalTableMenu;
import com.titammods.hephaestus_tools.tables.menu.WorkbenchMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class ArsenalTableBlockEntity extends BlockEntity implements MenuProvider {
    public static final int BULK_LIMIT = 4000;
    private static final int SAVED_STACK_LIMIT = 99;

    private final ItemStackHandler upgradeSlot = new ItemStackHandler(1) {
        @Override protected void onContentsChanged(int slot) { setChanged(); syncToClient(); }
        @Override public int getSlotLimit(int slot) { return 1; }
    };
    private final ItemStackHandler blueprintSlot = new ItemStackHandler(1) {
        @Override protected void onContentsChanged(int slot) { setChanged(); syncToClient(); }
        @Override public int getSlotLimit(int slot) { return 1; }
    };
    private final ItemStackHandler inputSlots = new ItemStackHandler(4) {
        @Override protected void onContentsChanged(int slot) { setChanged(); syncToClient(); }

        @Override public int getSlotLimit(int slot) { return slot == 0 ? BULK_LIMIT : slot < 3 ? 99 : 1; }
    };
    private final ItemStackHandler outputSlot = new ItemStackHandler(1) {
        @Override protected void onContentsChanged(int slot) { setChanged(); syncToClient(); }
    };

    public static final int STORAGE_SIZE = 54;
    public static final TagKey<Item> STORABLE = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(HephaestusTools.MOD_ID, "forge_storage"));

    private final NonNullList<ItemStack> storageItems = NonNullList.withSize(STORAGE_SIZE, ItemStack.EMPTY);
    private final StorageContainer storage = new StorageContainer();

    public static boolean isStorable(ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() instanceof ToolPartItem || stack.is(STORABLE));
    }

    public ArsenalTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.ARSENAL_TABLE_BE.get(), pos, state);
    }

    public ItemStackHandler getUpgradeSlot()   { return upgradeSlot; }
    public ItemStackHandler getBlueprintSlot() { return blueprintSlot; }
    public ItemStack getUpgradeItem()          { return upgradeSlot.getStackInSlot(0); }
    public ItemStackHandler getInputSlots()    { return inputSlots; }
    public ItemStackHandler getOutputSlot()    { return outputSlot; }
    public Container getStorage()              { return storage; }

    public int storedCount() {
        int count = 0;
        for (ItemStack stack : storageItems) if (!stack.isEmpty()) count++;
        return count;
    }

    public boolean hasStoredItems() {
        return storedCount() > 0;
    }

    private boolean noDrops;

    public void markNoDrops() {
        noDrops = true;
    }

    public boolean noDrops() {
        return noDrops;
    }

    public void compactStorage() {
        int write = 0;
        boolean moved = false;
        for (int read = 0; read < STORAGE_SIZE; read++) {
            ItemStack stack = storageItems.get(read);
            if (stack.isEmpty()) continue;
            if (read != write) {
                storageItems.set(write, stack);
                storageItems.set(read, ItemStack.EMPTY);
                moved = true;
            }
            write++;
        }
        if (moved) setChanged();
    }

    public void clearStorage() {
        for (int i = 0; i < STORAGE_SIZE; i++) storageItems.set(i, ItemStack.EMPTY);
        setChanged();
    }

    private void syncToClient() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public void dropContents(Level level, BlockPos pos) {
        List<ItemStack> drops = new ArrayList<>(7);
        drops.add(upgradeSlot.getStackInSlot(0));
        drops.add(blueprintSlot.getStackInSlot(0));
        for (int i = 0; i < 4; i++) drops.add(inputSlots.getStackInSlot(i));
        drops.add(outputSlot.getStackInSlot(0));
        for (ItemStack stack : drops) {
            if (!stack.isEmpty()) Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack.copy());
        }
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput components) {
        super.applyImplicitComponents(components);
        for (int i = 0; i < STORAGE_SIZE; i++) storageItems.set(i, ItemStack.EMPTY);
        components.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(storageItems);
        for (int i = 0; i < STORAGE_SIZE; i++) if (!isStorable(storageItems.get(i))) storageItems.set(i, ItemStack.EMPTY);
        compactStorage();
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        if (hasStoredItems()) components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(storageItems));
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove("Storage");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.hephaestus_tools.arsenal_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
        if (TableStyle.isModern(player)) return new WorkbenchMenu(containerId, inv, this);
        return new ArsenalTableMenu(containerId, inv, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Upgrade", upgradeSlot.serializeNBT(registries));
        tag.put("Blueprint", blueprintSlot.serializeNBT(registries));
        saveInputs(tag, registries);
        tag.put("Output", outputSlot.serializeNBT(registries));
        if (hasStoredItems()) {
            CompoundTag stored = new CompoundTag();
            ContainerHelper.saveAllItems(stored, storageItems, registries);
            tag.put("Storage", stored);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Upgrade")) upgradeSlot.deserializeNBT(registries, tag.getCompound("Upgrade"));
        if (tag.contains("Blueprint")) blueprintSlot.deserializeNBT(registries, tag.getCompound("Blueprint"));
        loadInputs(tag, registries);
        if (tag.contains("Output")) outputSlot.deserializeNBT(registries, tag.getCompound("Output"));
        for (int i = 0; i < STORAGE_SIZE; i++) storageItems.set(i, ItemStack.EMPTY);
        if (tag.contains("Storage")) ContainerHelper.loadAllItems(tag.getCompound("Storage"), storageItems, registries);
    }

    private void saveInputs(CompoundTag tag, HolderLookup.Provider registries) {
        ItemStackHandler capped = new ItemStackHandler(inputSlots.getSlots());
        int[] counts = new int[inputSlots.getSlots()];
        boolean bulk = false;
        for (int i = 0; i < inputSlots.getSlots(); i++) {
            ItemStack stack = inputSlots.getStackInSlot(i);
            counts[i] = stack.getCount();
            if (stack.getCount() > SAVED_STACK_LIMIT) {
                capped.setStackInSlot(i, stack.copyWithCount(SAVED_STACK_LIMIT));
                bulk = true;
            } else {
                capped.setStackInSlot(i, stack.copy());
            }
        }
        tag.put("Inputs", capped.serializeNBT(registries));
        if (bulk) tag.putIntArray("InputCounts", counts);
    }

    private void loadInputs(CompoundTag tag, HolderLookup.Provider registries) {
        if (!tag.contains("Inputs")) return;
        inputSlots.deserializeNBT(registries, tag.getCompound("Inputs"));
        int[] counts = tag.getIntArray("InputCounts");
        for (int i = 0; i < Math.min(counts.length, inputSlots.getSlots()); i++) {
            ItemStack stack = inputSlots.getStackInSlot(i);
            if (!stack.isEmpty() && counts[i] > stack.getCount())
                stack.setCount(Math.min(counts[i], inputSlots.getSlotLimit(i)));
        }
    }

    private final class StorageContainer implements Container {

        @Override
        public int getContainerSize() {
            return STORAGE_SIZE;
        }

        @Override
        public boolean isEmpty() {
            return !hasStoredItems();
        }

        @Override
        public ItemStack getItem(int slot) {
            return slot >= 0 && slot < STORAGE_SIZE ? storageItems.get(slot) : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            ItemStack out = ContainerHelper.removeItem(storageItems, slot, amount);
            if (!out.isEmpty()) setChanged();
            return out;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return ContainerHelper.takeItem(storageItems, slot);
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            if (slot < 0 || slot >= STORAGE_SIZE) return;
            storageItems.set(slot, stack);
            stack.limitSize(getMaxStackSize(stack));
            setChanged();
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return isStorable(stack);
        }

        @Override
        public void setChanged() {
            ArsenalTableBlockEntity.this.setChanged();
        }

        @Override
        public boolean stillValid(Player player) {
            return !isRemoved() && Container.stillValidBlockEntity(ArsenalTableBlockEntity.this, player);
        }

        @Override
        public void clearContent() {
            clearStorage();
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.put("Upgrade", upgradeSlot.serializeNBT(registries));
        tag.put("Blueprint", blueprintSlot.serializeNBT(registries));
        saveInputs(tag, registries);
        tag.put("Output", outputSlot.serializeNBT(registries));
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
