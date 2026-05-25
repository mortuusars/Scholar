package io.github.mortuusars.scholar.mixin.reading;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.mortuusars.scholar.client.animation.ReadingPose;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.VillagerHeadModel;
import net.minecraft.client.model.ZombieVillagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ZombieVillagerModel.class)
public abstract class ZombieVillagerModelMixin<T extends Zombie> extends HumanoidModel<T> implements VillagerHeadModel {
    public ZombieVillagerModelMixin(ModelPart root) {
        super(root);
    }

    @WrapOperation(method = "setupAnim(Lnet/minecraft/world/entity/monster/Zombie;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/AnimationUtils;animateZombieArms(Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;ZFF)V"))
    private void onSetupAnim(ModelPart leftArm, ModelPart rightArm, boolean isAggressive,
                             float attackTime, float ageInTicks, Operation<Void> original, @Local(argsOnly = true) T entity) {
        if (ReadingPose.getOpenedBookHand(entity) != null) {
            return; // Stop zombie arms animation, so it wouldn't override ours.
        }

        original.call(leftArm, rightArm, isAggressive, attackTime, ageInTicks);
    }
}
