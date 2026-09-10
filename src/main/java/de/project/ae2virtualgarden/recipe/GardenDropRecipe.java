package de.project.ae2virtualgarden.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.project.ae2virtualgarden.registry.ModRecipes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.List;

public record GardenDropRecipe(Ingredient seed, int minTier, List<GardenDropEntry> drops) implements Recipe<SingleRecipeInput> {

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
    public boolean matches(SingleRecipeInput input, Level level) {
        return seed.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input) {
        if (!drops.isEmpty()) {
            return drops.get(0).item().copy();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public RecipeSerializer<GardenDropRecipe> getSerializer() {
        return ModRecipes.GARDEN_DROP_SERIALIZER.get();
    }

    @Override
    public RecipeType<GardenDropRecipe> getType() {
        return ModRecipes.GARDEN_DROP_TYPE.get();
    }
}
