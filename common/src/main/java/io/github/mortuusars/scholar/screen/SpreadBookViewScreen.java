package io.github.mortuusars.scholar.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.util.RenderUtil;
import io.github.mortuusars.scholar.visual.BookColors;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntFunction;

public class SpreadBookViewScreen extends Screen {
    public static final BookAccess EMPTY_ACCESS = new BookAccess() {
        public int getPageCount() {
            return 0;
        }

        public FormattedText getPageRaw(int i) {
            return FormattedText.EMPTY;
        }
    };

    public static final ResourceLocation TEXTURE = Scholar.resource("textures/gui/book.png");

    public static final int BOOK_WIDTH = 295;
    public static final int BOOK_HEIGHT = 180;

    public static final int TEXT_LEFT_X = 23;
    public static final int TEXT_RIGHT_X = 158;
    public static final int TEXT_Y = 21;
    public static final int TEXT_WIDTH = 114;
    public static final int TEXT_HEIGHT = 128;

    protected BookAccess bookAccess;
    protected final int bookColor;

    protected int currentSpread;
    protected Pair<List<FormattedCharSequence>, List<FormattedCharSequence>> cachedPageComponents;
    protected int cachedSpread;

    protected final int mainFontColor;
    protected final int secondaryFontColor;

    protected int leftPos;
    protected int topPos;
    protected Button nextButton;
    protected Button prevButton;

    public SpreadBookViewScreen(BookAccess bookAccess, int bookColor) {
        super(GameNarrator.NO_TITLE);
        this.bookColor = bookColor;
        this.cachedPageComponents = Pair.of(Collections.emptyList(), Collections.emptyList());
        this.cachedSpread = -1;
        this.bookAccess = bookAccess;

        this.mainFontColor = Config.Client.getColor(Config.Client.MAIN_FONT_COLOR);
        this.secondaryFontColor = Config.Client.getColor(Config.Client.SECONDARY_FONT_COLOR);
    }

    public SpreadBookViewScreen() {
        this(EMPTY_ACCESS, BookColors.REGULAR);
    }

    public void setBookAccess(BookAccess bookAccess) {
        this.bookAccess = bookAccess;
        this.currentSpread = Mth.clamp(this.currentSpread, 0, bookAccess.getPageCount());
        this.updateButtonVisibility();
        this.cachedSpread = -1;
    }

    protected void init() {
        this.leftPos = (this.width - BOOK_WIDTH) / 2;
        this.topPos = (this.height - BOOK_HEIGHT) / 2;

        this.createMenuControls();
        this.createPageControlButtons();
    }

    @Override
    public boolean isPauseScreen() {
        return Config.Client.BOOK_VIEW_SCREEN_PAUSE.get();
    }

