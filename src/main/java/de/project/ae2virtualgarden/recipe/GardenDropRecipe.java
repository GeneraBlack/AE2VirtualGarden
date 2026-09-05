package de.project.ae2virtualgarden.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.project.ae2virtualgarden.registry.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.List;

public record GardenDropRecipe(Ingredient seed, int minTier, List<GardenDropEntry> drops) implements Recipe<SingleRecipeInput> {

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return seed.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        if (!drops.isEmpty()) {
            return drops.get(0).item().copy();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        if (!drops.isEmpty()) {
            return drops.get(0).item();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.GARDEN_DROP_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.GARDEN_DROP_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<GardenDropRecipe> {
        public static final MapCodec<GardenDropRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("seed").forGetter(GardenDropRecipe::seed),
                Codec.INT.optionalFieldOf("min_tier", 1).forGetter(GardenDropRecipe::minTier),
                GardenDropEntry.CODEC.listOf().fieldOf("drops").forGetter(GardenDropRecipe::drops)
        ).apply(instance, GardenDropRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, GardenDropRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, GardenDropRecipe::seed,
                ByteBufCodecs.VAR_INT, GardenDropRecipe::minTier,
                GardenDropEntry.STREAM_CODEC.apply(ByteBufCodecs.list()), GardenDropRecipe::drops,
                GardenDropRecipe::new
        );

        @Override
        public MapCodec<GardenDropRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GardenDropRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
