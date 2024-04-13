package com.github.tartaricacid.netmusic.item;

import com.github.tartaricacid.netmusic.client.renderer.MusicPlayerItemRenderer;
import com.github.tartaricacid.netmusic.init.InitBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.IItemRenderProperties;

import java.util.function.Consumer;

import static com.github.tartaricacid.netmusic.init.InitItems.TAB;

public class ItemMusicPlayer extends BlockItem {
    public ItemMusicPlayer() {
        super(InitBlocks.MUSIC_PLAYER.get(),
                (new Item.Properties()).tab(TAB));
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                Minecraft minecraft = Minecraft.getInstance();
                return new MusicPlayerItemRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
            }
        });
    }
}
