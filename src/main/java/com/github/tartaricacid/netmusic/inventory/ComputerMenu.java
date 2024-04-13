package com.github.tartaricacid.netmusic.inventory;

import com.github.tartaricacid.netmusic.init.InitItems;
import com.github.tartaricacid.netmusic.item.ItemMusicCD;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class ComputerMenu extends Container {
    public static final ContainerType<ComputerMenu> TYPE = IForgeContainerType.create((windowId, inv, data) -> new ComputerMenu(windowId, inv));
    private final ItemStackHandler input = new ItemStackHandler() {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return stack.getItem() == InitItems.MUSIC_CD.get();
        }
    };
    private final ItemStackHandler output = new ItemStackHandler() {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return false;
        }

        @Override
        protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
            return 1;
        }
    };
    private ItemMusicCD.SongInfo songInfo;

    public ComputerMenu(int id, PlayerInventory inventory) {
        super(TYPE, id);

        this.addSlot(new SlotItemHandler(input, 0, 147, 14));
        this.addSlot(new SlotItemHandler(output, 0, 147, 79));

        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 192));
        }

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 134 + i * 18));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotItem = slot.getItem();
            itemStack = slotItem.copy();
            if (index < 2) {
                if (!this.moveItemStackTo(slotItem, 2, this.slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotItem, 0, 2, true)) {
                return ItemStack.EMPTY;
            }

            if (slotItem.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    @Override
    public void removed(PlayerEntity player) {
        super.removed(player);
        ItemHandlerHelper.giveItemToPlayer(player, input.getStackInSlot(0));
        ItemHandlerHelper.giveItemToPlayer(player, output.getStackInSlot(0));
    }

    public void setSongInfo(ItemMusicCD.SongInfo setSongInfo) {
        this.songInfo = setSongInfo;
        if (!this.input.getStackInSlot(0).isEmpty() && this.output.getStackInSlot(0).isEmpty()) {
            ItemStack itemStack = this.input.extractItem(0, 1, false);
            ItemMusicCD.SongInfo rawSongInfo = ItemMusicCD.getSongInfo(itemStack);
            if (rawSongInfo == null || !rawSongInfo.readOnly) {
                ItemMusicCD.setSongInfo(this.songInfo, itemStack);
            }
            this.output.setStackInSlot(0, itemStack);
        }
    }

    public ItemStackHandler getInput() {
        return input;
    }
}
