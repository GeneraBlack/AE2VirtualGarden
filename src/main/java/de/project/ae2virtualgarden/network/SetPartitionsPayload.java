package de.project.ae2virtualgarden.network;

import de.project.ae2virtualgarden.AE2VirtualGarden;
import de.project.ae2virtualgarden.cell.partition.GardenCellPartitionList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SetPartitionsPayload(GardenCellPartitionList partitions) implements CustomPacketPayload {
    public static final Type<SetPartitionsPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AE2VirtualGarden.MODID, "set_partitions"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetPartitionsPayload> STREAM_CODEC =
            StreamCodec.composite(
                    GardenCellPartitionList.STREAM_CODEC,
                    SetPartitionsPayload::partitions,
                    SetPartitionsPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
