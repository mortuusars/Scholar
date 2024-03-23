package io.github.mortuusars.scholar;

import io.github.mortuusars.scholar.screen.SpreadBookEditScreen;
import io.github.mortuusars.scholar.screen.SpreadBookViewScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ScholarBookHandler {
    public static boolean handleBookOpening(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.is(Items.WRITABLE_BOOK)) {
            if (!Config.Client.SCHOLAR_EDIT_SCREEN_ENABLED.get())
                return false;

            if (Config.Client.SNEAK_OPENS_VANILLA_EDIT_SCREEN.get() && player.isSecondaryUseActive())
                return false;

            Minecraft.getInstance().setScreen(new SpreadBookEditScreen(player, itemStack, hand));
            return true;
        }

        if (itemStack.is(Items.WRITTEN_BOOK)) {
            if (!Config.Client.SCHOLAR_VIEW_SCREEN_ENABLED.get())
                return false;

            if (Config.Client.SNEAK_OPENS_VANILLA_VIEW_SCREEN.get() && player.isSecondaryUseActive())
                return false;

            Minecraft.getInstance().setScreen(new SpreadBookViewScreen(new SpreadBookViewScreen.WrittenBookAccess(itemStack)));
            return true;
        }

        return false;
    }
}
