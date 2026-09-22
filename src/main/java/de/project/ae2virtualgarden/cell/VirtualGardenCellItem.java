package de.project.ae2virtualgarden.cell;

import appeng.api.config.FuzzyMode;
import appeng.api.ids.AEComponents;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.storage.cells.StorageCell;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.AEConfig;
import appeng.core.localization.Tooltips;
import appeng.items.contents.CellConfig;
import appeng.items.storage.StorageCellTooltipComponent;
import appeng.util.ConfigInventory;
import de.project.ae2virtualgarden.config.VirtualGardenConfig;
import de.project.ae2virtualgarden.recipe.GardenDropRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.*;

public class VirtualGardenCellItem extends Item implements ICellWorkbenchItem {

    private final GardenCellTier tier;

    public VirtualGardenCellItem(GardenCellTier tier, Properties properties) {
        super(properties.stacksTo(1));
        this.tier = tier;
    }

    public GardenCellTier getTier() {
        return tier;
    }

    public int getBytes(ItemStack stack) {
        return tier.getTotalBytes();
    }

    public int getBytesPerType(ItemStack stack) {
        return tier.getBytesPerType();
    }

    public int getTotalTypes(ItemStack stack) {
        return tier.getTotalTypes();
    }

    public double getIdleDrain() {
        return tier.getIdleDrain();
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack stack) {
        return UpgradeInventories.forItem(stack, 4);
    }

    @Override
    public ConfigInventory getConfigInventory(ItemStack stack) {
        var holder = new Holder(stack);
        holder.inv = ConfigInventory.configTypes(63)
                .supportedTypes(AEKeyType.items())
                .slotFilter((slot, what) -> {
                    if (!(what instanceof AEItemKey itemKey)) {
                        return false;
                    }
                    Item item = itemKey.getItem();
                    if (!GardenDropRegistry.isValidSeed(item, null)) {
                        return false;
                    }
                    if (VirtualGardenConfig.ENFORCE_INVENTORY_CHECK.get()) {
                        return playerHasItem(stack, item);
                    }
                    return true;
                })
                .changeListener(holder::save)
                .build();
        holder.load();
        return holder.inv;
    }

    private static class Holder {
        private final ItemStack stack;
        private ConfigInventory inv;

        public Holder(ItemStack stack) {
            this.stack = stack;
        }

        public void load() {
            inv.readFromList(stack.getOrDefault(AEComponents.STORAGE_CELL_CONFIG_INV, List.of()));
        }

        public void save() {
            stack.set(AEComponents.STORAGE_CELL_CONFIG_INV, inv.toList());
        }
    }

    private static boolean playerHasItem(ItemStack cellStack, Item item) {
        var server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            for (var player : server.getPlayerList().getPlayers()) {
                if (player.containerMenu instanceof appeng.menu.implementations.CellWorkbenchMenu menu) {
                    if (ItemStack.isSameItemSameComponents(menu.getWorkbenchItem(), cellStack)) {
                        return player.getInventory().contains(new ItemStack(item));
                    }
                }
            }
        }

        if (appeng.util.Platform.isClient()) {
            return ClientInventoryCheck.hasItem(item);
        }

