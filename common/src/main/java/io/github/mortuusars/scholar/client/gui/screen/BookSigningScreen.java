package io.github.mortuusars.scholar.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.book.BookSignature;
import io.github.mortuusars.scholar.client.gui.Widgets;
import io.github.mortuusars.scholar.client.gui.widget.textbox.display.HorizontalAlignment;
import io.github.mortuusars.scholar.client.gui.widget.textbox.text.FormattedString;
import io.github.mortuusars.scholar.client.gui.widget.textbox.TextBox;
import io.github.mortuusars.scholar.client.util.RenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class BookSigningScreen extends Screen {
    public static final ResourceLocation TEXTURE = Scholar.resource("textures/gui/book_signing.png");

    @NotNull
    protected final Minecraft minecraft;
    @NotNull
    protected final Player player;

    protected final Screen parentScreen;
    protected final int bookColor;
    protected final Consumer<BookSignature> onSign;

    protected int textColor;
    protected int selectionColor;
    protected int selectionUnfocusedColor;
    protected int enterBookTitleFontColor;
    protected int byAuthorFontColor;

    protected int imageWidth, imageHeight, leftPos, topPos, textureWidth, textureHeight;

    protected TextBox titleTextBox;
    protected TextBox authorTextBox;
    protected ImageButton signButton;
    protected ImageButton cancelSigningButton;

    protected String titleText = "";
    protected String authorText = Objects.requireNonNull(Minecraft.getInstance().player).getScoreboardName();

    public BookSigningScreen(Screen parentScreen, int bookColor, Consumer<BookSignature> onSign) {
        super(Component.empty());
        this.parentScreen = parentScreen;
        this.bookColor = bookColor;
        this.onSign = onSign;

        this.textColor = Config.Client.getColor(Config.Client.TEXT_COLOR);
        this.selectionColor = Config.Client.getColor(Config.Client.SELECTION_COLOR);
        this.selectionUnfocusedColor = Config.Client.getColor(Config.Client.SELECTION_UNFOCUSED_COLOR);
        this.enterBookTitleFontColor = Config.Client.getColor(Config.Client.ENTER_TITLE_COLOR);
        this.byAuthorFontColor = Config.Client.getColor(Config.Client.BY_AUTHOR_COLOR);

        this.minecraft = Minecraft.getInstance();
        this.player = Objects.requireNonNull(minecraft.player);
        this.textureWidth = 256;
        this.textureHeight = 256;
    }

    @Override
    public boolean isPauseScreen() {
        return Config.Client.SCREEN_PAUSE.get();
    }

    @Override
    protected void init() {
        this.imageWidth = 149;
        this.imageHeight = 180;
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        // TITLE
        titleTextBox = new TextBox(font, leftPos + 21, topPos + 71, 108, 9)
              .setFontColor(textColor)
              .setFontUnfocusedColor(textColor)
              .setSelectionColor(selectionColor)
              .setSelectionUnfocusedColor(selectionUnfocusedColor)
              .setHorizontalAlignment(HorizontalAlignment.CENTER)
              .setOnTextChanged(this::setTitleText)
              .setTextValidator(text -> text != null && text.length() <= WrittenBookContent.TITLE_MAX_LENGTH
                    && font.wordWrapHeight(text, 108) <= 9 && !text.contains("\n"));
        addRenderableWidget(titleTextBox);

        authorTextBox = new TextBox(font, leftPos + 33, topPos + 81, 100, 9)
              .setText(FormattedString.parse(authorText))
              .setFontColor(byAuthorFontColor)
              .setFontUnfocusedColor(byAuthorFontColor)
              .setSelectionColor(0xFF9271dc)
              .setSelectionUnfocusedColor(0xFFc6b2f2)
              .setHorizontalAlignment(HorizontalAlignment.CENTER)
              .setOnTextChanged(this::setAuthorText)
              .setTextValidator(text -> text != null && text.length() <= WrittenBookContent.TITLE_MAX_LENGTH
                    && font.wordWrapHeight(text, 100) <= 9 && !text.contains("\n"));
        authorTextBox.getEditor().setCursorToEnd(false);
        addRenderableWidget(authorTextBox);

        // SIGN
        signButton = new ImageButton(leftPos + 46, topPos + 108, 22, 22, 149, 0,
                22, TEXTURE, textureWidth, textureHeight,
                b -> sign(), Component.translatable("book.finalizeButton"));
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

    protected void setTitleText(FormattedString text) {
        titleText = text.toString();
        updateButtons();
    }

    protected void setAuthorText(FormattedString text) {
        authorText = text.toString();
        updateButtons();
    }

    protected void updateButtons() {
        signButton.active = canSign();
        authorTextBox.visible = Config.Common.BOOK_CHANGEABLE_AUTHOR.get();
        authorTextBox.active = Config.Common.BOOK_CHANGEABLE_AUTHOR.get();

        boolean canSign = canSign();

        signButton.active = canSign;

        MutableComponent component = Component.translatable("book.finalizeButton")
              .append(CommonComponents.NEW_LINE)
              .append(Component.translatable("book.finalizeWarning").withStyle(ChatFormatting.GRAY));
        if (!canSign && titleText.length() > WrittenBookContent.TITLE_MAX_LENGTH) {
            component.append(CommonComponents.NEW_LINE);
            component.append(Component.translatable("scholar.book_singing.error_title_too_long").withStyle(ChatFormatting.RED));
        }

        signButton.setTooltip(Tooltip.create(component));
    }

    protected boolean canSign() {
        return !titleText.isBlank();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        updateButtons();

        renderBackground(guiGraphics);

        RenderUtil.withColorMultiplied(bookColor, () -> {
            guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, 0,
                  imageWidth, imageHeight, textureWidth, textureHeight);
        });

        guiGraphics.blit(TEXTURE, leftPos, topPos + 31, 0, 0, 180,
              imageWidth, 76, textureWidth, textureHeight);

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        renderLabels(guiGraphics);
    }

    protected void renderLabels(GuiGraphics guiGraphics) {
        MutableComponent component = Component.translatable("book.editTitle");
        guiGraphics.drawString(font, component, leftPos + 149 / 2 - font.width(component) / 2, topPos + 51,
              enterBookTitleFontColor, false);

        if (!Config.Common.BOOK_CHANGEABLE_AUTHOR.get()) {
            component = Component.translatable("book.byAuthor", player.getName());
            guiGraphics.drawString(font, component, leftPos + 149 / 2 - font.width(component) / 2, topPos + 81,
                  byAuthorFontColor, false);
        } else {
            component = Component.empty()
                  .append(authorText.equals(Objects.requireNonNull(minecraft.player).getScoreboardName()) ? "✎ " : "")
                  .append(Component.translatable("book.byAuthor", ""));
            int width = font.width(component);
            int authorWidth = font.width(authorTextBox.getEditor().getString().toString());
            int x = (authorTextBox.getX() + authorTextBox.getWidth() / 2) - (authorWidth / 2) - width;
            guiGraphics.drawString(font, component, x, topPos + 81,
                  byAuthorFontColor, false);
        }
    }

    protected void sign() {
        /*if (canSign()) {
            onSign.accept(titleText.trim());
            minecraft.getSoundManager().play(
                  SimpleSoundInstance.forUI(Scholar.SoundEvents.BOOK_SIGNED.get(), 1f, 0.8f));
            onClose();
        }*/
        if (canSign()) {
            Optional<String> author = Optional.of(authorText)
                  .filter(a -> Config.Common.BOOK_CHANGEABLE_AUTHOR.get()
                        && !a.equals(Objects.requireNonNull(minecraft.player).getScoreboardName()));
            onSign.accept(new BookSignature(titleText.trim(), author));
            minecraft.getSoundManager().play(
                  SimpleSoundInstance.forUI(Scholar.SoundEvents.BOOK_SIGNED.get(), 1f, 0.8f));
            onClose();
        }
    }

    protected void cancelSigning() {
        minecraft.setScreen(parentScreen);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputConstants.KEY_ESCAPE) {
            cancelSigning();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
