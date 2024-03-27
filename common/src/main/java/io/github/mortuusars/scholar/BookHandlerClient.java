package io.github.mortuusars.scholar;

import io.github.mortuusars.scholar.screen.SpreadBookEditScreen;
import io.github.mortuusars.scholar.screen.SpreadBookViewScreen;
import io.github.mortuusars.scholar.visual.BookColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.item.WrittenBookItem;

public class BookHandlerClient {
    public static boolean handleBookOpening(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.getItem() instanceof WritableBookItem) {
            if (Config.Common.WRITABLE_REPLACE_VANILLA_SCREEN.get()
                    && (!Config.Common.WRITABLE_SNEAK_OPENS_VANILLA_SCREEN.get() || !player.isSecondaryUseActive())) {
                Minecraft.getInstance().setScreen(new SpreadBookEditScreen(player, itemStack, hand));
            }
            else
                Minecraft.getInstance().setScreen(new BookEditScreen(player, itemStack, hand));

            return true;
        }

        if (itemStack.getItem() instanceof WrittenBookItem) {
            if (Config.Common.WRITTEN_REPLACE_VANILLA_SCREEN.get()
                    && (!Config.Common.WRITTEN_SNEAK_OPENS_VANILLA_SCREEN.get() || !player.isSecondaryUseActive())) {
                Minecraft.getInstance().setScreen(new SpreadBookViewScreen(new SpreadBookViewScreen.WrittenBookAccess(itemStack), BookColors.fromStack(itemStack)));
            }
            else
                Minecraft.getInstance().setScreen(new BookViewScreen(new BookViewScreen.WrittenBookAccess(itemStack)));

            return true;
        }

        return false;
    }
}
