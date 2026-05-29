package io.github.mortuusars.scholar.client.gui.screen.edit;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.platform.InputConstants;
import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.PlatformHelperClient;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.ScholarClient;
import io.github.mortuusars.scholar.book.BookColor;
import io.github.mortuusars.scholar.book.BookSignature;
import io.github.mortuusars.scholar.book.Spread;
import io.github.mortuusars.scholar.client.gui.screen.BookSigningScreen;
import io.github.mortuusars.scholar.client.gui.screen.SpreadBookScreen;
import io.github.mortuusars.scholar.client.gui.widget.textbox.TextBox;
import io.github.mortuusars.scholar.client.gui.widget.textbox.text.FormattedString;
import io.github.mortuusars.scholar.client.gui.widget.textbox.text.FormattedStringEditor;
import io.github.mortuusars.scholar.util.Change;
import io.github.mortuusars.scholar.client.util.FileDialogs;
import io.github.mortuusars.scholar.util.History;
import io.github.mortuusars.scholar.client.util.RenderUtil;
import io.netty.util.internal.StringUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public abstract class SpreadBookEditScreen extends SpreadBookScreen {
    protected final ItemStack bookStack;

    protected final List<String> pages = new ArrayList<>();

    protected final History history = new History();

    protected TextBox rightPageTextBox;
    protected TextBox leftPageTextBox;

    protected ImageButton insertEmptyPageLeftButton;
    protected ImageButton removePageLeftButton;
    protected ImageButton insertEmptyPageRightButton;
    protected ImageButton removePageRightButton;
    protected ImageButton exportBookButton;
    protected ImageButton importBookButton;

    protected boolean bookModified;

    public SpreadBookEditScreen(ItemStack bookStack) {
        super(BookColor.of(bookStack));
        this.bookStack = bookStack;
    }

    public History getHistory() {
        return history;
    }

    protected void setupPages(ItemStack bookStack) {
        pages.clear();
        CompoundTag compoundtag = bookStack.getTag();
        if (compoundtag != null) {
            BookViewScreen.loadPages(compoundtag, this.pages::add);
        }

        while (this.pages.size() < 2) {
            this.pages.add("");
        }

        setTextBoxes(false);
    }

    @Override
    protected void createWidgets() {
        leftPageTextBox = new TextBox(font, leftPos + TEXT_LEFT_X, topPos + TEXT_Y, TEXT_WIDTH, TEXT_HEIGHT)
              .setFontColor(textColor)
              .setFontUnfocusedColor(textColor)
              .setSelectionColor(selectionColor)
              .setSelectionUnfocusedColor(selectionUnfocusedColor)
              .setText(FormattedString.parse(getPageText(Spread.Side.LEFT)))
              .setOnTextChanged(text -> setPageText(Spread.Side.LEFT, text.toString()));
        addRenderableWidget(leftPageTextBox);

        rightPageTextBox = new TextBox(font, leftPos + TEXT_RIGHT_X, topPos + TEXT_Y, TEXT_WIDTH, TEXT_HEIGHT)
              .setFontColor(textColor)
              .setFontUnfocusedColor(textColor)
              .setSelectionColor(selectionColor)
              .setSelectionUnfocusedColor(selectionUnfocusedColor)
              .setText(FormattedString.parse(getPageText(Spread.Side.RIGHT)))
              .setOnTextChanged(text -> setPageText(Spread.Side.RIGHT, text.toString()));
        addRenderableWidget(rightPageTextBox);

        setupPages(bookStack);

        createPrevPageButton();
        createNextPageButton();

        ImageButton enterSignModeButton = new ImageButton(leftPos - 24, topPos + 18, 22, 22,
              ENTER_SIGN_MODE_SPRITES, b -> enterSignMode(), Component.translatable("book.signButton"));
        ImageButton enterSignModeButton = new ImageButton(leftPos - 24, topPos + 18, 22, 22, 321, 0,
                22, TEXTURE, 512, 512,
                b -> enterSignMode(), Component.translatable("book.signButton"));
        enterSignModeButton.setTooltip(Tooltip.create(Component.translatable("book.signButton")));
        addRenderableWidget(enterSignModeButton);

        createExtraToolsButtons();

        createBottomButtons();
    }

    protected void createExtraToolsButtons() {
        insertEmptyPageLeftButton = new ImageButton(leftPos + 112, topPos + 154, 13, 13,
              INSERT_EMPTY_PAGE_SPRITES, b -> insertEmptyPage(Spread.Side.LEFT), Component.translatable("gui.scholar.insert_empty_page"));
        insertEmptyPageLeftButton = new ImageButton(leftPos + 112, topPos + 154, 13, 13, 343, 0,
              13, TEXTURE, 512, 512,
              b -> insertEmptyPage(Spread.Side.LEFT), Component.translatable("gui.scholar.insert_empty_page"));
        insertEmptyPageLeftButton.setTooltip(Tooltip.create(Component.translatable("gui.scholar.insert_empty_page")
              .append(ScholarClient.KeyMappings.componentForTooltip(ScholarClient.KeyMappings.insertEmptyPageLeft))));
        addRenderableWidget(insertEmptyPageLeftButton);

        removePageLeftButton = new ImageButton(leftPos + 126, topPos + 154, 13, 13, 356, 0,
              13, TEXTURE, 512, 512,
              b -> removePage(Spread.Side.LEFT), Component.translatable("gui.scholar.remove_page"));
        removePageLeftButton.setTooltip(Tooltip.create(Component.translatable("gui.scholar.remove_page")
              .append(ScholarClient.KeyMappings.componentForTooltip(ScholarClient.KeyMappings.removePageLeft))));
        addRenderableWidget(removePageLeftButton);

        insertEmptyPageRightButton = new ImageButton(leftPos + 156, topPos + 154, 13, 13, 343, 0,
              13, TEXTURE, 512, 512,
              b -> insertEmptyPage(Spread.Side.RIGHT), Component.translatable("gui.scholar.insert_empty_page"));
        insertEmptyPageRightButton.setTooltip(Tooltip.create(Component.translatable("gui.scholar.insert_empty_page")
              .append(ScholarClient.KeyMappings.componentForTooltip(ScholarClient.KeyMappings.insertEmptyPageRight))));
        addRenderableWidget(insertEmptyPageRightButton);

        removePageRightButton = new ImageButton(leftPos + 170, topPos + 154, 13, 13, 356, 0,
              13, TEXTURE, 512, 512,
              b -> removePage(Spread.Side.RIGHT), Component.translatable("gui.scholar.remove_page"));
        removePageRightButton.setTooltip(Tooltip.create(Component.translatable("gui.scholar.remove_page")
              .append(ScholarClient.KeyMappings.componentForTooltip(ScholarClient.KeyMappings.removePageRight))));
        addRenderableWidget(removePageRightButton);
    }

    protected void createImportExportButtons() {
        importBookButton = new ImageButton(leftPos + 297, topPos + 16, 18, 18, 387, 0,
                18, TEXTURE, 512, 512,
                b -> importBook(!Screen.hasShiftDown()), Component.translatable("gui.scholar.import_book"));
        importBookButton.setTooltip(Tooltip.create(Component.translatable("gui.scholar.import_book")
                .append(ScholarClient.KeyMappings.componentForTooltip(ScholarClient.KeyMappings.importBook))
                .append(CommonComponents.NEW_LINE)
                .append(Component.translatable("gui.scholar.import_book.tooltip"))));
        addRenderableWidget(importBookButton);

        exportBookButton = new ImageButton(leftPos + 297, topPos + 41, 18, 18, 369, 0,
                18, TEXTURE, 512, 512,
                b -> exportBook(Screen.hasShiftDown()), Component.translatable("gui.scholar.export_book"));
        exportBookButton.setTooltip(Tooltip.create(Component.translatable("gui.scholar.export_book")
                .append(ScholarClient.KeyMappings.componentForTooltip(ScholarClient.KeyMappings.exportBook))
                .append(CommonComponents.NEW_LINE)
                .append(Component.translatable("gui.scholar.export_book.tooltip"))));
        addRenderableWidget(exportBookButton);
    }

    @Override
    protected void updateButtons() {
        super.updateButtons();

        insertEmptyPageLeftButton.visible = isToolsVisible();
        insertEmptyPageLeftButton.active = canInsertEmptyPage(Spread.Side.LEFT);
        insertEmptyPageRightButton.visible = isToolsVisible();
        insertEmptyPageRightButton.active = canInsertEmptyPage(Spread.Side.RIGHT);

        removePageLeftButton.visible = isToolsVisible();
        removePageLeftButton.active = canRemovePage(Spread.Side.LEFT);
        removePageRightButton.visible = isToolsVisible();
        removePageRightButton.active = canRemovePage(Spread.Side.RIGHT);

        exportBookButton.visible = isToolsVisible();
        exportBookButton.active = pages.stream().anyMatch(p -> !p.isEmpty());
        importBookButton.visible = isToolsVisible();
    }

    // -- Render

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        updateButtons();

        renderBackground(guiGraphics);
        renderBook(guiGraphics, mouseX, mouseY, partialTick);
        renderPageNumbers(guiGraphics, mouseX, mouseY, partialTick, currentSpread);
        renderTools(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBook(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderUtil.withColorMultiplied(bookColor, () -> {
            if (isToolsVisible()) {
                // Import/Export buttons BG
                guiGraphics.blit(TEXTURE, leftPos + 295, topPos + 14, 0, 388,
                        23, 48, 512, 512);
            }

            // Cover
            guiGraphics.blit(TEXTURE, (width - BOOK_WIDTH) / 2, (height - BOOK_HEIGHT) / 2, BOOK_WIDTH, BOOK_HEIGHT,
                    0, 0, BOOK_WIDTH, BOOK_HEIGHT, 512, 512);

            // Enter Sign Mode button BG
            guiGraphics.blit(TEXTURE, leftPos - 29, topPos + 14, 0, 360,
                    29, 28, 512, 512);
        });

        // Paper
        guiGraphics.blit(TEXTURE, (width - BOOK_WIDTH) / 2, (height - BOOK_HEIGHT) / 2, BOOK_WIDTH, BOOK_HEIGHT,
                0, 180, BOOK_WIDTH, BOOK_HEIGHT, 512, 512);
    }

    @Override
    protected void renderTools(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int x = width - 12;
        int y = 6;
        boolean isHovering = mouseX >= x - 3 && mouseX < x + 12 + 3 && mouseY >= y - 3 && mouseY < y + 12;

        int color = isHovering
              ? 0xFFFFFFFF
              : Config.Client.TUTORIAL_EXTRA_TOOLS.get() && Util.getMillis() % 750 > 300 ? 0xFFff625e : 0xFFAAAAAA;
        guiGraphics.drawString(font, "✎", x, y, color);

        if (isHovering) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("gui.scholar.tools.toggle")
                  .append(ScholarClient.KeyMappings.componentForTooltip(ScholarClient.KeyMappings.toggleBookTools)));
            tooltip.add(Component.translatable("gui.scholar.tools.tooltip.copy_without_formatting"));
            tooltip.add(Component.translatable("gui.scholar.tools.tooltip.paste_without_formatting"));
            tooltip.add(Component.translatable("gui.scholar.tools.tooltip.undo"));
            tooltip.add(Component.translatable("gui.scholar.tools.tooltip.redo"));
            guiGraphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY + 20);
        }
    }

    // -- Input

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (ScholarClient.KeyMappings.importBook.matches(key, scanCode)) {
            playButtonClickSound();
            importBook(!Screen.hasShiftDown());
            return true;
        }

        if (ScholarClient.KeyMappings.exportBook.matches(key, scanCode)) {
            playButtonClickSound();
            exportBook(!Screen.hasShiftDown());
            return true;
        }

        if (Screen.hasControlDown() && key == InputConstants.KEY_Z && !Screen.hasAltDown()) {
            @Nullable Change change;
            float pitch;

            if (Screen.hasShiftDown()) {
                change = getHistory().redo();
                pitch = change == null ? 1.4f : 0.8f;
            } else {
                change = getHistory().undo();
                pitch = change == null ? 1.8f : 0.95f;
            }

            playButtonClickSound(pitch);
            return true;
        }

        if ((!(getFocused() instanceof TextBox textBox) || !textBox.getEditor().isSelecting())
              && key == InputConstants.KEY_C && Screen.hasControlDown() && !Screen.hasAltDown()) {
            String bookContents = getBookContents(!Screen.hasShiftDown());
            Minecraft.getInstance().keyboardHandler.setClipboard(bookContents);
            return true;
        }

        if (PlatformHelperClient.matchesWithModifiers(ScholarClient.KeyMappings.insertEmptyPageLeft, key, scanCode, modifiers)) {
            insertEmptyPage(Spread.Side.LEFT);
            return true;
        }

        if (PlatformHelperClient.matchesWithModifiers(ScholarClient.KeyMappings.insertEmptyPageRight, key, scanCode, modifiers)) {
            insertEmptyPage(Spread.Side.RIGHT);
            return true;
        }

        if (PlatformHelperClient.matchesWithModifiers(ScholarClient.KeyMappings.removePageLeft, key, scanCode, modifiers)) {
            removePage(Spread.Side.LEFT);
            return true;
        }

        if (PlatformHelperClient.matchesWithModifiers(ScholarClient.KeyMappings.removePageRight, key, scanCode, modifiers)) {
            removePage(Spread.Side.RIGHT);
            return true;
        }

        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = width - 12;
        int y = 6;
        if (mouseX >= x - 3 && mouseX < x + 12 + 3 && mouseY >= y - 3 && mouseY < y + 12) {
            toggleBookTools();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    // --

    @Override
    protected boolean pageForward() {
        if (super.pageForward()) {
            // Ensure we have pages to display:
            while (this.pages.size() < (currentSpread + 1) * 2) {
                appendEmptyPage();
            }

            setTextBoxes();

            getHistory().add(() -> {
                super.pageForward();
                setTextBoxes();
            }, () -> {
                super.pageBack();
                setTextBoxes();
            });

            return true;
        }
        return false;
    }

    @Override
    protected boolean pageToStart() {
        int currentSpreadIndex = currentSpread;
        if (super.pageToStart()) {
            setTextBoxes();

            getHistory().add(() -> {
                super.pageToStart();
                setTextBoxes();
            }, () -> {
                super.setSpread(currentSpreadIndex);
                setTextBoxes();
            });

            return true;
        }
        return false;
    }

    @Override
    protected boolean pageBack() {
        if (super.pageBack()) {
            setTextBoxes();
            getHistory().add(() -> {
                super.pageBack();
                setTextBoxes();
            }, () -> {
                super.pageForward();
                setTextBoxes();
            });
            return true;
        }
        return false;
    }

    @Override
    protected boolean pageToEnd() {
        int currentSpreadIndex = currentSpread;
        if (super.pageToEnd()) {
            setTextBoxes();

            getHistory().add(() -> {
                super.pageToEnd();
                setTextBoxes();
            }, () -> {
                super.setSpread(currentSpreadIndex);
                setTextBoxes();
            });

            return true;
        }
        return false;
    }

    protected void setTextBoxes() {
        setTextBoxes(true);
    }

    protected void setTextBoxes(boolean resetCursor) {
        FormattedString leftString = FormattedString.parse(getPageText(Spread.Side.LEFT));
        if (!leftString.equals(leftPageTextBox.getEditor().getString())) {
            leftPageTextBox.getEditor().setString(leftString);
            int leftCursorPos = resetCursor ? 0 : leftPageTextBox.getEditor().getCursorPos();
            leftPageTextBox.getEditor().setCursorPos(leftCursorPos, false);
        }
        leftPageTextBox.getDisplayCache().scheduleUpdate();

        FormattedString rightString = FormattedString.parse(getPageText(Spread.Side.RIGHT));
        if (!rightString.equals(rightPageTextBox.getEditor().getString())) {
            rightPageTextBox.getEditor().setString(rightString);
            int rightCursorPos = resetCursor ? 0 : rightPageTextBox.getEditor().getCursorPos();
            rightPageTextBox.getEditor().setCursorPos(rightCursorPos, false);
        }
        rightPageTextBox.getDisplayCache().scheduleUpdate();
    }

    protected void enterSignMode() {
        saveChanges(false, null);
        minecraft.execute(() -> minecraft.setScreen(new BookSigningScreen(this, bookColor, this::signBook)));
    }

    protected abstract void signBook(BookSignature signature);

    // --

    protected String getPageText(Spread.Side side) {
        int pageIndex = side.getPageIndexFromSpread(currentSpread);
        return pageIndex >= 0 && pageIndex < this.pages.size() ? this.pages.get(pageIndex) : "";
    }

    protected void setPageText(Spread.Side side, String text) {
        int pageIndex = side.getPageIndexFromSpread(currentSpread);

        // Add empty pages until current page index
        while (pageIndex > pages.size() - 1 && pageIndex < getPageCount()) {
            appendEmptyPage();
        }

        if (pageIndex >= 0 && pageIndex < this.pages.size()) {
            String currentText = getPageText(side);
            getHistory().add(() -> {
                pages.set(pageIndex, text);
                this.bookModified = true;
                setTextBoxes(false);

            }, () -> {
                pages.set(pageIndex, currentText);
                this.bookModified = true;
                setTextBoxes(false);
            });

            this.pages.set(pageIndex, text);
            this.bookModified = true;
        }
    }

    protected void appendEmptyPage() {
        if (this.pages.size() < 100) {
            this.pages.add("");
        }
    }

    protected void insertEmptyPage(Spread.Side side) {
        if (!canInsertEmptyPage(side)) {
            Objects.requireNonNull(Minecraft.getInstance().player).displayClientMessage(
                  Component.translatable("gui.scholar.cannot_insert_page"), false);
            return;
        }

        int pageIndex = side.getPageIndexFromSpread(currentSpread);

        Change change = Change.create(() -> {
            pages.add(pageIndex, "");
            while (pages.size() >= 100) {
                pages.remove(pages.size() - 1);
            }
            setTextBoxes();
            bookModified = true;
            playPageTurnSound(1f, 0.6f);
        }, () -> {
            pages.remove(pageIndex);
            setTextBoxes();
            bookModified = true;
            playPageTurnSound(1f, 0.8f);
        });

        change.apply();
        getHistory().add(change);
    }

    protected void removePage(Spread.Side side) {
        int pageIndex = side.getPageIndexFromSpread(currentSpread);
        String pageContent = pages.get(pageIndex);

        Change change = Change.create(() -> {
            pages.remove(pageIndex);
            while (this.pages.size() < Spread.Side.RIGHT.getPageIndexFromSpread(currentSpread) + 1) {
                this.pages.add("");
            }
            setTextBoxes();
            bookModified = true;
            playPageTurnSound(0.85f, 0.6f);
        }, () -> {
            pages.add(pageIndex, pageContent);
            setTextBoxes();
            bookModified = true;
            playPageTurnSound(0.85f, 0.8f);
        });

        change.apply();
        getHistory().add(change);
    }

    protected void saveChanges(boolean sign, @Nullable String title) {
        if (bookModified || sign) {
            if (!sign) {
                title = null;
            }
            removeEmptyTrailingPages();
            updateLocalCopy(sign, title);

            sendChanges(title);
        }
    }

    protected abstract void sendChanges(@Nullable String title);

    protected void removeEmptyTrailingPages() {
        ListIterator<String> iterator = this.pages.listIterator(this.pages.size());
        while (iterator.hasPrevious() && iterator.previous().isEmpty()) {
            iterator.remove();
        }
    }

    protected void updateLocalCopy() {
        ListTag listTag = new ListTag();
        this.pages.stream().map(StringTag::valueOf).forEach(listTag::add);
        if (!this.pages.isEmpty()) {
            this.bookStack.addTagElement("pages", listTag);
        }
    }

    // --

    protected boolean canInsertEmptyPage(Spread.Side side) {
        int pageIndex = side.getPageIndexFromSpread(currentSpread);
        int lastPageWithContent = getLastPageWithContent();
        return lastPageWithContent < 99 && pageIndex <= lastPageWithContent;
    }

    protected boolean canRemovePage(Spread.Side side) {
        return containsContentAfter(side.getPageIndexFromSpread(currentSpread));
    }

    @Override
    public int getLastPage() {
        return Math.max(pages.size() - 1, 0);
    }

    @Override
    protected int getFirstPageWithContent() {
        for (int i = 0; i < pages.size(); i++) {
            if (!pages.get(i).isEmpty()) {
                return i;
            }
        }
        return 0;
    }

    @Override
    protected int getLastPageWithContent() {
        for (int i = pages.size() - 1; i >= 0; i--) {
            if (!pages.get(i).isEmpty()) {
                return i;
            }
        }
        return 0;
    }

    protected boolean containsContentAfter(int pageIndex) {
        return pages.stream().skip(pageIndex).anyMatch(p -> !p.isEmpty());
    }

    public String getBookContents(boolean withFormatting) {
        String contents = String.join("\n", pages);
        if (!withFormatting) {
            ChatFormatting.stripFormatting(contents);
        }
        return contents;
    }

    public void setBookContents(String contents, boolean withFormatting) {
        List<String> oldPages = new ArrayList<>(pages);
        pages.clear();

        contents = contents.replaceAll("\\r", "");
        if (!withFormatting) {
            contents = ChatFormatting.stripFormatting(contents);
            assert contents != null;
        }
        String[] pages = contents.split("\f");

        Predicate<String> validator = FormattedStringEditor.Validator.fitInDimensions(font, leftPageTextBox.getWidth(), leftPageTextBox.getHeight());

        for (String pageContent : pages) {
            if (pageContent.isEmpty()) {
                this.pages.add("");
                continue;
            }

            FormattedString string = FormattedString.parse(pageContent);

            int currentChar = 0;
            FormattedString currentString = new FormattedString();
            String lastValidString = "";

            while (currentChar < string.length()) {
                currentString.add(string.get(currentChar));
                currentChar++;
                String str = currentString.toString();

                if (!validator.test(str)) {
                    this.pages.add(lastValidString);

                    if (this.pages.size() >= 100) {
                        lastValidString = "";
                        break;
                    }

                    currentChar--;
                    currentString.clear();
                    continue;
                }

                lastValidString = str;
            }

            if (!lastValidString.isEmpty() && this.pages.size() <= 100) {
                this.pages.add(lastValidString);
            }
        }

        bookModified = true;
        setTextBoxes();

        List<String> newPages = new ArrayList<>(this.pages);

        getHistory().add(() -> {
            this.pages.clear();
            this.pages.addAll(newPages);
            bookModified = true;
            setTextBoxes();
        }, () -> {
            this.pages.clear();
            this.pages.addAll(oldPages);
            bookModified = true;
            setTextBoxes();
        });
    }

    public void importBook(boolean withFormatting) {
        CompletableFuture.runAsync(() -> {
            String defaultDirectory = Minecraft.getInstance().gameDirectory.toPath().toAbsolutePath().normalize().toString();
            if (!defaultDirectory.endsWith(File.separator)) {
                defaultDirectory = defaultDirectory + File.separator;
            }
            String title = Component.translatable("gui.scholar.import_book").getString();

            FileDialogs.loadFile(defaultDirectory, title, "Text Files (.txt)", false, "*.txt").ifPresent(filePath -> {
                try {
                    String content = Files.readString(Path.of(filePath));
                    Minecraft.getInstance().execute(() -> setBookContents(content, withFormatting));
                } catch (IOException e) {
                    Minecraft.getInstance().execute(() -> player.displayClientMessage(
                          Component.translatable("gui.scholar.import_book.failure"), false));
                    Scholar.LOGGER.error("Failed to import book: ", e);
                }
            });
        }).exceptionally(e -> {
            Minecraft.getInstance().execute(() -> player.displayClientMessage(
                  Component.translatable("gui.scholar.import_book.failure"), false));
            Scholar.LOGGER.error("Failed to import book: ", e);
            return null;
        });
    }

    public void exportBook(boolean withFormatting) {
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

    // --

    @Override
    public void onClose() {
        saveChanges(false, null);
        super.onClose();
    }
}
