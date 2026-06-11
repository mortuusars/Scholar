package io.github.mortuusars.scholar.mixin.reading;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.mortuusars.scholar.world.entity.Reading;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.monster.piglin.AbstractPiglinModel;
import net.minecraft.client.model.monster.piglin.PiglinModel;
import net.minecraft.client.renderer.entity.state.PiglinRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PiglinModel.class)
public abstract class PiglinModelMixin extends AbstractPiglinModel<PiglinRenderState> {
    public PiglinModelMixin(ModelPart modelPart) {
        super(modelPart);
    }

    @WrapOperation(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/PiglinRenderState;)V",
          at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/monster/piglin/PiglinModel;holdWeaponHigh(Lnet/minecraft/client/renderer/entity/state/PiglinRenderState;)V"))
    private void onSetupAnim(PiglinModel instance, PiglinRenderState renderState, Operation<Void> original) {
        @Nullable InteractionHand openedBookHand = Reading.getOpenedBookHand(renderState.leftHandItemStack,
              renderState.rightHandItemStack,
              renderState.mainArm == HumanoidArm.RIGHT);
        if (openedBookHand != null) {
            return; // Stop arms animation, so it wouldn't override ours.
        }

        original.call(instance, renderState);
    }
}
