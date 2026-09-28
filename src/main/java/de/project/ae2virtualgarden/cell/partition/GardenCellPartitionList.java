package de.project.ae2virtualgarden.cell.partition;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record GardenCellPartitionList(List<GardenCellPartition> partitions) {

    public static final GardenCellPartitionList EMPTY = new GardenCellPartitionList(List.of());

    public static final Codec<GardenCellPartitionList> CODEC =
            GardenCellPartition.CODEC.listOf().xmap(GardenCellPartitionList::new, GardenCellPartitionList::partitions);

    public static final StreamCodec<RegistryFriendlyByteBuf, GardenCellPartitionList> STREAM_CODEC =
            GardenCellPartition.STREAM_CODEC.apply(ByteBufCodecs.list()).map(GardenCellPartitionList::new, GardenCellPartitionList::partitions);

    public boolean isEmpty() {
        return partitions == null || partitions.isEmpty();
    }

    public int size() {
        return partitions == null ? 0 : partitions.size();
    }

    public int getTotalPercent() {
        if (partitions == null) return 0;
        int total = 0;
        for (GardenCellPartition p : partitions) {
            total += p.percent();
        }
        return total;
    }

    public int getUnallocatedPercent() {
        return Math.max(0, 100 - getTotalPercent());
    }

    @Nullable
    public GardenCellPartition getPartition(Item item) {
        if (partitions == null) return null;
        for (GardenCellPartition p : partitions) {
            if (p.target() == item) {
                return p;
            }
        }
        return null;
    }

    public boolean contains(Item item) {
        return getPartition(item) != null;
    }

    public long getAllocatedByteLimit(GardenCellPartition partition, long totalBytes) {
        if (partition == null || partition.percent() <= 0) {
            return 0;
        }
        return (totalBytes * partition.percent()) / 100L;
    }

    public GardenCellPartitionList withUpdated(List<GardenCellPartition> newPartitions) {
        return new GardenCellPartitionList(new ArrayList<>(newPartitions));
    }
}
