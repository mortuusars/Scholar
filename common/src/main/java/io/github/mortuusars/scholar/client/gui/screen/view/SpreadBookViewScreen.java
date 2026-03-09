package io.github.mortuusars.scholar.client.gui.screen.view;

import com.mojang.datafixers.util.Pair;
import io.github.mortuusars.scholar.book.Spread;
import io.github.mortuusars.scholar.client.gui.screen.SpreadBookScreen;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SpreadBookViewScreen extends SpreadBookScreen {
    protected BookViewAccess bookAccess;
    protected Style pageTextStyle;
    protected Pair<List<FormattedCharSequence>, List<FormattedCharSequence>> cachedPageComponents;
    protected int cachedSpread;

    public SpreadBookViewScreen(BookViewAccess bookAccess, int bookColor) {
        super(bookColor);
        this.bookAccess = bookAccess;
        this.cachedPageComponents = Pair.of(Collections.emptyList(), Collections.emptyList());
        this.cachedSpread = -1;
        this.pageTextStyle = Style.EMPTY.withoutShadow().withColor(textColor);
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
        int spreadIndex = (int) (pageIndex / 2f);
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
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        visitText(guiGraphics.textRenderer(GuiGraphics.HoveredTextEffects.TOOLTIP_AND_CURSOR), Spread.Side.LEFT);
        visitText(guiGraphics.textRenderer(GuiGraphics.HoveredTextEffects.TOOLTIP_AND_CURSOR), Spread.Side.RIGHT);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        renderBook(guiGraphics, mouseX, mouseY, partialTick);
        renderPageNumbers(guiGraphics, mouseX, mouseY, partialTick, currentSpread);
        renderTools(guiGraphics, mouseX, mouseY, partialTick);
    }

    protected void updateAndCacheContentsIfNeeded() {
        if (cachedSpread != currentSpread) {
            Component leftComponent = ComponentUtils.mergeStyles(getBookAccess().getPage(currentSpread * 2), pageTextStyle);
            Component rightComponent = ComponentUtils.mergeStyles(getBookAccess().getPage(currentSpread * 2 + 1), pageTextStyle);

            // Can't use font#split here, because we need to provide a default style.
            // Without default style text after '§r' will be displayed as white with shadow.
            cachedPageComponents = Pair.of(
                  Language.getInstance().getVisualOrder(font.getSplitter().splitLines(leftComponent, TEXT_WIDTH, pageTextStyle)),
                  Language.getInstance().getVisualOrder(font.getSplitter().splitLines(rightComponent, TEXT_WIDTH, pageTextStyle)));

            cachedSpread = currentSpread;
        }
    }

    // --

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        if (event.button() == 0 && handleTextClick(event, isDoubleClick)) {
            return true;
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    protected @NotNull Boolean handleTextClick(MouseButtonEvent event, boolean isDoubleClick) {
        return getTextSideUnderMouse(event.x(), event.y())
              .map(side -> {
                  var clickableStyleFinder = new ActiveTextCollector.ClickableStyleFinder(font, (int) event.x(), (int) event.y());
                  visitText(clickableStyleFinder, side);
                  Style style = clickableStyleFinder.result();
                  return style != null && style.getClickEvent() != null && handleTextClickEvent(style.getClickEvent());
              })
              .orElse(false);
    }

    protected boolean handleTextClickEvent(@NotNull ClickEvent clickEvent) {
        LocalPlayer localPlayer = Objects.requireNonNull(minecraft.player, "Player not available");
        switch (clickEvent) {
            case ClickEvent.ChangePage(int pageIndex):
                boolean pageChanged = this.setPage(pageIndex);
                if (pageChanged) {
                    playPageTurnSound();
                }
                break;
            case ClickEvent.RunCommand(String command):
                this.onClose();
                clickCommandAction(localPlayer, command, null);
                break;
            default:
                defaultHandleGameClickEvent(clickEvent, minecraft, this);
        }

        return true;
    }

    protected void visitText(ActiveTextCollector collector, Spread.Side side) {
        updateAndCacheContentsIfNeeded();

        List<FormattedCharSequence> lines = side == Spread.Side.LEFT
              ? cachedPageComponents.getFirst()
              : cachedPageComponents.getSecond();
        int x = leftPos + (side == Spread.Side.LEFT ? TEXT_LEFT_X : TEXT_RIGHT_X);
        int y = topPos + TEXT_Y;

        int maxLines = Math.min(TEXT_HEIGHT / font.lineHeight, lines.size());
        for (int i = 0; i < maxLines; i++) {
            FormattedCharSequence line = lines.get(i);
            collector.accept(x, y + i * font.lineHeight, line);
        }
    }

    public Optional<Spread.Side> getTextSideUnderMouse(double mouseX, double mouseY) {
        if (mouseY >= topPos + TEXT_Y && mouseY < topPos + TEXT_Y + TEXT_HEIGHT) {
            if (mouseX >= leftPos + TEXT_LEFT_X && mouseX < leftPos + TEXT_LEFT_X + TEXT_WIDTH) {
                return Optional.of(Spread.Side.LEFT);
            }
            if (mouseX >= leftPos + TEXT_RIGHT_X && mouseX < leftPos + TEXT_RIGHT_X + TEXT_WIDTH) {
                return Optional.of(Spread.Side.RIGHT);
            }
        }
        return Optional.empty();
    }
}