    protected void createMenuControls() {
        if (Config.Client.BOOK_VIEW_SCREEN_SHOW_DONE_BUTTON.get()) {
            this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE,
                    (button) -> this.onClose()).bounds(this.width / 2 - 60, topPos + BOOK_HEIGHT + 12, 120, 20).build());
        }
    }

    protected void createPageControlButtons() {
        ImageButton prevButton = new ImageButton(leftPos + 12, topPos + 156, 13, 15,
                295, 0, 15, TEXTURE, 512, 512,
                (button) -> this.pageBack());
        prevButton.setTooltip(Tooltip.create(Component.translatable("spectatorMenu.previous_page")));
        this.prevButton = this.addRenderableWidget(prevButton);
        ImageButton nextButton = new ImageButton(leftPos + 270, topPos + 156, 13, 15,
                308, 0, 15, TEXTURE, 512, 512,
                (button) -> this.pageForward());
        nextButton.setTooltip(Tooltip.create(Component.translatable("spectatorMenu.next_page")));
        this.nextButton = this.addRenderableWidget(nextButton);
        this.updateButtonVisibility();
    }

    private int getSpreadCount() {
        return (int)Math.ceil(this.bookAccess.getPageCount() / 2.0);
    }

    protected void pageBack() {
        if (this.currentSpread > 0) {
            this.currentSpread--;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 0.8f));
        }

        this.updateButtonVisibility();
    }

    protected void pageForward() {
        if (this.currentSpread < this.getSpreadCount() - 1) {
            this.currentSpread++;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1f));
        }

        this.updateButtonVisibility();
    }


    public boolean setPage(int pageIndex) {
        pageIndex = Mth.clamp(pageIndex, 0, this.bookAccess.getPageCount() - 1);
        int spreadIndex = (int)(pageIndex / 2f);
        if (spreadIndex != this.currentSpread) {
            this.currentSpread = spreadIndex;
            this.updateButtonVisibility();
            this.cachedSpread = -1;
            return true;
        } else {
            return false;
        }
    }

    protected boolean forcePage(int i) {
        return this.setPage(i);
    }

    private void updateButtonVisibility() {
        this.nextButton.visible = this.currentSpread < this.getSpreadCount() - 1;
        this.prevButton.visible = this.currentSpread > 0;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers))
            return true;

        if (Minecraft.getInstance().options.keyInventory.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }

        if (keyCode == InputConstants.KEY_LEFT || keyCode == InputConstants.KEY_PAGEUP || Minecraft.getInstance().options.keyLeft.matches(keyCode, scanCode)) {
            pageBack();
            return true;
        }

        if (keyCode == InputConstants.KEY_RIGHT || keyCode == InputConstants.KEY_PAGEDOWN || Minecraft.getInstance().options.keyRight.matches(keyCode, scanCode)) {
            pageForward();
            return true;
        }

        return false;
    }

    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);

        RenderUtil.withColorMultiplied(bookColor, () -> {
            // Cover
            guiGraphics.blit(TEXTURE, (width - BOOK_WIDTH) / 2, (height - BOOK_HEIGHT) / 2, BOOK_WIDTH, BOOK_HEIGHT,
                    0, 0, BOOK_WIDTH, BOOK_HEIGHT, 512, 512);
        });

        // Pages
        guiGraphics.blit(TEXTURE, (width - BOOK_WIDTH) / 2, (height - BOOK_HEIGHT) / 2, BOOK_WIDTH, BOOK_HEIGHT,
                0, 180, BOOK_WIDTH, BOOK_HEIGHT, 512, 512);

        drawPageNumbers(guiGraphics, currentSpread);

        if (this.cachedSpread != this.currentSpread) {
            FormattedText leftFormattedText = this.bookAccess.getPage(this.currentSpread * 2);
            FormattedText rightFormattedText = bookAccess.getPageCount() > this.currentSpread * 2 + 1 ?
                    this.bookAccess.getPage(this.currentSpread * 2 + 1) : FormattedText.EMPTY;

            cachedPageComponents = Pair.of(
                    font.split(leftFormattedText, TEXT_WIDTH),
                    font.split(rightFormattedText, TEXT_WIDTH));
        }

        this.cachedSpread = this.currentSpread;

        drawPageContents(guiGraphics, this.cachedPageComponents.getFirst(), leftPos + TEXT_LEFT_X, topPos + TEXT_Y);
        drawPageContents(guiGraphics, this.cachedPageComponents.getSecond(), leftPos + TEXT_RIGHT_X, topPos + TEXT_Y);

        Style style = this.getClickedComponentStyleAt(mouseX, mouseY);
        if (style != null)
            guiGraphics.renderComponentHoverEffect(this.font, style, mouseX, mouseY);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    protected void drawPageNumbers(GuiGraphics guiGraphics, int currentSpreadIndex) {
        String leftPageNumber = Integer.toString(currentSpreadIndex * 2 + 1);
        guiGraphics.drawString(font, leftPageNumber, leftPos + 69 + (8 - font.width(leftPageNumber) / 2),
                topPos + 157, secondaryFontColor, false);

        String rightPageNumber = Integer.toString(currentSpreadIndex * 2 + 2);
        guiGraphics.drawString(font, rightPageNumber, leftPos + 208 + (8 - font.width(rightPageNumber) / 2),
                topPos + 157, secondaryFontColor, false);
    }

    protected void drawPageContents(GuiGraphics guiGraphics, List<FormattedCharSequence> lines, int x, int y) {
        int maxLines = Math.min(TEXT_HEIGHT / font.lineHeight, lines.size());
        for (int i = 0; i < maxLines; ++i) {
            FormattedCharSequence text = lines.get(i);
            guiGraphics.drawString(font, text, x, y + i * font.lineHeight, mainFontColor, false);
        }
    }

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
                boolean pageChanged = this.forcePage(pageIndex);
                if (pageChanged)
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1f));
                return pageChanged;
            } catch (Exception var5) {
                return false;
            }
        } else {
            boolean handled = super.handleComponentClicked(style);
            if (handled && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND)
                this.closeScreen();

            return handled;
        }
    }

    protected void closeScreen() {
        Minecraft.getInstance().setScreen(null);
    }

    @Nullable
    public Style getClickedComponentStyleAt(double mouseX, double mouseY) {
        if (mouseY < topPos + TEXT_Y || mouseY >= topPos + TEXT_Y + TEXT_HEIGHT)
            return null;

        boolean isOverRightPage;
        if (mouseX >= leftPos + TEXT_RIGHT_X && mouseX < leftPos + TEXT_RIGHT_X + TEXT_WIDTH)
            isOverRightPage = true;
        else if (mouseX >= leftPos + TEXT_LEFT_X && mouseX < leftPos + TEXT_LEFT_X + TEXT_WIDTH)
            isOverRightPage = false;
        else
            return null;

        List<FormattedCharSequence> pageContents = isOverRightPage ? this.cachedPageComponents.getSecond() : this.cachedPageComponents.getFirst();

        if (pageContents.isEmpty())
            return null;

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

    static List<String> loadPages(CompoundTag compoundTag) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        Objects.requireNonNull(builder);
        loadPages(compoundTag, builder::add);
        return builder.build();
    }

    public static void loadPages(CompoundTag compoundTag, Consumer<String> consumer) {
        ListTag listTag = compoundTag.getList("pages", 8).copy();
        IntFunction<String> intFunction;
        if (Minecraft.getInstance().isTextFilteringEnabled() && compoundTag.contains("filtered_pages", 10)) {
            CompoundTag compoundTag2 = compoundTag.getCompound("filtered_pages");
            intFunction = (ix) -> {
                String string = String.valueOf(ix);
                return compoundTag2.contains(string) ? compoundTag2.getString(string) : listTag.getString(ix);
            };
        } else {
            Objects.requireNonNull(listTag);
            intFunction = listTag::getString;
        }

        for (int i = 0; i < listTag.size(); ++i) {
            consumer.accept(intFunction.apply(i));
        }

    }

    public interface BookAccess {
        int getPageCount();

        FormattedText getPageRaw(int i);

        default FormattedText getPage(int i) {
            return i >= 0 && i < this.getPageCount() ? this.getPageRaw(i) : FormattedText.EMPTY;
        }

        static BookAccess fromItem(ItemStack itemStack) {
            if (itemStack.is(Items.WRITTEN_BOOK)) {
                return new WrittenBookAccess(itemStack);
            } else {
                return (itemStack.is(Items.WRITABLE_BOOK) ? new WritableBookAccess(itemStack) : EMPTY_ACCESS);
            }
        }
    }

    public static class WritableBookAccess implements BookAccess {
        private final List<String> pages;

        public WritableBookAccess(ItemStack itemStack) {
            this.pages = readPages(itemStack);
        }

        private static List<String> readPages(ItemStack itemStack) {
            CompoundTag compoundTag = itemStack.getTag();
            return (compoundTag != null ? loadPages(compoundTag) : ImmutableList.of());
        }

        public int getPageCount() {
            return this.pages.size();
        }

        public FormattedText getPageRaw(int i) {
            return FormattedText.of(this.pages.get(i));
        }
    }

    public static class WrittenBookAccess implements BookAccess {
        private final List<String> pages;

        public WrittenBookAccess(ItemStack itemStack) {
            this.pages = readPages(itemStack);
        }

        private static List<String> readPages(ItemStack itemStack) {
            CompoundTag compoundTag = itemStack.getTag();
            return (WrittenBookItem.makeSureTagIsValid(compoundTag) ? loadPages(compoundTag) : ImmutableList.of(Component.Serializer.toJson(Component.translatable("book.invalid.tag").withStyle(ChatFormatting.DARK_RED))));
        }

        public int getPageCount() {
            return this.pages.size();
        }

        public @NotNull FormattedText getPageRaw(int i) {
            String string = this.pages.get(i);

            try {
                FormattedText formattedText = Component.Serializer.fromJson(string);
                if (formattedText != null) {
                    return formattedText;
                }
            } catch (Exception ignored) {
            }

            return FormattedText.of(string);
        }
    }
}