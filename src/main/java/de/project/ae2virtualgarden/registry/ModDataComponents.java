package de.project.ae2virtualgarden.registry;

import de.project.ae2virtualgarden.AE2VirtualGarden;
import de.project.ae2virtualgarden.cell.partition.GardenCellPartitionList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, AE2VirtualGarden.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GardenCellPartitionList>> PARTITIONS =
            DATA_COMPONENTS.register("partitions", () -> DataComponentType.<GardenCellPartitionList>builder()
                    .persistent(GardenCellPartitionList.CODEC)
                    .networkSynchronized(GardenCellPartitionList.STREAM_CODEC)
                    .build());
}
