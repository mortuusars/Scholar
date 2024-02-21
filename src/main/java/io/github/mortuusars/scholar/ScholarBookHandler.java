package io.github.mortuusars.scholar;

import io.github.mortuusars.scholar.screen.SpreadBookViewScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ScholarBookHandler {
    public static boolean handleBookOpening(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.is(Items.WRITTEN_BOOK)) {
            if (!Config.Client.SCHOLAR_VIEW_SCREEN_ENABLED.get())
                return false;

            if (Config.Client.SNEAK_OPENS_VANILLA_SCREEN.get() && player.isSecondaryUseActive())
                return false;

            Minecraft.getInstance().setScreen(new SpreadBookViewScreen(new SpreadBookViewScreen.WrittenBookAccess(itemStack)));
            return true;
        }

        return false;
    }
}
