package de.project.ae2virtualgarden.registry;

import de.project.ae2virtualgarden.AE2VirtualGarden;
import de.project.ae2virtualgarden.cell.GardenCellTier;
import de.project.ae2virtualgarden.cell.VirtualGardenCellItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AE2VirtualGarden.MODID);

    // Housing
    public static final DeferredHolder<Item, Item> GARDEN_CELL_HOUSING =
            ITEMS.register("garden_cell_housing", () -> new Item(new Item.Properties()));

    // Storage Components
    public static final DeferredHolder<Item, Item> GARDEN_COMPONENT_1K =
            ITEMS.register("garden_cell_component_1k", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> GARDEN_COMPONENT_4K =
            ITEMS.register("garden_cell_component_4k", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> GARDEN_COMPONENT_16K =
            ITEMS.register("garden_cell_component_16k", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> GARDEN_COMPONENT_64K =
            ITEMS.register("garden_cell_component_64k", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> GARDEN_COMPONENT_256K =
            ITEMS.register("garden_cell_component_256k", () -> new Item(new Item.Properties()));

    // Complete Storage Cells
    public static final DeferredHolder<Item, VirtualGardenCellItem> GARDEN_CELL_1K =
            ITEMS.register("garden_storage_cell_1k", () -> new VirtualGardenCellItem(GardenCellTier.TIER_1K, new Item.Properties()));
    public static final DeferredHolder<Item, VirtualGardenCellItem> GARDEN_CELL_4K =
            ITEMS.register("garden_storage_cell_4k", () -> new VirtualGardenCellItem(GardenCellTier.TIER_4K, new Item.Properties()));
    public static final DeferredHolder<Item, VirtualGardenCellItem> GARDEN_CELL_16K =
            ITEMS.register("garden_storage_cell_16k", () -> new VirtualGardenCellItem(GardenCellTier.TIER_16K, new Item.Properties()));
    public static final DeferredHolder<Item, VirtualGardenCellItem> GARDEN_CELL_64K =
            ITEMS.register("garden_storage_cell_64k", () -> new VirtualGardenCellItem(GardenCellTier.TIER_64K, new Item.Properties()));
    public static final DeferredHolder<Item, VirtualGardenCellItem> GARDEN_CELL_256K =
            ITEMS.register("garden_storage_cell_256k", () -> new VirtualGardenCellItem(GardenCellTier.TIER_256K, new Item.Properties()));
}
