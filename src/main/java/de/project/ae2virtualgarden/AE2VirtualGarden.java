package de.project.ae2virtualgarden;

import appeng.api.networking.GridServices;
import appeng.api.storage.StorageCells;
import de.project.ae2virtualgarden.cell.VirtualGardenCellHandler;
import de.project.ae2virtualgarden.config.VirtualGardenConfig;
import de.project.ae2virtualgarden.network.IVirtualGardenGridService;
import de.project.ae2virtualgarden.network.VirtualGardenGridService;
import de.project.ae2virtualgarden.registry.ModCreativeTabs;
import de.project.ae2virtualgarden.registry.ModItems;
import de.project.ae2virtualgarden.registry.ModRecipes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(AE2VirtualGarden.MODID)
public class AE2VirtualGarden {
    public static final String MODID = "ae2virtualgarden";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public AE2VirtualGarden(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Initializing AE2 Virtual Garden");

        // Register Config
        modContainer.registerConfig(ModConfig.Type.COMMON, VirtualGardenConfig.SPEC);

        // Register Registries
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModRecipes.SERIALIZERS.register(modEventBus);
        ModRecipes.RECIPE_TYPES.register(modEventBus);

        // Register Grid Service during mod init
        GridServices.register(IVirtualGardenGridService.class, VirtualGardenGridService.class);

        // Register Setup Listener
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("Registering AE2 Virtual Garden Storage Cell Handler");
            StorageCells.addCellHandler(new VirtualGardenCellHandler());
        });
    }
}