        return false;
    }

    private static class ClientInventoryCheck {
        static boolean hasItem(Item item) {
            var mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.player != null) {
                return mc.player.getInventory().contains(new ItemStack(item));
            }
            return false;
        }
    }

    @Override
    public FuzzyMode getFuzzyMode(ItemStack stack) {
        return stack.getOrDefault(AEComponents.STORAGE_CELL_FUZZY_MODE, FuzzyMode.IGNORE_ALL);
    }

    @Override
    public void setFuzzyMode(ItemStack stack, FuzzyMode mode) {
        stack.set(AEComponents.STORAGE_CELL_FUZZY_MODE, mode);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);

        StorageCell cell = StorageCells.getCellInventory(stack, null);
        if (cell instanceof VirtualGardenCellInventory gardenInv) {
            lines.add(Tooltips.bytesUsed(gardenInv.getUsedBytes(), gardenInv.getTotalBytes()));
            lines.add(Tooltips.typesUsed(gardenInv.getStoredItemTypes(), gardenInv.getTotalItemTypes()));
        } else {
            lines.add(Tooltips.bytesUsed(0, tier.getTotalBytes()));
            lines.add(Tooltips.typesUsed(0, tier.getTotalTypes()));
        }

        int drops = tier.getDropCount();
        int intervalTicks = VirtualGardenConfig.BASE_TICK_INTERVAL.get();
        double seconds = intervalTicks / 20.0;

        lines.add(Component.translatable("tooltip.ae2virtualgarden.tier", tier.getTierName())
                .withStyle(ChatFormatting.AQUA));
        lines.add(Component.translatable("tooltip.ae2virtualgarden.production", drops, String.format(Locale.ROOT, "%.1f", seconds))
                .withStyle(ChatFormatting.GRAY));

        List<GenericStack> config = stack.get(AEComponents.STORAGE_CELL_CONFIG_INV);
        Item configuredItem = null;
        if (config != null && !config.isEmpty()) {
            for (GenericStack entry : config) {
                if (entry != null && entry.what() instanceof AEItemKey itemKey) {
                    configuredItem = itemKey.getItem();
                    break;
                }
            }
        }

        if (configuredItem != null) {
            lines.add(Component.translatable("tooltip.ae2virtualgarden.configured_plant",
                            Component.translatable(configuredItem.getDescriptionId()))
                    .withStyle(ChatFormatting.GREEN));
        } else {
            lines.add(Component.translatable("tooltip.ae2virtualgarden.not_configured")
                    .withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        StorageCell cell = StorageCells.getCellInventory(stack, null);
        if (!(cell instanceof VirtualGardenCellInventory gardenInv)) {
            return Optional.empty();
        }

        List<ItemStack> upgradeStacks = new ArrayList<>();
        try {
            if (AEConfig.instance().isTooltipShowCellUpgrades()) {
                for (ItemStack upgrade : getUpgrades(stack)) {
                    if (!upgrade.isEmpty()) {
                        upgradeStacks.add(upgrade);
                    }
                }
            }
        } catch (Throwable ignored) {
        }

        List<GenericStack> content = new ArrayList<>();
        try {
            if (AEConfig.instance().isTooltipShowCellContent()) {
                int maxCountShown = AEConfig.instance().getTooltipMaxCellContentShown();
                KeyCounter availableStacks = new KeyCounter();
                gardenInv.getAvailableStacks(availableStacks);
                for (var entry : availableStacks) {
                    content.add(new GenericStack(entry.getKey(), entry.getLongValue()));
                }

                content.sort(Comparator.comparingLong(GenericStack::amount).reversed());
                boolean hasMoreContent = content.size() > maxCountShown;
                if (content.size() > maxCountShown) {
                    content = new ArrayList<>(content.subList(0, maxCountShown));
                }
                return Optional.of(new StorageCellTooltipComponent(upgradeStacks, content, hasMoreContent, true));
            }
        } catch (Throwable ignored) {
        }

        return Optional.of(new StorageCellTooltipComponent(upgradeStacks, Collections.emptyList(), false, true));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        InteractionHand otherHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack otherStack = player.getItemInHand(otherHand);

        if (player.isShiftKeyDown()) {
            if (!otherStack.isEmpty()) {
                // Quick-partition using item in off-hand
                if (GardenDropRegistry.isValidSeed(otherStack.getItem(), level)) {
                    if (VirtualGardenConfig.ENFORCE_INVENTORY_CHECK.get() && !player.getInventory().contains(otherStack)) {
                        return InteractionResultHolder.fail(stack);
                    }
                    if (!level.isClientSide()) {
                        AEItemKey key = AEItemKey.of(otherStack.getItem());
                        stack.set(AEComponents.STORAGE_CELL_CONFIG_INV, List.of(new GenericStack(key, 1)));
                        player.displayClientMessage(Component.translatable("message.ae2virtualgarden.configured",
                                Component.translatable(otherStack.getItem().getDescriptionId())).withStyle(ChatFormatting.GREEN), true);
                    }
                    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
                }
            } else {
                // Clear configuration
                if (!level.isClientSide()) {
                    stack.remove(AEComponents.STORAGE_CELL_CONFIG_INV);
                    player.displayClientMessage(Component.translatable("message.ae2virtualgarden.cleared")
                            .withStyle(ChatFormatting.RED), true);
                }
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            }
        }

        return super.use(level, player, hand);
    }
}
