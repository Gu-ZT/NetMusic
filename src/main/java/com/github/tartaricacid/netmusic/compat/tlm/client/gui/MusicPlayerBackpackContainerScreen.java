package com.github.tartaricacid.netmusic.compat.tlm.client.gui;

import com.github.tartaricacid.netmusic.NetMusic;
import com.github.tartaricacid.netmusic.compat.tlm.inventory.MusicPlayerBackpackContainer;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.AbstractMaidContainerGui;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.maid.backpack.IBackpackContainerScreen;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MusicPlayerBackpackContainerScreen extends AbstractMaidContainerGui<MusicPlayerBackpackContainer> implements IBackpackContainerScreen {
    private static final ResourceLocation BACKPACK = new ResourceLocation(NetMusic.MOD_ID, "textures/gui/maid_music_player.png");
    private final EntityMaid maid;

    public MusicPlayerBackpackContainerScreen(MusicPlayerBackpackContainer container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
        this.imageHeight = 256;
        this.imageWidth = 256;
        this.maid = menu.getMaid();
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(Button.builder(Component.literal("<"), b -> clickButton(0))
                .size(15, 20).pos(this.leftPos + 142, this.topPos + 135)
                .tooltip(Tooltip.create(Component.translatable("gui.netmusic.maid.music_player_backpack.previous")))
                .build());
        this.addRenderableWidget(Button.builder(Component.literal(">"), b -> clickButton(1))
                .size(15, 20).pos(this.leftPos + 235, this.topPos + 135)
                .tooltip(Tooltip.create(Component.translatable("gui.netmusic.maid.music_player_backpack.next")))
                .build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.netmusic.maid.music_player_backpack.stop"), b -> this.clickButton(2))
                .size(36, 20).pos(this.leftPos + 159, this.topPos + 135).build());
        this.addRenderableWidget(Button.builder(Component.translatable("gui.netmusic.maid.music_player_backpack.play"), b -> this.clickButton(3))
                .size(36, 20).pos(this.leftPos + 197, this.topPos + 135).build());
    }

    private void clickButton(int id) {
        if (this.getMinecraft().gameMode != null) {
            this.getMinecraft().gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int x, int y) {
        super.renderBg(graphics, partialTicks, x, y);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BACKPACK);
        graphics.blit(BACKPACK, leftPos + 85, topPos + 36, 0, 0, 165, 128);
        int selectSlotId = this.menu.getSelectSlotId();
        int xIndex = selectSlotId % 6;
        int yIndex = selectSlotId / 6;
        graphics.blit(BACKPACK, leftPos + 142 + 18 * xIndex, topPos + 56 + 18 * yIndex, 165, 0, 18, 18);
    }
}