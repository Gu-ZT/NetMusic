package com.github.tartaricacid.netmusic;

import com.github.tartaricacid.netmusic.init.InitItems;
import com.github.tartaricacid.netmusic.tileentity.TileEntityMusicPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class MusicPlayerInv extends ItemStackHandler {
    private final TileEntityMusicPlayer te;

    public MusicPlayerInv(TileEntityMusicPlayer te) {
        super();
        this.te = te;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return stack.getItem() == InitItems.MUSIC_CD.get();
    }

    @Override
    protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
        return 1;
    }

    @Override
    protected void onContentsChanged(int slot) {
        ItemStack stackInSlot = getStackInSlot(slot);
        if (stackInSlot.isEmpty()) {
            te.setPlay(false);
            te.setCurrentTime(0);
        }
        te.markDirty();
    }
}