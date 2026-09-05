package de.project.ae2virtualgarden.cell;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IVirtualGardenCell extends StorageCell {
    ItemStack getItemStack();
    @Nullable
    ISaveProvider getSaveProvider();
    GardenCellTier getTier();
    @Nullable
    Item getConfiguredSeedOrSapling();
    boolean isFull();
    long injectGeneratedDrop(AEKey key, long amount, Actionable mode);
}
