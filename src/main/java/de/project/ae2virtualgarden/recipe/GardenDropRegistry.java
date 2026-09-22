package de.project.ae2virtualgarden.recipe;

import de.project.ae2virtualgarden.cell.GardenCellTier;
import de.project.ae2virtualgarden.config.VirtualGardenConfig;
import de.project.ae2virtualgarden.registry.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GardenDropRegistry {

    public static final TagKey<Item> CROPS_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "crops"));
    public static final TagKey<Item> SEEDS_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "seeds"));
    public static final TagKey<Item> SAPLINGS_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "saplings"));
    public static final TagKey<Item> MUSHROOMS_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "mushrooms"));

    private static final Map<Item, List<GardenDropEntry>> BUILTIN_DROPS = new HashMap<>();
    private static final Map<Item, List<GardenDropEntry>> DYNAMIC_CACHE = new HashMap<>();

    static {
        registerTreeDefaults();
        registerCropDefaults();
    }

    private static void registerTreeDefaults() {
        // Oak
        addBuiltin(Items.OAK_SAPLING, List.of(
                new GardenDropEntry(new ItemStack(Items.OAK_LOG), 60, 1, 1),
                new GardenDropEntry(new ItemStack(Items.OAK_SAPLING), 15, 1, 1),
                new GardenDropEntry(new ItemStack(Items.APPLE), 5, 1, 1),
                new GardenDropEntry(new ItemStack(Items.STICK), 20, 1, 2)
        ));

        // Spruce
        addBuiltin(Items.SPRUCE_SAPLING, List.of(
                new GardenDropEntry(new ItemStack(Items.SPRUCE_LOG), 65, 1, 1),
                new GardenDropEntry(new ItemStack(Items.SPRUCE_SAPLING), 15, 1, 1),
                new GardenDropEntry(new ItemStack(Items.STICK), 20, 1, 2)
        ));

        // Birch
        addBuiltin(Items.BIRCH_SAPLING, List.of(
                new GardenDropEntry(new ItemStack(Items.BIRCH_LOG), 65, 1, 1),
                new GardenDropEntry(new ItemStack(Items.BIRCH_SAPLING), 15, 1, 1),
                new GardenDropEntry(new ItemStack(Items.STICK), 20, 1, 2)
        ));

        // Jungle
        addBuiltin(Items.JUNGLE_SAPLING, List.of(
                new GardenDropEntry(new ItemStack(Items.JUNGLE_LOG), 60, 1, 1),
                new GardenDropEntry(new ItemStack(Items.JUNGLE_SAPLING), 15, 1, 1),
                new GardenDropEntry(new ItemStack(Items.COCOA_BEANS), 10, 1, 2),
                new GardenDropEntry(new ItemStack(Items.STICK), 15, 1, 2)
        ));

        // Acacia
        addBuiltin(Items.ACACIA_SAPLING, List.of(
                new GardenDropEntry(new ItemStack(Items.ACACIA_LOG), 65, 1, 1),
                new GardenDropEntry(new ItemStack(Items.ACACIA_SAPLING), 15, 1, 1),
                new GardenDropEntry(new ItemStack(Items.STICK), 20, 1, 2)
        ));

        // Dark Oak
        addBuiltin(Items.DARK_OAK_SAPLING, List.of(
                new GardenDropEntry(new ItemStack(Items.DARK_OAK_LOG), 60, 1, 1),
                new GardenDropEntry(new ItemStack(Items.DARK_OAK_SAPLING), 15, 1, 1),
                new GardenDropEntry(new ItemStack(Items.APPLE), 5, 1, 1),
                new GardenDropEntry(new ItemStack(Items.STICK), 20, 1, 2)
        ));

        // Mangrove
        addBuiltin(Items.MANGROVE_PROPAGULE, List.of(
                new GardenDropEntry(new ItemStack(Items.MANGROVE_LOG), 55, 1, 1),
                new GardenDropEntry(new ItemStack(Items.MANGROVE_ROOTS), 15, 1, 1),
                new GardenDropEntry(new ItemStack(Items.MANGROVE_PROPAGULE), 15, 1, 1),
                new GardenDropEntry(new ItemStack(Items.STICK), 15, 1, 2)
        ));

        // Cherry
        addBuiltin(Items.CHERRY_SAPLING, List.of(
                new GardenDropEntry(new ItemStack(Items.CHERRY_LOG), 65, 1, 1),
                new GardenDropEntry(new ItemStack(Items.CHERRY_SAPLING), 15, 1, 1),
                new GardenDropEntry(new ItemStack(Items.STICK), 20, 1, 2)
        ));

        // Bamboo
        addBuiltin(Items.BAMBOO, List.of(
                new GardenDropEntry(new ItemStack(Items.BAMBOO), 100, 1, 2)
        ));

        // Crimson Fungus
        addBuiltin(Items.CRIMSON_FUNGUS, List.of(
                new GardenDropEntry(new ItemStack(Items.CRIMSON_STEM), 65, 1, 1),
                new GardenDropEntry(new ItemStack(Items.NETHER_WART_BLOCK), 15, 1, 1),
                new GardenDropEntry(new ItemStack(Items.SHROOMLIGHT), 5, 1, 1),
                new GardenDropEntry(new ItemStack(Items.CRIMSON_FUNGUS), 15, 1, 1)
        ));

        // Warped Fungus
        addBuiltin(Items.WARPED_FUNGUS, List.of(
                new GardenDropEntry(new ItemStack(Items.WARPED_STEM), 65, 1, 1),
                new GardenDropEntry(new ItemStack(Items.WARPED_WART_BLOCK), 15, 1, 1),
                new GardenDropEntry(new ItemStack(Items.SHROOMLIGHT), 5, 1, 1),
                new GardenDropEntry(new ItemStack(Items.WARPED_FUNGUS), 15, 1, 1)
        ));

        // Chorus
        addBuiltin(Items.CHORUS_FLOWER, List.of(
                new GardenDropEntry(new ItemStack(Items.CHORUS_FRUIT), 85, 1, 2),
                new GardenDropEntry(new ItemStack(Items.CHORUS_FLOWER), 15, 1, 1)
        ));
    }

    private static void registerCropDefaults() {
        // Wheat
        addBuiltin(Items.WHEAT_SEEDS, List.of(
                new GardenDropEntry(new ItemStack(Items.WHEAT), 65, 1, 1),
                new GardenDropEntry(new ItemStack(Items.WHEAT_SEEDS), 35, 1, 2)
        ));

        // Carrot
        addBuiltin(Items.CARROT, List.of(
                new GardenDropEntry(new ItemStack(Items.CARROT), 100, 1, 2)
        ));

        // Potato
        addBuiltin(Items.POTATO, List.of(
                new GardenDropEntry(new ItemStack(Items.POTATO), 95, 1, 2),
                new GardenDropEntry(new ItemStack(Items.POISONOUS_POTATO), 5, 1, 1)
        ));

        // Beetroot
        addBuiltin(Items.BEETROOT_SEEDS, List.of(
                new GardenDropEntry(new ItemStack(Items.BEETROOT), 65, 1, 1),
                new GardenDropEntry(new ItemStack(Items.BEETROOT_SEEDS), 35, 1, 2)
        ));

        // Melon
        addBuiltin(Items.MELON_SEEDS, List.of(
                new GardenDropEntry(new ItemStack(Items.MELON_SLICE), 80, 1, 3),
                new GardenDropEntry(new ItemStack(Items.MELON_SEEDS), 20, 1, 1)
        ));

        // Pumpkin
        addBuiltin(Items.PUMPKIN_SEEDS, List.of(
                new GardenDropEntry(new ItemStack(Items.PUMPKIN), 75, 1, 1),
                new GardenDropEntry(new ItemStack(Items.PUMPKIN_SEEDS), 25, 1, 2)
        ));

        // Nether Wart
        addBuiltin(Items.NETHER_WART, List.of(
                new GardenDropEntry(new ItemStack(Items.NETHER_WART), 100, 1, 2)
        ));

        // Sugar Cane
        addBuiltin(Items.SUGAR_CANE, List.of(
                new GardenDropEntry(new ItemStack(Items.SUGAR_CANE), 100, 1, 2)
        ));

        // Cactus
        addBuiltin(Items.CACTUS, List.of(
                new GardenDropEntry(new ItemStack(Items.CACTUS), 100, 1, 2)
        ));

        // Kelp
        addBuiltin(Items.KELP, List.of(
                new GardenDropEntry(new ItemStack(Items.KELP), 100, 1, 2)
        ));

        // Sweet Berries
        addBuiltin(Items.SWEET_BERRIES, List.of(
                new GardenDropEntry(new ItemStack(Items.SWEET_BERRIES), 100, 1, 2)
        ));

        // Glow Berries
        addBuiltin(Items.GLOW_BERRIES, List.of(
                new GardenDropEntry(new ItemStack(Items.GLOW_BERRIES), 100, 1, 2)
        ));

        // Cocoa Beans
        addBuiltin(Items.COCOA_BEANS, List.of(
                new GardenDropEntry(new ItemStack(Items.COCOA_BEANS), 100, 1, 2)
        ));

        // Torchflower
        addBuiltin(Items.TORCHFLOWER_SEEDS, List.of(
                new GardenDropEntry(new ItemStack(Items.TORCHFLOWER), 70, 1, 1),
                new GardenDropEntry(new ItemStack(Items.TORCHFLOWER_SEEDS), 30, 1, 1)
        ));

        // Pitcher Plant
        addBuiltin(Items.PITCHER_POD, List.of(
                new GardenDropEntry(new ItemStack(Items.PITCHER_PLANT), 70, 1, 1),
                new GardenDropEntry(new ItemStack(Items.PITCHER_POD), 30, 1, 1)
        ));

        // Mushrooms
        addBuiltin(Items.BROWN_MUSHROOM, List.of(
                new GardenDropEntry(new ItemStack(Items.BROWN_MUSHROOM), 100, 1, 2)
        ));
        addBuiltin(Items.RED_MUSHROOM, List.of(
                new GardenDropEntry(new ItemStack(Items.RED_MUSHROOM), 100, 1, 2)
        ));
    }

    private static void addBuiltin(Item seed, List<GardenDropEntry> drops) {
        BUILTIN_DROPS.put(seed, drops);
    }

    public static boolean isValidSeed(Item item, @Nullable Level level) {
        if (item == null || item.equals(Items.AIR)) {
            return false;
        }

        // 1. Built-in defaults
        if (BUILTIN_DROPS.containsKey(item) || DYNAMIC_CACHE.containsKey(item)) {
            return true;
        }

        // 2. Datapack recipes
        if (level != null) {
            SingleRecipeInput input = new SingleRecipeInput(new ItemStack(item));
            if (level.getRecipeManager().getRecipeFor(ModRecipes.GARDEN_DROP_TYPE.get(), input, level).isPresent()) {
                return true;
            }
        }

        // 3. If dynamic fallback is disabled, only built-in drops and custom datapack recipes are valid
        if (!VirtualGardenConfig.ENABLE_DYNAMIC_FALLBACK.get()) {
            return false;
        }

        // 4. Conventional Tags check
        ItemStack stack = new ItemStack(item);
        if (stack.is(CROPS_TAG) || stack.is(SEEDS_TAG) || stack.is(SAPLINGS_TAG) || stack.is(ItemTags.SAPLINGS) || stack.is(MUSHROOMS_TAG)) {
            return true;
        }

        // 5. Block classes (strictly CropBlock or SaplingBlock, NO loose BonemealableBlock!)
        if (item instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block instanceof CropBlock || block instanceof SaplingBlock) {
                return true;
            }
        }

        // 6. Seed/sapling naming convention
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        String path = id.getPath();
        return path.endsWith("_seeds") || path.endsWith("_seed") || path.endsWith("_sapling");
    }

    public static List<GardenDropEntry> getDropEntries(Item seed, Level level) {
        if (seed == null || seed.equals(Items.AIR)) {
            return Collections.emptyList();
        }

        // 1. Custom Datapack Recipe
        if (level != null) {
            SingleRecipeInput input = new SingleRecipeInput(new ItemStack(seed));
            Optional<RecipeHolder<GardenDropRecipe>> recipe =
                    level.getRecipeManager().getRecipeFor(ModRecipes.GARDEN_DROP_TYPE.get(), input, level);
            if (recipe.isPresent()) {
                return recipe.get().value().drops();
            }
        }

        // 2. Built-in defaults
        if (BUILTIN_DROPS.containsKey(seed)) {
            return BUILTIN_DROPS.get(seed);
        }

        // 3. Dynamic Cache
        if (DYNAMIC_CACHE.containsKey(seed)) {
            return DYNAMIC_CACHE.get(seed);
        }

        // 4. Dynamic Fallback: only if enabled AND strictly valid
        if (!VirtualGardenConfig.ENABLE_DYNAMIC_FALLBACK.get() || !isValidSeed(seed, level)) {
            return Collections.emptyList();
        }

        if (level instanceof ServerLevel serverLevel) {
            List<GardenDropEntry> discovered = autoDiscoverDrops(seed, serverLevel);
            if (discovered != null && !discovered.isEmpty()) {
                DYNAMIC_CACHE.put(seed, discovered);
                return discovered;
            }
        }

        return Collections.emptyList();
    }

    private static List<GardenDropEntry> autoDiscoverDrops(Item seed, ServerLevel serverLevel) {
        List<GardenDropEntry> entries = new ArrayList<>();

        if (seed instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();

            // 1. Modded / Vanilla Crop Block: simulate drops at max maturity
            if (block instanceof CropBlock cropBlock) {
                BlockState matureState = cropBlock.getStateForAge(cropBlock.getMaxAge());
                List<ItemStack> simulatedDrops = Block.getDrops(matureState, serverLevel, BlockPos.ZERO, null);
                if (simulatedDrops != null && !simulatedDrops.isEmpty()) {
                    for (ItemStack drop : simulatedDrops) {
                        if (drop.isEmpty()) continue;
                        boolean isSeed = drop.is(seed);
                        int weight = isSeed ? 35 : 65;
                        entries.add(new GardenDropEntry(drop.copy(), weight, 1, Math.max(1, drop.getCount())));
                    }
                    return entries;
                }
            }

            // 2. Modded Sapling: lookup corresponding log, leaves, stick
            ItemStack stack = new ItemStack(seed);
            if (block instanceof SaplingBlock || stack.is(ItemTags.SAPLINGS)) {
                ResourceLocation id = BuiltInRegistries.ITEM.getKey(seed);
                String path = id.getPath();
                String base = path.replace("_sapling", "");

                ResourceLocation logId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), base + "_log");
                ResourceLocation stemId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), base + "_stem");
                ResourceLocation leavesId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), base + "_leaves");

                Item logItem = BuiltInRegistries.ITEM.get(logId);
                if (logItem.equals(Items.AIR)) {
                    logItem = BuiltInRegistries.ITEM.get(stemId);
                }
                Item leavesItem = BuiltInRegistries.ITEM.get(leavesId);

                if (!logItem.equals(Items.AIR)) {
                    entries.add(new GardenDropEntry(new ItemStack(logItem), 65, 1, 1));
                }
                entries.add(new GardenDropEntry(new ItemStack(seed), 15, 1, 1));
                if (!leavesItem.equals(Items.AIR)) {
                    entries.add(new GardenDropEntry(new ItemStack(leavesItem), 10, 1, 1));
                }
                entries.add(new GardenDropEntry(new ItemStack(Items.STICK), 10, 1, 2));

                if (!entries.isEmpty()) {
                    return entries;
                }
            }
        }

        // 4. Modded seed without BlockItem: pattern match name (e.g. mod:cotton_seeds -> mod:cotton)
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(seed);
        String path = id.getPath();
        if (path.endsWith("_seeds") || path.endsWith("_seed")) {
            String cropPath = path.replaceAll("_seeds?$", "");
            ResourceLocation cropId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), cropPath);
            Item cropItem = BuiltInRegistries.ITEM.get(cropId);
            if (!cropItem.equals(Items.AIR)) {
                entries.add(new GardenDropEntry(new ItemStack(cropItem), 65, 1, 2));
                entries.add(new GardenDropEntry(new ItemStack(seed), 35, 1, 2));
                return entries;
            }
        }

        return entries;
    }

    public static ItemStack rollDrop(List<GardenDropEntry> entries, RandomSource random) {
        if (entries == null || entries.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int totalWeight = 0;
        for (GardenDropEntry entry : entries) {
            totalWeight += entry.weight();
        }

        if (totalWeight <= 0) {
            return ItemStack.EMPTY;
        }

        int roll = random.nextInt(totalWeight);
        int current = 0;
        for (GardenDropEntry entry : entries) {
            current += entry.weight();
            if (roll < current) {
                int count = entry.minCount();
                if (entry.maxCount() > entry.minCount()) {
                    count += random.nextInt(entry.maxCount() - entry.minCount() + 1);
                }
                ItemStack result = entry.item().copy();
                result.setCount(count);
                return result;
            }
        }

        return ItemStack.EMPTY;
    }
}
