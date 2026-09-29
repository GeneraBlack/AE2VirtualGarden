package de.project.ae2virtualgarden.cell;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import de.project.ae2virtualgarden.cell.partition.GardenCellPartition;
import de.project.ae2virtualgarden.cell.partition.GardenCellPartitionList;
import de.project.ae2virtualgarden.registry.ModDataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IVirtualGardenCell extends StorageCell {
    ItemStack getItemStack();
    @Nullable
    ISaveProvider getSaveProvider();
    GardenCellTier getTier();
    @Nullable
    Item getConfiguredSeedOrSapling();
    boolean isFull();
    long injectGeneratedDrop(AEKey key, long amount, Actionable mode);

    default GardenCellPartitionList getPartitions() {
        ItemStack stack = getItemStack();
        if (stack.has(ModDataComponents.PARTITIONS.get())) {
            GardenCellPartitionList list = stack.get(ModDataComponents.PARTITIONS.get());
            if (list != null && !list.isEmpty()) {
                return list;
            }
        }
        Item single = getConfiguredSeedOrSapling();
        if (single != null) {
            return new GardenCellPartitionList(List.of(new GardenCellPartition(single, 100, true)));
        }
        return GardenCellPartitionList.EMPTY;
    }

    default long getStoredCountForTarget(Item target) {
        return 0;
    }

    default boolean isPartitionFull(GardenCellPartition partition) {
        if (isFull()) {
            return true;
        }
        if (partition == null || partition.percent() <= 0) {
            return true;
        }
        long allocatedBytes = (getTier().getTotalBytes() * partition.percent()) / 100L;
        long storedCount = getStoredCountForTarget(partition.target());
        // BUG-06 FIX: Account for both item count bytes AND type overhead bytes
        long storedBytes = (storedCount + 7L) / 8L + (long) getTier().getBytesPerType();
        return storedBytes >= allocatedBytes;
    }
}
