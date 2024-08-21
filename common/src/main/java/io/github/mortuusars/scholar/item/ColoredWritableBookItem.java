package io.github.mortuusars.scholar.item;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.visual.BookColor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ColoredWritableBookItem extends WritableBookItem implements IColoredBook {
    private final DyeColor color;

    public ColoredWritableBookItem(DyeColor color, Properties properties) {
        super(properties);
        this.color = color;
    }

    @Override
    public DyeColor getColor() {
        return color;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("item.scholar.deprecated_book_message").withStyle(ChatFormatting.RED));
        tooltipComponents.add(Component.translatable("item.scholar.convert_book_message").withStyle(ChatFormatting.RED));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack deprecatedStack = player.getItemInHand(usedHand);

        ItemStack correctStack = new ItemStack(Items.WRITABLE_BOOK);
        correctStack.setTag(deprecatedStack.getTag());

        BookColor color = BookColor.COLORS.stream()
                .filter(c -> c.getDyeColor()
                        .map(dye -> dye.equals(getColor()))
                        .orElse(false))
                .findFirst()
                .orElse(BookColor.DEFAULT);

        BookColor.set(correctStack, color);

        player.playSound(Scholar.SoundEvents.BOOK_SIGNED.get(), 1f, 1f);

        return InteractionResultHolder.success(correctStack);
    }
}
