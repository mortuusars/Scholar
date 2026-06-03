package io.github.mortuusars.scholar.mixin.reading;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.mortuusars.scholar.world.entity.Reading;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PiglinModel.class)
public abstract class PiglinModelMixin<T extends Mob> extends PlayerModel<T> {
    public PiglinModelMixin(ModelPart root, boolean slim) {
        super(root, slim);
    }

    @WrapOperation(method = "setupAnim(Lnet/minecraft/world/entity/Mob;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/AnimationUtils;animateZombieArms(Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;ZFF)V"))
    private void onSetupAnim(ModelPart leftArm, ModelPart rightArm, boolean isAggressive,
                             float attackTime, float ageInTicks, Operation<Void> original, @Local(argsOnly = true) T entity) {
        if (Reading.getOpenedBookHand(entity) != null) {
            return; // Stop zombie arms animation, so it wouldn't override ours.
        }

        original.call(leftArm, rightArm, isAggressive, attackTime, ageInTicks);
    }
}
