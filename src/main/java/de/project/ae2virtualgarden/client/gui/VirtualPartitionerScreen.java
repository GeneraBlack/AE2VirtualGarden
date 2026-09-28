package de.project.ae2virtualgarden.client.gui;

import appeng.api.upgrades.UpgradeInventories;
import appeng.core.definitions.AEItems;
import de.project.ae2virtualgarden.cell.VirtualGardenCellItem;
import de.project.ae2virtualgarden.cell.partition.GardenCellPartition;
import de.project.ae2virtualgarden.cell.partition.GardenCellPartitionList;
import de.project.ae2virtualgarden.menu.VirtualPartitionerMenu;
import de.project.ae2virtualgarden.network.SetPartitionsPayload;
import de.project.ae2virtualgarden.recipe.GardenDropRegistry;
import de.project.ae2virtualgarden.registry.ModDataComponents;
import de.project.ae2virtualgarden.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class VirtualPartitionerScreen extends AbstractContainerScreen<VirtualPartitionerMenu> {

    private static final int[] PALETTE = {
            0xFF2E86AB, // Blue
            0xFF2BA84A, // Green
            0xFFE08D3C, // Orange
            0xFF8338EC, // Purple
            0xFFE63946, // Red
            0xFF00B4D8  // Cyan
    };

    public static class PartitionDraft {
        public Item target;
        public int percent;
        public boolean voidSecondary;

        public PartitionDraft(Item target, int percent, boolean voidSecondary) {
            this.target = target;
            this.percent = percent;
            this.voidSecondary = voidSecondary;
        }
    }

    private final List<PartitionDraft> workingList = new ArrayList<>();
    private ItemStack lastCellStack = ItemStack.EMPTY;
    private boolean dirty = false;
    private int selectedRowForPicker = -1;
    private int scrollOffset = 0;

    public VirtualPartitionerScreen(VirtualPartitionerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 220;
        this.imageHeight = 262;
        this.inventoryLabelY = 169;
        this.inventoryLabelX = 30;
    }

    @Override
    protected void init() {
        super.init();
        syncFromCell(true);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        ItemStack currentCell = menu.getSlot(0).getItem();
        if (!ItemStack.matches(currentCell, lastCellStack)) {
            syncFromCell(false);
        }
    }

    private void syncFromCell(boolean force) {
        ItemStack currentCell = menu.getSlot(0).getItem();
        if (force || !ItemStack.isSameItemSameComponents(currentCell, lastCellStack)) {
            lastCellStack = currentCell.copy();
            workingList.clear();
            selectedRowForPicker = -1;
            scrollOffset = 0;
            dirty = false;

            if (!currentCell.isEmpty() && currentCell.getItem() instanceof VirtualGardenCellItem) {
                GardenCellPartitionList list = currentCell.get(ModDataComponents.PARTITIONS.get());
                if (list != null && !list.isEmpty()) {
                    for (GardenCellPartition p : list.partitions()) {
                        workingList.add(new PartitionDraft(p.target(), p.percent(), p.voidSecondary()));
                    }
                }
            }
        }
    }

    private int getTotalPercent() {
        int sum = 0;
        for (PartitionDraft p : workingList) {
            sum += p.percent;
        }
        return sum;
    }

    private int getUnallocatedPercent() {
        return Math.max(0, 100 - getTotalPercent());
    }

    private boolean hasVoidCard() {
        ItemStack cell = menu.getSlot(0).getItem();
        if (!cell.isEmpty()) {
            if (menu.getSlot(5).hasItem()) return true;
            var upgrades = UpgradeInventories.forItem(cell, 5);
            return upgrades != null && (upgrades.isInstalled(ModItems.VOID_SECONDARY_CARD.get())
                    || upgrades.isInstalled(AEItems.VOID_CARD.asItem()));
        }
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        renderCustomTooltips(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        // Dark modern background panel
        guiGraphics.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xFF1E1E1E);

        // Header border & title area
        guiGraphics.fill(x, y, x + this.imageWidth, y + 18, 0xFF2A2A2A);
        guiGraphics.fill(x, y + 18, x + this.imageWidth, y + 19, 0xFF3D3D3D);
        guiGraphics.drawString(font, this.title, x + 8, y + 5, 0xFFE0E0E0, false);

        // Cell input slot box (x=16, y=20)
        drawSlotBox(guiGraphics, x + 15, y + 19);

        // Cell status info text
        ItemStack cell = menu.getSlot(0).getItem();
        boolean hasCell = !cell.isEmpty() && cell.getItem() instanceof VirtualGardenCellItem;
        if (hasCell) {
            String tierName = ((VirtualGardenCellItem) cell.getItem()).getTier().getTierName();
            guiGraphics.drawString(font, "Cell: " + tierName, x + 38, y + 21, 0xFF55FF55, false);
            int alloc = getTotalPercent();
            int allocColor = alloc > 100 ? 0xFFFF5555 : (alloc == 100 ? 0xFF55FF55 : 0xFFFFAA00);
            guiGraphics.drawString(font, "Allocated: " + alloc + "%", x + 38, y + 31, allocColor, false);
        } else {
            guiGraphics.drawString(font, "Insert Virtual Garden Cell", x + 38, y + 25, 0xFF888888, false);
        }

        // Partition Bar (GParted style) at y=42
        int barX = x + 12;
        int barY = y + 42;
        int barW = 196;
        int barH = 12;
        guiGraphics.fill(barX - 1, barY - 1, barX + barW + 1, barY + barH + 1, 0xFF000000);
        guiGraphics.fill(barX, barY, barX + barW, barY + barH, 0xFF333333);

        if (hasCell && !workingList.isEmpty()) {
            int currentX = barX;
            for (int i = 0; i < workingList.size(); i++) {
                PartitionDraft p = workingList.get(i);
                int segW = (int) Math.round((p.percent / 100.0) * barW);
                if (i == workingList.size() - 1 && getTotalPercent() == 100) {
                    segW = (barX + barW) - currentX;
                }
                segW = Math.max(0, Math.min(segW, (barX + barW) - currentX));
                if (segW > 0) {
                    int color = PALETTE[i % PALETTE.length];
                    guiGraphics.fill(currentX, barY, currentX + segW, barY + barH, color);
                    if (segW > 16) {
                        String pctStr = p.percent + "%";
                        int textW = font.width(pctStr);
                        guiGraphics.drawString(font, pctStr, currentX + (segW - textW) / 2, barY + 2, 0xFFFFFFFF, false);
                    }
                    currentX += segW;
                }
            }
        }

        // Table container at y=58
        int tableX = x + 12;
        int tableY = y + 58;
        int tableW = 196;
        int tableH = 76;
        guiGraphics.fill(tableX, tableY, tableX + tableW, tableY + tableH, 0xFF141414);
        guiGraphics.fill(tableX, tableY, tableX + tableW, tableY + 12, 0xFF222222);

        guiGraphics.drawString(font, "Target", tableX + 4, tableY + 2, 0xFFAAAAAA, false);
        guiGraphics.drawString(font, "Allocation", tableX + 85, tableY + 2, 0xFFAAAAAA, false);
        guiGraphics.drawString(font, "Void", tableX + 140, tableY + 2, 0xFFAAAAAA, false);

        int maxVisible = 3;
        for (int i = 0; i < maxVisible; i++) {
            int index = scrollOffset + i;
            int rowY = tableY + 14 + i * 20;

            if (index < workingList.size()) {
                PartitionDraft p = workingList.get(index);
                int color = PALETTE[index % PALETTE.length];

                // Color indicator dot
                guiGraphics.fill(tableX + 3, rowY + 5, tableX + 7, rowY + 13, color);

                // Target slot / box
                int boxColor = (selectedRowForPicker == index) ? 0xFFFFFF00 : 0xFF373737;
                guiGraphics.fill(tableX + 9, rowY - 1, tableX + 28, rowY + 18, boxColor);
                guiGraphics.fill(tableX + 10, rowY, tableX + 27, rowY + 17, 0xFF1A1A1A);

                if (p.target != null) {
                    guiGraphics.renderItem(new ItemStack(p.target), tableX + 10, rowY + 1);
                }

                // Item Name
                String name = (p.target != null) ? new ItemStack(p.target).getHoverName().getString() : "[Select]";
                if (font.width(name) > 52) {
                    name = font.plainSubstrByWidth(name, 48) + "..";
                }
                guiGraphics.drawString(font, name, tableX + 30, rowY + 4, 0xFFDDDDDD, false);

                // [-] button (x = tableX + 88)
                drawButton(guiGraphics, tableX + 88, rowY + 3, 11, 11, "-", 0xFFE0E0E0);

                // Percent text
                String pctStr = p.percent + "%";
                int pctW = font.width(pctStr);
                guiGraphics.drawString(font, pctStr, tableX + 111 - pctW / 2, rowY + 4, 0xFFFFFFFF, false);

                // [+] button (x = tableX + 122)
                drawButton(guiGraphics, tableX + 122, rowY + 3, 11, 11, "+", 0xFFE0E0E0);

                // [Void] toggle button (x = tableX + 138)
                int voidBg;
                int voidText;
                if (!hasVoidCard()) {
                    voidBg = 0xFF1A1A1A;
                    voidText = 0xFF444444;
                } else {
                    voidBg = p.voidSecondary ? 0xFF6A0DAD : 0xFF2C2C2C;
                    voidText = p.voidSecondary ? 0xFFFFFFFF : 0xFF777777;
                }
                drawButtonWithCustomBg(guiGraphics, tableX + 138, rowY + 3, 26, 11, "Void", voidText, voidBg);

                // [X] delete button (x = tableX + 172)
                drawButton(guiGraphics, tableX + 170, rowY + 3, 11, 11, "×", 0xFFFF5555);
            }
        }

        // Scroll buttons if needed
        if (workingList.size() > maxVisible) {
            drawButton(guiGraphics, tableX + tableW - 11, tableY + 14, 9, 9, "▲", scrollOffset > 0 ? 0xFFFFFFFF : 0xFF555555);
            drawButton(guiGraphics, tableX + tableW - 11, tableY + tableH - 12, 9, 9, "▼", (scrollOffset + maxVisible < workingList.size()) ? 0xFFFFFFFF : 0xFF555555);
        }

        // Action Buttons Row (y=138)
        int btnY = y + 137;
        int addColor = (hasCell && workingList.size() < 6 && getUnallocatedPercent() > 0) ? 0xFFFFFFFF : 0xFF666666;
        drawButton(guiGraphics, x + 12, btnY, 36, 14, "+ Add", addColor);

        int eqColor = (hasCell && !workingList.isEmpty()) ? 0xFFFFFFFF : 0xFF666666;
        drawButton(guiGraphics, x + 51, btnY, 44, 14, "Equalize", eqColor);

        int clearColor = (hasCell && !workingList.isEmpty()) ? 0xFFFF7777 : 0xFF666666;
        drawButton(guiGraphics, x + 98, btnY, 36, 14, "Clear", clearColor);

        int applyBg = dirty ? 0xFF2E7D32 : 0xFF2C2C2C;
        int applyText = hasCell ? 0xFFFFFFFF : 0xFF666666;
        drawButtonWithCustomBg(guiGraphics, x + 138, btnY, 70, 14, dirty ? "Apply *" : "Apply", applyText, applyBg);

        // Upgrade Slots Row (y=155)
        guiGraphics.drawString(font, "Upgrades:", x + 10, y + 158, 0xFF888888, false);
        // 4 Acceleration card slots (x = 52 + i * 18)
        for (int i = 0; i < 4; i++) {
            int slotX = x + 51 + i * 18;
            int slotY = y + 154;
            if (hasCell) {
                drawSlotBox(guiGraphics, slotX, slotY);
            } else {
                guiGraphics.fill(slotX, slotY, slotX + 18, slotY + 18, 0xFF1A1A1A);
                guiGraphics.fill(slotX + 1, slotY + 1, slotX + 17, slotY + 17, 0xFF111111);
            }
        }
        // 1 Void Secondary card slot (x = 138)
        int voidSlotX = x + 137;
        int voidSlotY = y + 154;
        if (hasCell) {
            drawVoidSlotBox(guiGraphics, voidSlotX, voidSlotY);
        } else {
            guiGraphics.fill(voidSlotX, voidSlotY, voidSlotX + 18, voidSlotY + 18, 0xFF1A1A1A);
            guiGraphics.fill(voidSlotX + 1, voidSlotY + 1, voidSlotX + 17, voidSlotY + 17, 0xFF111111);
        }

        // Player Inventory slots
        int invStartX = x + 30;
        int invStartY = y + 180;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                drawSlotBox(guiGraphics, invStartX + col * 18 - 1, invStartY + row * 18 - 1);
            }
        }

        // Hotbar slots
        int hotbarStartY = y + 238;
        for (int col = 0; col < 9; ++col) {
            drawSlotBox(guiGraphics, invStartX + col * 18 - 1, hotbarStartY - 1);
        }
    }

    private void drawSlotBox(GuiGraphics guiGraphics, int sx, int sy) {
        guiGraphics.fill(sx, sy, sx + 18, sy + 18, 0xFF8B8B8B);
        guiGraphics.fill(sx + 1, sy + 1, sx + 16, sy + 16, 0xFF373737);
        guiGraphics.fill(sx + 1, sy + 1, sx + 17, sy + 2, 0xFF373737);
        guiGraphics.fill(sx + 1, sy + 1, sx + 2, sy + 17, 0xFF373737);
        guiGraphics.fill(sx + 1, sy + 1, sx + 17, sy + 17, 0xFF1A1A1A);
    }

    private void drawVoidSlotBox(GuiGraphics guiGraphics, int sx, int sy) {
        guiGraphics.fill(sx, sy, sx + 18, sy + 18, 0xFF5A189A); // Subtle purple highlight
        guiGraphics.fill(sx + 1, sy + 1, sx + 17, sy + 17, 0xFF8B8B8B);
        guiGraphics.fill(sx + 1, sy + 1, sx + 16, sy + 16, 0xFF373737);
        guiGraphics.fill(sx + 1, sy + 1, sx + 17, sy + 2, 0xFF373737);
        guiGraphics.fill(sx + 1, sy + 1, sx + 2, sy + 17, 0xFF373737);
        guiGraphics.fill(sx + 1, sy + 1, sx + 17, sy + 17, 0xFF1A1A1A);
    }

    private void drawButton(GuiGraphics guiGraphics, int bx, int by, int bw, int bh, String label, int textColor) {
        drawButtonWithCustomBg(guiGraphics, bx, by, bw, bh, label, textColor, 0xFF2C2C2C);
    }

    private void drawButtonWithCustomBg(GuiGraphics guiGraphics, int bx, int by, int bw, int bh, String label, int textColor, int bgColor) {
        guiGraphics.fill(bx, by, bx + bw, by + bh, 0xFF555555);
        guiGraphics.fill(bx + 1, by + 1, bx + bw - 1, by + bh - 1, bgColor);
        int textW = font.width(label);
        int textX = bx + (bw - textW) / 2;
        int textY = by + (bh - 8) / 2;
        guiGraphics.drawString(font, label, textX, textY, textColor, false);
    }

    private void renderCustomTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        // Partition Bar hover tooltip
        int barX = x + 12;
        int barY = y + 42;
        int barW = 196;
        int barH = 12;
        if (mouseX >= barX && mouseX <= barX + barW && mouseY >= barY && mouseY <= barY + barH) {
            if (!workingList.isEmpty()) {
                int currentX = barX;
                for (int i = 0; i < workingList.size(); i++) {
                    PartitionDraft p = workingList.get(i);
                    int segW = (int) Math.round((p.percent / 100.0) * barW);
                    if (i == workingList.size() - 1 && getTotalPercent() == 100) {
                        segW = (barX + barW) - currentX;
                    }
                    if (mouseX >= currentX && mouseX <= currentX + segW) {
                        String name = (p.target != null) ? new ItemStack(p.target).getHoverName().getString() : "Undefined";
                        List<Component> tooltip = new ArrayList<>();
                        tooltip.add(Component.literal(name).withStyle(ChatFormatting.GOLD));
                        tooltip.add(Component.literal("Allocation: " + p.percent + "%").withStyle(ChatFormatting.GRAY));
                        if (p.voidSecondary) {
                            tooltip.add(Component.literal("Voiding Secondary Output").withStyle(ChatFormatting.DARK_PURPLE));
                        }
                        guiGraphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
                        return;
                    }
                    currentX += segW;
                }
            }
        }

        // Action buttons hover tooltips
        int btnY = y + 137;
        if (mouseY >= btnY && mouseY <= btnY + 14) {
            if (mouseX >= x + 12 && mouseX <= x + 48) {
                guiGraphics.renderTooltip(font, Component.literal("Add a new partition to this cell"), mouseX, mouseY);
            } else if (mouseX >= x + 51 && mouseX <= x + 95) {
                guiGraphics.renderTooltip(font, Component.literal("Evenly distribute space across all active partitions"), mouseX, mouseY);
            } else if (mouseX >= x + 98 && mouseX <= x + 134) {
                guiGraphics.renderTooltip(font, Component.literal("Clear all partitions"), mouseX, mouseY);
            } else if (mouseX >= x + 138 && mouseX <= x + 208) {
                guiGraphics.renderTooltip(font, Component.literal("Apply partition layout to the storage cell"), mouseX, mouseY);
            }
        }

        // Table row tooltips
        int tableX = x + 12;
        int tableY = y + 58;
        int maxVisible = 3;
        for (int i = 0; i < maxVisible; i++) {
            int index = scrollOffset + i;
            if (index >= workingList.size()) break;
            PartitionDraft p = workingList.get(index);
            int rowY = tableY + 14 + i * 20;

            // Target box hover
            if (mouseX >= tableX + 10 && mouseX <= tableX + 27 && mouseY >= rowY && mouseY <= rowY + 17) {
                if (p.target != null) {
                    List<Component> tooltip = new ArrayList<>(getTooltipFromContainerItem(new ItemStack(p.target)));
                    tooltip.add(Component.literal("Drop a seed/sapling here or click to pick").withStyle(ChatFormatting.YELLOW));
                    guiGraphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
                } else {
                    guiGraphics.renderTooltip(font, Component.literal("Drop a seed/sapling here or click to pick from inventory"), mouseX, mouseY);
                }
            }
            // Void button hover
            if (mouseX >= tableX + 138 && mouseX <= tableX + 164 && mouseY >= rowY + 3 && mouseY <= rowY + 14) {
                if (!hasVoidCard()) {
                    guiGraphics.renderTooltip(font, Component.literal("Requires Void Secondary Card").withStyle(ChatFormatting.RED), mouseX, mouseY);
                } else {
                    guiGraphics.renderTooltip(font, Component.literal("Toggle voiding byproduct drops (apples, saplings, sticks, etc.)"), mouseX, mouseY);
                }
            }
        }

        // Empty Upgrade slot tooltips
        for (int i = 0; i < 4; i++) {
            int slotX = x + 51 + i * 18;
            int slotY = y + 154;
            if (mouseX >= slotX && mouseX <= slotX + 18 && mouseY >= slotY && mouseY <= slotY + 18) {
                if (!menu.getSlot(1 + i).hasItem()) {
                    guiGraphics.renderTooltip(font, Component.literal("Acceleration Card (" + (i + 1) + "/4)").withStyle(ChatFormatting.GRAY), mouseX, mouseY);
                }
            }
        }
        int voidSlotX = x + 137;
        int voidSlotY = y + 154;
        if (mouseX >= voidSlotX && mouseX <= voidSlotX + 18 && mouseY >= voidSlotY && mouseY <= voidSlotY + 18) {
            if (!menu.getSlot(5).hasItem()) {
                guiGraphics.renderTooltip(font, Component.literal("Void Secondary Card").withStyle(ChatFormatting.LIGHT_PURPLE), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = this.leftPos;
        int y = this.topPos;

        ItemStack cell = menu.getSlot(0).getItem();
        boolean hasCell = !cell.isEmpty() && cell.getItem() instanceof VirtualGardenCellItem;

        // Action Buttons Row
        int btnY = y + 137;
        if (mouseY >= btnY && mouseY <= btnY + 14) {
            // [+ Add]
            if (mouseX >= x + 12 && mouseX <= x + 48 && hasCell && workingList.size() < 6 && getUnallocatedPercent() > 0) {
                playClickSound();
                Item target = findFirstUnusedInventoryTarget();
                int pct = Math.min(20, Math.max(5, getUnallocatedPercent()));
                workingList.add(new PartitionDraft(target, pct, false));
                dirty = true;
                return true;
            }
            // [Equalize]
            if (mouseX >= x + 51 && mouseX <= x + 95 && hasCell && !workingList.isEmpty()) {
                playClickSound();
                int count = workingList.size();
                int share = 100 / count;
                int rem = 100 % count;
                for (int i = 0; i < count; i++) {
                    workingList.get(i).percent = share + (i < rem ? 1 : 0);
                }
                dirty = true;
                return true;
            }
            // [Clear]
            if (mouseX >= x + 98 && mouseX <= x + 134 && hasCell && !workingList.isEmpty()) {
                playClickSound();
                workingList.clear();
                selectedRowForPicker = -1;
                dirty = true;
                return true;
            }
            // [Apply & Format]
            if (mouseX >= x + 138 && mouseX <= x + 208 && hasCell) {
                playClickSound();
                applyPartitionsToServer();
                return true;
            }
        }

        // Table Rows interaction
        int tableX = x + 12;
        int tableY = y + 58;
        int maxVisible = 3;
        for (int i = 0; i < maxVisible; i++) {
            int index = scrollOffset + i;
            if (index >= workingList.size()) break;
            PartitionDraft p = workingList.get(index);
            int rowY = tableY + 14 + i * 20;

            // Target box click — supports drag-and-drop (cursor item) and picker mode
            if (mouseX >= tableX + 10 && mouseX <= tableX + 27 && mouseY >= rowY && mouseY <= rowY + 17) {
                // Check if player is carrying an item on cursor (drag-and-drop)
                ItemStack carried = menu.getCarried();
                if (!carried.isEmpty()) {
                    Item carriedItem = carried.getItem();
                    if (GardenDropRegistry.isValidSeed(carriedItem, minecraft != null ? minecraft.level : null)) {
                        playClickSound();
                        p.target = carriedItem;
                        selectedRowForPicker = -1;
                        dirty = true;
                        return true;
                    }
                }
                // Otherwise toggle picker mode (click inventory slot to select)
                playClickSound();
                if (selectedRowForPicker == index) {
                    selectedRowForPicker = -1;
                } else {
                    selectedRowForPicker = index;
                }
                return true;
            }

            // [-] button
            if (mouseX >= tableX + 88 && mouseX <= tableX + 99 && mouseY >= rowY + 3 && mouseY <= rowY + 14) {
                playClickSound();
                if (p.percent > 5) {
                    p.percent -= 5;
                    dirty = true;
                }
                return true;
            }

            // [+] button
            if (mouseX >= tableX + 122 && mouseX <= tableX + 133 && mouseY >= rowY + 3 && mouseY <= rowY + 14) {
                playClickSound();
                int unalloc = getUnallocatedPercent();
                if (unalloc > 0) {
                    int add = Math.min(5, unalloc);
                    p.percent += add;
                    dirty = true;
                }
                return true;
            }

            // [Void] toggle
            if (mouseX >= tableX + 138 && mouseX <= tableX + 164 && mouseY >= rowY + 3 && mouseY <= rowY + 14) {
                if (hasVoidCard()) {
                    playClickSound();
                    p.voidSecondary = !p.voidSecondary;
                    dirty = true;
                }
                return true;
            }

            // [X] delete
            if (mouseX >= tableX + 170 && mouseX <= tableX + 181 && mouseY >= rowY + 3 && mouseY <= rowY + 14) {
                playClickSound();
                workingList.remove(index);
                if (selectedRowForPicker == index) selectedRowForPicker = -1;
                dirty = true;
                return true;
            }
        }

        // Scroll buttons
        if (workingList.size() > maxVisible) {
            if (mouseX >= tableX + 185 && mouseX <= tableX + 194) {
                if (mouseY >= tableY + 14 && mouseY <= tableY + 23 && scrollOffset > 0) {
                    playClickSound();
                    scrollOffset--;
                    return true;
                }
                if (mouseY >= tableY + 64 && mouseY <= tableY + 73 && scrollOffset + maxVisible < workingList.size()) {
                    playClickSound();
                    scrollOffset++;
                    return true;
                }
            }
        }

        // If in picker mode and player clicks an inventory slot:
        Slot slot = getSlotUnderMouse();
        if (slot != null && slot.hasItem() && selectedRowForPicker >= 0 && selectedRowForPicker < workingList.size()) {
            Item item = slot.getItem().getItem();
            if (GardenDropRegistry.isValidSeed(item, minecraft != null ? minecraft.level : null)) {
                playClickSound();
                workingList.get(selectedRowForPicker).target = item;
                selectedRowForPicker = -1;
                dirty = true;
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY > 0 && scrollOffset > 0) {
            scrollOffset--;
            return true;
        } else if (scrollY < 0 && scrollOffset + 3 < workingList.size()) {
            scrollOffset++;
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private Item findFirstUnusedInventoryTarget() {
        if (minecraft != null && minecraft.player != null) {
            Inventory inv = minecraft.player.getInventory();
            for (int i = 0; i < inv.getContainerSize(); i++) {
                ItemStack stack = inv.getItem(i);
                if (!stack.isEmpty()) {
                    Item item = stack.getItem();
                    if (GardenDropRegistry.isValidSeed(item, minecraft.level)) {
                        boolean used = false;
                        for (PartitionDraft p : workingList) {
                            if (p.target == item) {
                                used = true;
                                break;
                            }
                        }
                        if (!used) return item;
                    }
                }
            }
        }
        return null;
    }

    private void applyPartitionsToServer() {
        List<GardenCellPartition> partitions = new ArrayList<>();
        for (PartitionDraft draft : workingList) {
            if (draft.target != null && draft.percent > 0) {
                partitions.add(new GardenCellPartition(draft.target, draft.percent, draft.voidSecondary));
            }
        }
        PacketDistributor.sendToServer(new SetPartitionsPayload(new GardenCellPartitionList(partitions)));
        dirty = false;
    }

    private void playClickSound() {
        if (minecraft != null) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }
}
