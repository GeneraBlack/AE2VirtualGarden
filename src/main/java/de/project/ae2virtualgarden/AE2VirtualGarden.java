package de.project.ae2virtualgarden;

import appeng.api.networking.GridServices;
import appeng.api.storage.StorageCells;
import appeng.core.definitions.AEItems;
import de.project.ae2virtualgarden.cell.VirtualGardenCellHandler;
import de.project.ae2virtualgarden.config.VirtualGardenConfig;
import de.project.ae2virtualgarden.network.IVirtualGardenGridService;
import de.project.ae2virtualgarden.network.VirtualGardenGridService;
import de.project.ae2virtualgarden.registry.ModBlockEntities;
import de.project.ae2virtualgarden.registry.ModBlocks;
import de.project.ae2virtualgarden.registry.ModCreativeTabs;
import de.project.ae2virtualgarden.registry.ModDataComponents;
import de.project.ae2virtualgarden.registry.ModItems;
import de.project.ae2virtualgarden.registry.ModMenus;
import de.project.ae2virtualgarden.registry.ModRecipes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Mod(AE2VirtualGarden.MODID)
public class AE2VirtualGarden {
    public static final String MODID = "ae2virtualgarden";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public AE2VirtualGarden(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Initializing AE2 Virtual Garden");

        // Register Config
        modContainer.registerConfig(ModConfig.Type.COMMON, VirtualGardenConfig.SPEC);

        // Register Registries
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModRecipes.SERIALIZERS.register(modEventBus);
        ModRecipes.RECIPE_TYPES.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);

        // Register Grid Service during mod init
        GridServices.register(IVirtualGardenGridService.class, VirtualGardenGridService.class);

        // Register Setup Listener
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(de.project.ae2virtualgarden.network.VirtualPartitionerNetworking::onRegisterPayloadHandlers);

        // BUG-01 Fix: Guard client-only screen registration
        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(de.project.ae2virtualgarden.client.VirtualPartitionerClient::onRegisterMenuScreens);
        }

        // Refresh recipe cache and clear dynamic cache when tags/datapacks update
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.TagsUpdatedEvent event) -> {
            de.project.ae2virtualgarden.recipe.GardenDropRegistry.clearCache();
            var server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                de.project.ae2virtualgarden.recipe.GardenDropRegistry.refreshRecipeCache(server.getRecipeManager());
                LOGGER.info("AE2 Virtual Garden: Refreshed recipe cache");
            }
        });
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("Registering AE2 Virtual Garden Storage Cell Handler");
            StorageCells.addCellHandler(new VirtualGardenCellHandler());

            // Register Upgrades on all Garden Storage Cells
            for (var cell : List.of(
                    ModItems.GARDEN_CELL_1K,
                    ModItems.GARDEN_CELL_4K,
                    ModItems.GARDEN_CELL_16K,
                    ModItems.GARDEN_CELL_64K,
                    ModItems.GARDEN_CELL_256K
            )) {
                appeng.api.upgrades.Upgrades.add(AEItems.SPEED_CARD.asItem(), cell.get(), 4);
                appeng.api.upgrades.Upgrades.add(ModItems.VOID_SECONDARY_CARD.get(), cell.get(), 1);
                appeng.api.upgrades.Upgrades.add(AEItems.VOID_CARD.asItem(), cell.get(), 1);
                for (String ns : List.of("ae2virtualmine", "ae2virtualbattle", "ae2virtualwell")) {
                    net.minecraft.resources.ResourceLocation loc = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(ns, "void_secondary_card");
                    if (net.minecraft.core.registries.BuiltInRegistries.ITEM.containsKey(loc)) {
                        net.minecraft.world.item.Item sisterCard = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(loc);
                        appeng.api.upgrades.Upgrades.add(sisterCard, cell.get(), 1);
                    }
                }
            }
        });
    }
}
