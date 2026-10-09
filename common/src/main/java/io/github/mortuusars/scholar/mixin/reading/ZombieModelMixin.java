package io.github.mortuusars.scholar.mixin.reading;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.mortuusars.scholar.Scholar;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.monster.zombie.ZombieModel;
import net.minecraft.client.renderer.entity.state.UndeadRenderState;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ZombieModel.class)
public abstract class ZombieModelMixin<S extends ZombieRenderState> extends HumanoidModel<S> {
    public ZombieModelMixin(ModelPart root) {
        super(root);
    }

    @WrapOperation(method = "setupAttackAnimation*",
          at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/AnimationUtils;animateZombieArms(Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;ZLnet/minecraft/client/renderer/entity/state/UndeadRenderState;)V"))
    private void onSetupAnim(ModelPart leftArm, ModelPart rightArm, boolean aggressive, UndeadRenderState state, Operation<Void> original) {
        if (state.getMainHandItemStack().has(Scholar.DataComponents.BOOK_OPEN)) {
            return; // Stop zombie arms animation, so it wouldn't override ours.
        }

        original.call(leftArm, rightArm, aggressive, state);
    }
}
