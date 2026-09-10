package de.project.ae2virtualgarden.registry;

import de.project.ae2virtualgarden.AE2VirtualGarden;
import de.project.ae2virtualgarden.recipe.GardenDropRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, AE2VirtualGarden.MODID);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, AE2VirtualGarden.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<GardenDropRecipe>> GARDEN_DROP_TYPE =
            RECIPE_TYPES.register("garden_drop", () -> new RecipeType<GardenDropRecipe>() {
                @Override
                public String toString() {
                    return AE2VirtualGarden.MODID + ":garden_drop";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GardenDropRecipe>> GARDEN_DROP_SERIALIZER =
            SERIALIZERS.register("garden_drop", () -> new RecipeSerializer<>(GardenDropRecipe.CODEC, GardenDropRecipe.STREAM_CODEC));
}
