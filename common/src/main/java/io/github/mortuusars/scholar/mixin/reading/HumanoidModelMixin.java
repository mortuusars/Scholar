package io.github.mortuusars.scholar.mixin.reading;

import io.github.mortuusars.scholar.client.animation.ReadingAnimation;
import io.github.mortuusars.scholar.world.entity.Reading;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends HumanoidRenderState> extends EntityModel<T> implements ArmedModel<T>, HeadedModel {
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

    protected HumanoidModelMixin(ModelPart root) {
        super(root);
    }

    @Inject(method = "poseLeftArm", at = @At("HEAD"), cancellable = true)
    private void poseLeftArm(T renderState, CallbackInfo ci) {
        if (Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON
              && Minecraft.getInstance().getCameraEntity() != null
              && Minecraft.getInstance().getCameraEntity().getId() == ReadingAnimation.getEntityId(renderState)) {
            return;
        }

        @Nullable InteractionHand openedBookHand = Reading.getOpenedBookHand(renderState.leftHandItemStack,
              renderState.rightHandItemStack,
              renderState.mainArm == HumanoidArm.RIGHT);
        if (openedBookHand == null) {
            return;
        }

        ReadingAnimation.poseLeftArm(renderState, leftArm);
        ci.cancel();
    }

    @Inject(method = "poseRightArm", at = @At("HEAD"), cancellable = true)
    private void poseRightArm(T renderState, CallbackInfo ci) {
        if (Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON
              && Minecraft.getInstance().getCameraEntity() != null
              && Minecraft.getInstance().getCameraEntity().getId() == ReadingAnimation.getEntityId(renderState)) {
            return;
        }

        @Nullable InteractionHand openedBookHand = Reading.getOpenedBookHand(renderState.leftHandItemStack,
              renderState.rightHandItemStack,
              renderState.mainArm == HumanoidArm.RIGHT);
        if (openedBookHand == null) {
            return;
        }

        ReadingAnimation.poseRightArm(renderState, rightArm);
        ci.cancel();
    }

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V",
          at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/HumanoidModel;setupAttackAnimation(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V"))
    void onSetupAnim(T renderState, CallbackInfo ci) {
        @Nullable InteractionHand openedBookHand = Reading.getOpenedBookHand(renderState.leftHandItemStack,
              renderState.rightHandItemStack,
              renderState.mainArm == HumanoidArm.RIGHT);
        if (openedBookHand == null) {
            return;
        }

        ReadingAnimation.poseHead(renderState, body, head);
    }
}