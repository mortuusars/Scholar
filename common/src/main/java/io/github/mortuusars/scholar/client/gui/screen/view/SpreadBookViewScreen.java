package io.github.mortuusars.scholar.client.gui.screen.view;

import com.mojang.datafixers.util.Pair;
import io.github.mortuusars.scholar.book.Spread;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.ScholarClient;
import io.github.mortuusars.scholar.client.gui.screen.SpreadBookScreen;
import net.minecraft.client.gui.ActiveTextCollector;
import io.github.mortuusars.scholar.client.gui.screen.edit.SpreadBookEditScreen;
import io.github.mortuusars.scholar.client.util.FileDialogs;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

public abstract class SpreadBookViewScreen extends SpreadBookScreen {
    protected BookViewAccess bookAccess;
    protected Style pageTextStyle;
    protected Pair<List<FormattedCharSequence>, List<FormattedCharSequence>> cachedPageComponents;
    protected int cachedSpread;

    protected ImageButton exportBookButton;

    public SpreadBookViewScreen(BookViewAccess bookAccess, int bookColor) {
        super(bookColor);
        this.bookAccess = bookAccess;
        this.cachedPageComponents = Pair.of(Collections.emptyList(), Collections.emptyList());
        this.cachedSpread = -1;
        this.pageTextStyle = Style.EMPTY.withoutShadow().withColor(textColor);
    }

    @Override
    public boolean isGolden() {
        return getBookAccess().isGolden();
    }

    @Override
    protected void createWidgets() {
        super.createWidgets();
        exportBookButton = new ImageButton(leftPos + 297, topPos + 16, 18, 18, SpreadBookEditScreen.EXPORT_BOOK_SPRITES,
              _ -> exportBook(!Minecraft.getInstance().hasShiftDown()), Component.translatable("gui.scholar.export_book"));
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
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        updateButtons();
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        visitText(graphics.textRenderer(GuiGraphicsExtractor.HoveredTextEffects.TOOLTIP_AND_CURSOR), Spread.Side.LEFT);
        visitText(graphics.textRenderer(GuiGraphicsExtractor.HoveredTextEffects.TOOLTIP_AND_CURSOR), Spread.Side.RIGHT);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        extractBook(graphics, mouseX, mouseY, partialTick);
        extractPageNumbers(graphics, mouseX, mouseY, partialTick, currentSpread);
        extractTools(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void extractBook(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBook(graphics, mouseX, mouseY, partialTick);
        if (isToolsVisible()) {
            // Export button BG
            graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos + 295, topPos + 14,
                  0, 388, 23, 24, 512, 512, getTintColor());
        }
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

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (ScholarClient.KeyMappings.exportBook.matches(event)) {
            playButtonClickSound();
            exportBook(!event.hasShiftDown());
            return true;
        }

        return super.keyPressed(event);
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
                          .withClickEvent(new ClickEvent.OpenFile(filePath)));
                    Minecraft.getInstance().execute(() -> player.sendSystemMessage(
                          Component.translatable("gui.scholar.export_book.success")
                                .append(filePathComponent)));
                } catch (IOException e) {
                    Minecraft.getInstance().execute(() -> player.sendSystemMessage(
                          Component.translatable("gui.scholar.export_book.failure")));
                    Scholar.LOGGER.error("Failed to export book: ", e);
                }
            });
        }).exceptionally(e -> {
            Minecraft.getInstance().execute(() -> player.sendSystemMessage(
                  Component.translatable("gui.scholar.export_book.failure")));
            Scholar.LOGGER.error("Failed to export book: ", e);
            return null;
        });
    }
}