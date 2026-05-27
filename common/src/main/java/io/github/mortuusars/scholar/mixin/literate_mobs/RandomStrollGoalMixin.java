package io.github.mortuusars.scholar.mixin.literate_mobs;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.client.animation.ReadingAnimation;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RandomStrollGoal.class)
public abstract class RandomStrollGoalMixin extends Goal {
    @Shadow
    @Final
    protected PathfinderMob mob;

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void onCanUse(CallbackInfoReturnable<Boolean> cir) {
        if (mob.getType().is(Scholar.Tags.EntityTypes.LITERATE)
              && ReadingAnimation.getOpenedBookHand(mob) != null
              && mob.getRandom().nextFloat() > 0.05) {
            cir.setReturnValue(false); // Prevent moving when reading
        }
    }
}
