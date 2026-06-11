package io.github.mortuusars.scholar.mixin.reading;

import io.github.mortuusars.scholar.client.animation.ReadingAnimation;
import io.github.mortuusars.scholar.world.entity.Reading;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.monster.illager.IllagerModel;
import net.minecraft.client.renderer.entity.state.IllagerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IllagerModel.class)
public abstract class IllagerModelMixin<S extends IllagerRenderState> extends EntityModel<S> implements ArmedModel<S>, HeadedModel {
    @Shadow
    @Final
    private ModelPart leftArm;

    @Shadow
    @Final
    private ModelPart rightArm;

    @Shadow
    @Final
    private ModelPart head;

    protected IllagerModelMixin(ModelPart root) {
        super(root);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/IllagerRenderState;)V", at = @At("RETURN"))
    private void onSetupAnim(S renderState, CallbackInfo ci) {
        @Nullable InteractionHand openedBookHand = Reading.getOpenedBookHand(renderState.leftHandItemStack,
              renderState.rightHandItemStack,
              renderState.mainArm == HumanoidArm.RIGHT);
        if (openedBookHand != null && renderState.armPose != AbstractIllager.IllagerArmPose.CROSSED) {
            ReadingAnimation.poseLeftArm(renderState, leftArm);
            ReadingAnimation.poseRightArm(renderState, rightArm);
            ReadingAnimation.poseHead(renderState, root.getChild("body"), head);
        }
    }
}
