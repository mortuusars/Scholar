package io.github.mortuusars.scholar.world.entity;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ReadBookGoal extends Goal {
    private final Mob mob;
    private int readingTime;
    private int readingDuration;

    public ReadBookGoal(Mob mob) {
        this.mob = mob;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canUse() {
        if (Reading.getOpenedBookHand(mob) != null) {
            stop(); // Closes the book properly after world reload or something.
        }
        return canRead() && mob.getRandom().nextFloat() < Config.Common.LITERATE_MOBS_BOOK_READING_CHANCE.get();
    }

    public boolean canRead() {
        return mob.is(Scholar.Tags.EntityTypes.LITERATE)
              && mob.getTarget() == null
              && !mob.isInWaterOrRain()
              && !mob.getNavigation().isInProgress()
              && !mob.isOnFire()
              && isReadableBook(mob.getMainHandItem());
    }

    @Override
    public boolean canContinueToUse() {
        return readingTime < readingDuration && canRead();
    }

    @Override
    public void start() {
        mob.getMainHandItem().set(Scholar.DataComponents.BOOK_OPEN, Unit.INSTANCE);
        mob.playSound(SoundEvents.BOOK_PAGE_TURN, 0.6f, 1.1f);
        mob.swing(InteractionHand.MAIN_HAND);
        readingTime = 0;
        readingDuration = 300 + mob.getRandom().nextInt(10) * 20;
    }

    @Override
    public void tick() {
        readingTime++;
    }

    @Override
    public void stop() {
        mob.getMainHandItem().remove(Scholar.DataComponents.BOOK_OPEN);
        mob.playSound(SoundEvents.BOOK_PAGE_TURN, 0.6f, 0.75f);
        mob.swing(InteractionHand.MAIN_HAND);
    }

    // --

    public static boolean isReadableBook(ItemStack stack) {
        return stack.is(Items.BOOK) || stack.is(Items.WRITTEN_BOOK) || stack.is(Items.WRITABLE_BOOK);
    }

    public static int getPriority(Mob mob) {
        return 7;
    }
}
