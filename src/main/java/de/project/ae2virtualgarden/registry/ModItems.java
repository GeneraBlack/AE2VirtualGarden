package de.project.ae2virtualgarden.registry;

import de.project.ae2virtualgarden.AE2VirtualGarden;
import de.project.ae2virtualgarden.cell.GardenCellTier;
import de.project.ae2virtualgarden.cell.VirtualGardenCellItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AE2VirtualGarden.MODID);

    // Housing
    public static final DeferredItem<Item> GARDEN_CELL_HOUSING =
            ITEMS.registerSimpleItem("garden_cell_housing");

    // Storage Components
    public static final DeferredItem<Item> GARDEN_COMPONENT_1K =
            ITEMS.registerSimpleItem("garden_cell_component_1k");
    public static final DeferredItem<Item> GARDEN_COMPONENT_4K =
            ITEMS.registerSimpleItem("garden_cell_component_4k");
    public static final DeferredItem<Item> GARDEN_COMPONENT_16K =
            ITEMS.registerSimpleItem("garden_cell_component_16k");
    public static final DeferredItem<Item> GARDEN_COMPONENT_64K =
            ITEMS.registerSimpleItem("garden_cell_component_64k");
    public static final DeferredItem<Item> GARDEN_COMPONENT_256K =
            ITEMS.registerSimpleItem("garden_cell_component_256k");

    // Complete Storage Cells
    public static final DeferredItem<VirtualGardenCellItem> GARDEN_CELL_1K =
            ITEMS.registerItem("garden_storage_cell_1k", props -> new VirtualGardenCellItem(GardenCellTier.TIER_1K, props));
    public static final DeferredItem<VirtualGardenCellItem> GARDEN_CELL_4K =
            ITEMS.registerItem("garden_storage_cell_4k", props -> new VirtualGardenCellItem(GardenCellTier.TIER_4K, props));
    public static final DeferredItem<VirtualGardenCellItem> GARDEN_CELL_16K =
            ITEMS.registerItem("garden_storage_cell_16k", props -> new VirtualGardenCellItem(GardenCellTier.TIER_16K, props));
    public static final DeferredItem<VirtualGardenCellItem> GARDEN_CELL_64K =
            ITEMS.registerItem("garden_storage_cell_64k", props -> new VirtualGardenCellItem(GardenCellTier.TIER_64K, props));
    public static final DeferredItem<VirtualGardenCellItem> GARDEN_CELL_256K =
            ITEMS.registerItem("garden_storage_cell_256k", props -> new VirtualGardenCellItem(GardenCellTier.TIER_256K, props));
}

