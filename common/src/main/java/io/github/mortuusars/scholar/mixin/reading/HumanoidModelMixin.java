package io.github.mortuusars.scholar.mixin.reading;

import io.github.mortuusars.scholar.client.animation.ReadingAnimation;
import io.github.mortuusars.scholar.world.entity.Reading;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> extends AgeableListModel<T> {
    @Shadow
    @Final
    public ModelPart leftArm;
    @Shadow
    @Final
    public ModelPart rightArm;

    @Shadow
    @Final
    public ModelPart head;

    @Shadow
    @Final
    public ModelPart body;

    @Inject(method = "poseLeftArm", at = @At("HEAD"), cancellable = true)
    private void poseLeftArm(T entity, CallbackInfo ci) {
        if (entity == Minecraft.getInstance().cameraEntity
              && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) {
            return;
        }

        @Nullable InteractionHand openedBookHand = Reading.getOpenedBookHand(entity);
        if (openedBookHand == null) {
            return;
        }

        HumanoidArm openedBookArm = openedBookHand == InteractionHand.MAIN_HAND
              ? entity.getMainArm()
              : entity.getMainArm().getOpposite();
        boolean isHoldingArm = openedBookArm == HumanoidArm.LEFT;
        ReadingAnimation.poseLeftArm(entity, leftArm, isHoldingArm);
        ci.cancel();
    }

    @Inject(method = "poseRightArm", at = @At("HEAD"), cancellable = true)
    private void poseRightArm(T entity, CallbackInfo ci) {
        if (entity == Minecraft.getInstance().cameraEntity
              && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) {
            return;
        }

        @Nullable InteractionHand openedBookHand = Reading.getOpenedBookHand(entity);
        if (openedBookHand == null) {
            return;
        }

        HumanoidArm openedBookArm = openedBookHand == InteractionHand.MAIN_HAND
              ? entity.getMainArm()
              : entity.getMainArm().getOpposite();
        boolean isHoldingArm = openedBookArm == HumanoidArm.RIGHT;
        ReadingAnimation.poseRightArm(entity, rightArm, isHoldingArm);
        ci.cancel();
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/HumanoidModel;setupAttackAnimation(Lnet/minecraft/world/entity/LivingEntity;F)V"))
    void onSetupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        @Nullable InteractionHand openedBookHand = Reading.getOpenedBookHand(entity);
        if (openedBookHand == null) {
            return;
        }

        ReadingAnimation.poseHead(entity, ageInTicks, headPitch, openedBookHand, body, head);
    }
}