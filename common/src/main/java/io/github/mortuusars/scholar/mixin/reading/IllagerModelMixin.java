package io.github.mortuusars.scholar.mixin.reading;

import io.github.mortuusars.scholar.client.animation.ReadingAnimation;
import io.github.mortuusars.scholar.world.entity.Reading;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.AbstractIllager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IllagerModel.class)
public abstract class IllagerModelMixin<T extends AbstractIllager> extends HierarchicalModel<T> implements ArmedModel, HeadedModel {
    @Shadow
    @Final
    private ModelPart leftArm;

    @Shadow
    @Final
    private ModelPart rightArm;

    @Shadow
    @Final
    private ModelPart root;

    @Shadow
    @Final
    private ModelPart head;

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/monster/AbstractIllager;FFFFF)V", at = @At("RETURN"))
    private void onSetupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        InteractionHand hand = Reading.getOpenedBookHand(entity);
        if (hand != null && entity.getArmPose() != AbstractIllager.IllagerArmPose.CROSSED) {
            HumanoidArm openedBookArm = hand == InteractionHand.MAIN_HAND
                  ? entity.getMainArm()
                  : entity.getMainArm().getOpposite();
            ReadingAnimation.poseLeftArm(entity, leftArm, openedBookArm == HumanoidArm.LEFT);
            ReadingAnimation.poseRightArm(entity, rightArm, openedBookArm == HumanoidArm.LEFT);
            ReadingAnimation.poseHead(entity, ageInTicks, headPitch, hand, root, head);
        }
    }
}
