package de.project.ae2virtualgarden.cell.partition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;

public record GardenCellPartition(
        Item target,
        int percent,
        boolean voidSecondary
) {
    public static final Codec<GardenCellPartition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("target").forGetter(GardenCellPartition::target),
            Codec.INT.fieldOf("percent").forGetter(GardenCellPartition::percent),
            Codec.BOOL.optionalFieldOf("void_secondary", false).forGetter(GardenCellPartition::voidSecondary)
    ).apply(instance, GardenCellPartition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GardenCellPartition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(net.minecraft.core.registries.Registries.ITEM), GardenCellPartition::target,
            ByteBufCodecs.VAR_INT, GardenCellPartition::percent,
            ByteBufCodecs.BOOL, GardenCellPartition::voidSecondary,
            GardenCellPartition::new
    );
}
