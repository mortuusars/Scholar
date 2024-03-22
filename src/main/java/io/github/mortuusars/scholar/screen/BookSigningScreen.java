package io.github.mortuusars.scholar.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.screen.textbox.HorizontalAlignment;
import io.github.mortuusars.scholar.screen.textbox.TextBox;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BookSigningScreen extends Screen {
    public static final int SELECTION_COLOR = 0xFF8888FF;
    public static final int SELECTION_UNFOCUSED_COLOR = 0xFFBBBBFF;
    public static final ResourceLocation TEXTURE = Scholar.resource("textures/gui/book_signing.png");

    @NotNull
    protected final Minecraft minecraft;
    @NotNull
    protected final Player player;

    protected final SpreadBookEditScreen parentScreen;

    protected int imageWidth, imageHeight, leftPos, topPos, textureWidth, textureHeight;

    protected TextBox titleTextBox;
    protected ImageButton signButton;
    protected ImageButton cancelSigningButton;

    protected String titleText = "";

    public BookSigningScreen(SpreadBookEditScreen parentScreen) {
        super(Component.empty());
        this.parentScreen = parentScreen;

        minecraft = Minecraft.getInstance();
        player = Objects.requireNonNull(minecraft.player);
        textureWidth = 256;
        textureHeight = 256;
    }

    @Override
    protected void init() {
        this.imageWidth = 149;
        this.imageHeight = 180;
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        // TITLE
        titleTextBox = new TextBox(font, leftPos + 21, topPos + 71, 108, 9,
                () -> titleText, text -> titleText = text)
                .setFontColor(0xFF856036, 0xFF856036)
                .setSelectionColor(SELECTION_COLOR, SELECTION_UNFOCUSED_COLOR);
        titleTextBox.textValidator = text -> text != null && font.wordWrapHeight(text, 108) <= 9 && !text.contains("\n");
        titleTextBox.horizontalAlignment = HorizontalAlignment.CENTER;
        addRenderableWidget(titleTextBox);

        // SIGN
        signButton = new ImageButton(leftPos + 46, topPos + 108, 22, 22, 149, 0,
                22, TEXTURE, textureWidth, textureHeight,
                b -> signAlbum(), Component.translatable("book.finalizeButton"));
        MutableComponent component = Component.translatable("book.finalizeButton")
                .append("\n").append(Component.translatable("book.finalizeWarning").withStyle(ChatFormatting.GRAY));
        signButton.setTooltip(Tooltip.create(component));
        addRenderableWidget(signButton);

        // CANCEL
        cancelSigningButton = new ImageButton(leftPos + 83, topPos + 108, 22, 22, 171, 0,
                22, TEXTURE, textureWidth, textureHeight,
                b -> cancelSigning(), CommonComponents.GUI_CANCEL);
        cancelSigningButton.setTooltip(Tooltip.create(CommonComponents.GUI_CANCEL));
        addRenderableWidget(cancelSigningButton);

        setInitialFocus(titleTextBox);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        titleTextBox.tick();
    }

    private void updateButtons() {
        signButton.active = canSign();
    }

    protected boolean canSign() {
        return !titleText.isBlank();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        updateButtons();

        renderBackground(guiGraphics);

        RenderSystem.enableBlend();

        int col = 0xFF99422b;
        int alpha = col >> 24 & 0xFF;
        int red = col >> 16 & 0xFF;
        int green = col >> 8 & 0xFF;
        int blue = col & 0xFF;

        RenderSystem.setShaderColor(red / 255f, green / 255f, blue / 255f, alpha / 255f);
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, 0,
                imageWidth, imageHeight, textureWidth, textureHeight);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        guiGraphics.blit(TEXTURE, leftPos, topPos + 31, 0, 0, 180,
                imageWidth, 76, textureWidth, textureHeight);

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        renderLabels(guiGraphics);
    }

    private void renderLabels(GuiGraphics guiGraphics) {
        MutableComponent component = Component.translatable("book.editTitle");
        guiGraphics.drawString(font, component,  leftPos + 149 / 2 - font.width(component) / 2, topPos + 51, 0xf5ebd0, false);

        component = Component.translatable("book.byAuthor", player.getName());
        guiGraphics.drawString(font, component, leftPos + 149 / 2 - font.width(component) / 2, topPos + 81, 0xc7b496, false);
    }

    protected void signAlbum() {
        if (canSign()) {
            parentScreen.saveChanges(true, titleText.trim());
            this.onClose();
        }
    }

    protected void cancelSigning() {
        minecraft.setScreen(parentScreen);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputConstants.KEY_TAB)
            return super.keyPressed(keyCode, scanCode, modifiers);

        if (keyCode == InputConstants.KEY_ESCAPE) {
            cancelSigning();
            return true;
        }

        if (titleTextBox.isFocused())
            return titleTextBox.keyPressed(keyCode, scanCode, modifiers);

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
