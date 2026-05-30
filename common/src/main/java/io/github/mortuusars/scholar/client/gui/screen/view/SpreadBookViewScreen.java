package io.github.mortuusars.scholar.client.gui.screen.view;

import com.mojang.datafixers.util.Pair;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.ScholarClient;
import io.github.mortuusars.scholar.client.gui.screen.SpreadBookScreen;
import io.github.mortuusars.scholar.client.gui.screen.edit.SpreadBookEditScreen;
import io.github.mortuusars.scholar.client.util.FileDialogs;
import io.github.mortuusars.scholar.client.util.RenderUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.*;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

public abstract class SpreadBookViewScreen extends SpreadBookScreen {
    protected BookViewAccess bookAccess;
    protected Pair<List<FormattedCharSequence>, List<FormattedCharSequence>> cachedPageComponents;
    protected int cachedSpread;

    protected ImageButton exportBookButton;

    public SpreadBookViewScreen(BookViewAccess bookAccess, int bookColor) {
        super(bookColor);
        this.bookAccess = bookAccess;
        this.cachedPageComponents = Pair.of(Collections.emptyList(), Collections.emptyList());
        this.cachedSpread = -1;
    }

    @Override
    public boolean isGolden() {
        return getBookAccess().isGolden();
    }

    @Override
    protected void createWidgets() {
        super.createWidgets();
        exportBookButton = new ImageButton(leftPos + 297, topPos + 16, 18, 18, SpreadBookEditScreen.EXPORT_BOOK_SPRITES,
              b -> exportBook(!Screen.hasShiftDown()), Component.translatable("gui.scholar.export_book"));
        exportBookButton.setTooltip(Tooltip.create(Component.translatable("gui.scholar.export_book")
              .append(ScholarClient.KeyMappings.componentForTooltip(ScholarClient.KeyMappings.exportBook))
              .append(CommonComponents.NEW_LINE)
              .append(Component.translatable("gui.scholar.export_book.tooltip"))));
        addRenderableWidget(exportBookButton);
    }

    @Override
    protected void updateButtons() {
        super.updateButtons();
        exportBookButton.visible = isToolsVisible();
        exportBookButton.active = IntStream.range(0, getBookAccess().getPageCount())
              .mapToObj(getBookAccess()::getPage)
              .anyMatch(text -> !text.getString().isEmpty());
    }

    public BookViewAccess getBookAccess() {
        return bookAccess;
    }

    public void setBookAccess(BookViewAccess bookViewAccess) {
        this.bookAccess = bookViewAccess;
        this.currentSpread = Mth.clamp(this.currentSpread, 0, getSpreadCount());
        this.cachedSpread = -1; // Forces cache update
    }

    @Override
    public int getPageCount() {
        return getBookAccess().getPageCount();
    }

    @Override
    public int getLastPage() {
        return Math.max(getPageCount() - 1, 0);
    }

    @Override
    protected int getFirstPageWithContent() {
        for (int i = 0; i < getPageCount(); i++) {
            if (!getBookAccess().getPage(i).getString().isEmpty()) {
                return i;
            }
        }
        return 0;
    }

