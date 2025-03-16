package io.github.mortuusars.scholar.client.gui.screen.view;

import com.mojang.datafixers.util.Pair;
import io.github.mortuusars.scholar.client.gui.screen.SpreadBookScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class SpreadBookViewScreen extends SpreadBookScreen {
    protected BookViewAccess bookAccess;
    protected Pair<List<FormattedCharSequence>, List<FormattedCharSequence>> cachedPageComponents;
    protected int cachedSpread;

    public SpreadBookViewScreen(BookViewAccess bookAccess, int bookColor) {
        super(bookColor);
        this.bookAccess = bookAccess;
        this.cachedPageComponents = Pair.of(Collections.emptyList(), Collections.emptyList());
        this.cachedSpread = -1;
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

    public boolean setPage(int pageIndex) {
        pageIndex = Mth.clamp(pageIndex, 0, getBookAccess().getPageCount() - 1);
        int spreadIndex = (int)(pageIndex / 2f);
        if (spreadIndex != this.currentSpread) {
            this.currentSpread = spreadIndex;
            this.cachedSpread = -1;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        updateButtonVisibility();

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

        int x = (int)mouseX - (leftPos + (isOverRightPage ? TEXT_RIGHT_X : TEXT_LEFT_X));
        int y = (int)mouseY - (topPos + TEXT_Y);

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
}