package com.github.tartaricacid.netmusic.client.gui;

import com.github.tartaricacid.netmusic.NetMusic;
import com.github.tartaricacid.netmusic.inventory.ComputerMenu;
import com.github.tartaricacid.netmusic.item.ItemMusicCD;
import com.github.tartaricacid.netmusic.network.NetworkHandler;
import com.github.tartaricacid.netmusic.network.message.SetMusicIDMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class ComputerMenuScreen extends AbstractContainerScreen<ComputerMenu> {
    private static final ResourceLocation BG = new ResourceLocation(NetMusic.MOD_ID, "textures/gui/computer.png");
    private static final Pattern URL_HTTP_REG = Pattern.compile("(http|ftp|https)://[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-.,@?^=%&:/~+#]*[\\w\\-@?^=%&/~+#])?");
    private static final Pattern URL_FILE_REG = Pattern.compile("^[a-zA-Z]:\\\\(?:[^\\\\/:*?\"<>|\\r\\n]+\\\\)*[^\\\\/:*?\"<>|\\r\\n]*$");
    private static final Pattern TIME_REG = Pattern.compile("^\\d+$");
    private EditBox urlTextField;
    private EditBox nameTextField;
    private EditBox timeTextField;
    private Checkbox readOnlyButton;
    private Component tips = Component.empty();

    public ComputerMenuScreen(ComputerMenu screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.imageHeight = 206;
    }

    @Override
    protected void init() {
        super.init();
        this.initUrlEditBox();
        this.initNameEditBox();
        this.initTimeEditBox();
        this.readOnlyButton = new Checkbox(leftPos + 58, topPos + 55, 80, 20,
                Component.translatable("gui.netmusic.cd_burner.read_only"), false);
        this.addRenderableWidget(this.readOnlyButton);
        this.addRenderableWidget(Button.builder(Component.translatable("gui.netmusic.cd_burner.craft"), (b) -> handleCraftButton())
                .pos(leftPos + 7, topPos + 78).size(135, 18).build());
    }

    private void initUrlEditBox() {
        String perText = "";
        boolean focus = false;
        if (urlTextField != null) {
            perText = urlTextField.getValue();
            focus = urlTextField.isFocused();
        }
        urlTextField = new EditBox(getMinecraft().font, leftPos + 10, topPos + 18, 120, 16, Component.literal("Music URL Box"));
        urlTextField.setValue(perText);
        urlTextField.setBordered(false);
        urlTextField.setMaxLength(256);
        urlTextField.setTextColor(0xF3EFE0);
        urlTextField.setFocused(focus);
        urlTextField.moveCursorToEnd();
        this.addWidget(this.urlTextField);
    }

    private void initNameEditBox() {
        String perText = "";
        boolean focus = false;
        if (nameTextField != null) {
            perText = nameTextField.getValue();
            focus = nameTextField.isFocused();
        }
        nameTextField = new EditBox(getMinecraft().font, leftPos + 10, topPos + 39, 120, 16, Component.literal("Music Name Box"));
        nameTextField.setValue(perText);
        nameTextField.setBordered(false);
        nameTextField.setMaxLength(64);
        nameTextField.setTextColor(0xF3EFE0);
        nameTextField.setFocused(focus);
        nameTextField.moveCursorToEnd();
        this.addWidget(this.nameTextField);
    }

    private void initTimeEditBox() {
        String perText = "";
        boolean focus = false;
        if (timeTextField != null) {
            perText = timeTextField.getValue();
            focus = timeTextField.isFocused();
        }
        timeTextField = new EditBox(getMinecraft().font, leftPos + 10, topPos + 61, 40, 16, Component.literal("Music Time Box"));
        timeTextField.setValue(perText);
        timeTextField.setBordered(false);
        timeTextField.setMaxLength(5);
        timeTextField.setTextColor(0xF3EFE0);
        timeTextField.setFocused(focus);
        timeTextField.moveCursorToEnd();
        this.addWidget(this.timeTextField);
    }

    private void handleCraftButton() {
        ItemStack cd = this.getMenu().getInput().getStackInSlot(0);
        if (cd.isEmpty()) {
            this.tips = Component.translatable("gui.netmusic.cd_burner.cd_is_empty");
            return;
        }
        ItemMusicCD.SongInfo songInfo = ItemMusicCD.getSongInfo(cd);
        if (songInfo != null && songInfo.readOnly) {
            this.tips = Component.translatable("gui.netmusic.cd_burner.cd_read_only");
            return;
        }
        String urlText = urlTextField.getValue();
        if (Util.isBlank(urlText)) {
            this.tips = Component.translatable("gui.netmusic.computer.url.empty");
            return;
        }
        String nameText = nameTextField.getValue();
        if (Util.isBlank(nameText)) {
            this.tips = Component.translatable("gui.netmusic.computer.name.empty");
            return;
        }
        String timeText = timeTextField.getValue();
        if (Util.isBlank(timeText)) {
            this.tips = Component.translatable("gui.netmusic.computer.time.empty");
            return;
        }
        if (!TIME_REG.matcher(timeText).matches()) {
            this.tips = Component.translatable("gui.netmusic.computer.time.not_number");
            return;
        }
        int time = Integer.parseInt(timeText);
        if (URL_HTTP_REG.matcher(urlText).matches()) {
            ItemMusicCD.SongInfo song = new ItemMusicCD.SongInfo(urlText, nameText, time, this.readOnlyButton.selected());
            NetworkHandler.CHANNEL.sendToServer(new SetMusicIDMessage(song));
            return;
        }
        if (URL_FILE_REG.matcher(urlText).matches()) {
            File file = Paths.get(urlText).toFile();
            if (!file.isFile()) {
                this.tips = Component.translatable("gui.netmusic.computer.url.local_file_error");
                return;
            }
            try {
                URL url = file.toURI().toURL();
                ItemMusicCD.SongInfo song = new ItemMusicCD.SongInfo(url.toString(), nameText, time, this.readOnlyButton.selected());
                NetworkHandler.CHANNEL.sendToServer(new SetMusicIDMessage(song));
                return;
            } catch (MalformedURLException e) {
                e.fillInStackTrace();
            }
        }
        this.tips = Component.translatable("gui.netmusic.computer.url.error");
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int x, int y) {
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int x, int y) {
        renderBackground(graphics);
        int posX = this.leftPos;
        int posY = (this.height - this.imageHeight) / 2;
        graphics.blit(BG, posX, posY, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, float partialTicks) {
        super.render(graphics, x, y, partialTicks);
        urlTextField.render(graphics, x, y, partialTicks);
        nameTextField.render(graphics, x, y, partialTicks);
        timeTextField.render(graphics, x, y, partialTicks);
        if (Util.isBlank(urlTextField.getValue()) && !urlTextField.isFocused()) {
            graphics.drawString(font, Component.translatable("gui.netmusic.computer.url.tips").withStyle(ChatFormatting.ITALIC), this.leftPos + 12, this.topPos + 18, ChatFormatting.GRAY.getColor(), false);
        }
        if (Util.isBlank(nameTextField.getValue()) && !nameTextField.isFocused()) {
            graphics.drawString(font, Component.translatable("gui.netmusic.computer.name.tips").withStyle(ChatFormatting.ITALIC), this.leftPos + 12, this.topPos + 39, ChatFormatting.GRAY.getColor(), false);
        }
        if (Util.isBlank(timeTextField.getValue()) && !timeTextField.isFocused()) {
            graphics.drawString(font, Component.translatable("gui.netmusic.computer.time.tips").withStyle(ChatFormatting.ITALIC), this.leftPos + 11, this.topPos + 61, ChatFormatting.GRAY.getColor(), false);
        }
        graphics.drawWordWrap(font, tips, this.leftPos + 8, this.topPos + 100, 162, 0xCF0000);
        renderTooltip(graphics, x, y);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String urlValue = this.urlTextField.getValue();
        String nameValue = this.nameTextField.getValue();
        String timeValue = this.timeTextField.getValue();
        super.resize(minecraft, width, height);
        this.urlTextField.setValue(urlValue);
        this.nameTextField.setValue(nameValue);
        this.timeTextField.setValue(timeValue);
    }

    @Override
    protected void containerTick() {
        this.urlTextField.tick();
        this.nameTextField.tick();
        this.timeTextField.tick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.urlTextField.mouseClicked(mouseX, mouseY, button)) {
            this.setFocused(this.urlTextField);
            return true;
        }
        if (this.nameTextField.mouseClicked(mouseX, mouseY, button)) {
            this.setFocused(this.nameTextField);
            return true;
        }
        if (this.timeTextField.mouseClicked(mouseX, mouseY, button)) {
            this.setFocused(this.timeTextField);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void insertText(String text, boolean overwrite) {
        if (overwrite) {
            this.urlTextField.setValue(text);
            this.nameTextField.setValue(text);
            this.timeTextField.setValue(text);
        } else {
            this.urlTextField.insertText(text);
            this.nameTextField.insertText(text);
            this.timeTextField.insertText(text);
        }
    }
}
