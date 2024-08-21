package io.github.mortuusars.scholar.util;

import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.item.WrittenBookItem;

public class BookHelper {
    public static int getPageCount(ItemStack stack) {
        if (stack.is(Items.WRITTEN_BOOK))
            return WrittenBookItem.getPageCount(stack);

        if (stack.is(Items.WRITABLE_BOOK) && stack.getTag() != null
                && stack.getTag().contains("pages", Tag.TAG_LIST))
            return stack.getTag().getList("pages", Tag.TAG_STRING).size();

        return 0;
    }
}
