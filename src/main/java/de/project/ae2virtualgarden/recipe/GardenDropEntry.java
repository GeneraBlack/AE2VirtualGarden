package de.project.ae2virtualgarden.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record GardenDropEntry(ItemStack item, int weight, int minCount, int maxCount) {
    public static final Codec<GardenDropEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.fieldOf("item").forGetter(GardenDropEntry::item),
            Codec.INT.optionalFieldOf("weight", 1).forGetter(GardenDropEntry::weight),
            Codec.INT.optionalFieldOf("min_count", 1).forGetter(GardenDropEntry::minCount),
            Codec.INT.optionalFieldOf("max_count", 1).forGetter(GardenDropEntry::maxCount)
    ).apply(instance, GardenDropEntry::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GardenDropEntry> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, GardenDropEntry::item,
            ByteBufCodecs.VAR_INT, GardenDropEntry::weight,
            ByteBufCodecs.VAR_INT, GardenDropEntry::minCount,
            ByteBufCodecs.VAR_INT, GardenDropEntry::maxCount,
            GardenDropEntry::new
    );
}