    @Override
    protected int getLastPageWithContent() {
        for (int i = getPageCount() - 1; i >= 0; i--) {
            if (!getBookAccess().getPage(i).getString().isEmpty()) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public boolean setPage(int pageIndex) {
        if (super.setPage(pageIndex)) {
            this.cachedSpread = -1;
            return true;
        }
        return false;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        updateButtons();

        renderTransparentBackground(guiGraphics);
        renderBook(guiGraphics, mouseX, mouseY, partialTick);
        renderPageNumbers(guiGraphics, mouseX, mouseY, partialTick, currentSpread);
        renderTools(guiGraphics, mouseX, mouseY, partialTick);

        updateAndCacheContentsIfNeeded();

        renderPageContents(guiGraphics, cachedPageComponents.getFirst(), leftPos + TEXT_LEFT_X, topPos + TEXT_Y);
        renderPageContents(guiGraphics, cachedPageComponents.getSecond(), leftPos + TEXT_RIGHT_X, topPos + TEXT_Y);

        Style style = getClickedComponentStyleAt(mouseX, mouseY);
        if (style != null) {
            guiGraphics.renderComponentHoverEffect(font, style, mouseX, mouseY);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Stops blur from rendering
    }

    @Override
    protected void renderBook(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBook(guiGraphics, mouseX, mouseY, partialTick);
        if (isToolsVisible()) {
            RenderUtil.withColorMultiplied(bookColor, () -> {
                // Export button BG
                guiGraphics.blit(TEXTURE, leftPos + 295, topPos + 14, 0, 388, 23, 24, 512, 512);
            });
        }
    }

    protected void updateAndCacheContentsIfNeeded() {
        if (cachedSpread != currentSpread) {
            FormattedText leftFormattedText = getBookAccess().getPage(currentSpread * 2);
            FormattedText rightFormattedText = getBookAccess().getPageCount() > currentSpread * 2 + 1 ?
                  getBookAccess().getPage(currentSpread * 2 + 1) : FormattedText.EMPTY;

            cachedPageComponents = Pair.of(
                  font.split(leftFormattedText, TEXT_WIDTH),
                  font.split(rightFormattedText, TEXT_WIDTH));

            cachedSpread = currentSpread;
        }
    }

    protected void renderPageContents(GuiGraphics guiGraphics, List<FormattedCharSequence> lines, int x, int y) {
        int maxLines = Math.min(TEXT_HEIGHT / font.lineHeight, lines.size());
        for (int i = 0; i < maxLines; ++i) {
            FormattedCharSequence text = lines.get(i);
            guiGraphics.drawString(font, text, x, y + i * font.lineHeight, textColor, false);
        }
    }

    // --

    public boolean mouseClicked(double x, double y, int button) {
        if (button == 0) {
            Style style = this.getClickedComponentStyleAt(x, y);
            if (style != null && this.handleComponentClicked(style)) {
                return true;
            }
        }

        return super.mouseClicked(x, y, button);
    }

    public boolean handleComponentClicked(Style style) {
        if (style == null)
            return false;

        ClickEvent clickEvent = style.getClickEvent();
        if (clickEvent == null)
            return false;

        if (clickEvent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
            String pageNumber = clickEvent.getValue();

            try {
                int pageIndex = Integer.parseInt(pageNumber) - 1;
                boolean pageChanged = this.setPage(pageIndex);
                if (pageChanged) {
                    playPageTurnSound();
                }
                return pageChanged;
            } catch (Exception var5) {
                return false;
            }
        } else {
            boolean handled = super.handleComponentClicked(style);
            if (handled && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                this.onClose();
            }

            return handled;
        }
    }

    @Nullable
    public Style getClickedComponentStyleAt(double mouseX, double mouseY) {
        if (mouseY < topPos + TEXT_Y || mouseY >= topPos + TEXT_Y + TEXT_HEIGHT)
            return null;

        boolean isOverRightPage;
        if (mouseX >= leftPos + TEXT_RIGHT_X && mouseX < leftPos + TEXT_RIGHT_X + TEXT_WIDTH) {
            isOverRightPage = true;
        } else if (mouseX >= leftPos + TEXT_LEFT_X && mouseX < leftPos + TEXT_LEFT_X + TEXT_WIDTH) {
            isOverRightPage = false;
        } else {
            return null;
        }

        List<FormattedCharSequence> pageContents = isOverRightPage ? this.cachedPageComponents.getSecond() : this.cachedPageComponents.getFirst();

        if (pageContents.isEmpty()) {
            return null;
        }

        int x = (int) mouseX - (leftPos + (isOverRightPage ? TEXT_RIGHT_X : TEXT_LEFT_X));
        int y = (int) mouseY - (topPos + TEXT_Y);

        int linesCount = Math.min(TEXT_HEIGHT / font.lineHeight, pageContents.size());
        if (y < font.lineHeight * linesCount + linesCount) {
            int clickedLine = y / font.lineHeight;
            if (clickedLine >= 0 && clickedLine < pageContents.size()) {
                FormattedCharSequence text = pageContents.get(clickedLine);
                return font.getSplitter().componentStyleAtWidth(text, x);
            }

            return null;
        }

        return null;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (ScholarClient.KeyMappings.exportBook.matches(keyCode, scanCode)) {
            playButtonClickSound();
            exportBook(!Screen.hasShiftDown());
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // --

    public void exportBook(boolean withFormatting) {
        List<String> pages = new ArrayList<>();
        for (int i = 0; i < getBookAccess().getPageCount(); i++) {
            String text = getBookAccess().getPage(i).getString();
            pages.add(text);
        }

        String content = withFormatting
              ? String.join("\f", pages)
              : ChatFormatting.stripFormatting(String.join("\f", pages));

        CompletableFuture.runAsync(() -> {
            String defaultDirectory = Minecraft.getInstance().gameDirectory.toPath().toAbsolutePath().normalize().toString();
            if (!defaultDirectory.endsWith(File.separator)) {
                defaultDirectory = defaultDirectory + File.separator;
            }
            String title = Component.translatable("gui.scholar.export_book").getString();

            FileDialogs.saveFile(defaultDirectory, title, "Text Files (.txt)", "*.txt").ifPresent(filePath -> {
                try {
                    Files.writeString(Path.of(filePath), content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                    MutableComponent filePathComponent = Component.literal(filePath).withStyle(Style.EMPTY
                          .withUnderlined(true)
                          .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, filePath)));
                    Minecraft.getInstance().execute(() -> player.displayClientMessage(
                          Component.translatable("gui.scholar.export_book.success")
                                .append(filePathComponent), false));
                } catch (IOException e) {
                    Minecraft.getInstance().execute(() -> player.displayClientMessage(
                          Component.translatable("gui.scholar.export_book.failure"), false));
                    Scholar.LOGGER.error("Failed to export book: ", e);
                }
            });
        }).exceptionally(e -> {
            Minecraft.getInstance().execute(() -> player.displayClientMessage(
                  Component.translatable("gui.scholar.export_book.failure"), false));
            Scholar.LOGGER.error("Failed to export book: ", e);
            return null;
        });
    }
}