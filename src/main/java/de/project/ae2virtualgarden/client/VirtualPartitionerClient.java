package de.project.ae2virtualgarden.client;

import de.project.ae2virtualgarden.client.gui.VirtualPartitionerScreen;
import de.project.ae2virtualgarden.registry.ModMenus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class VirtualPartitionerClient {
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.PARTITIONER_MENU.get(), VirtualPartitionerScreen::new);
    }
}
