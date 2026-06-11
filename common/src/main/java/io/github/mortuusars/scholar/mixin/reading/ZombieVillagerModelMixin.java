package io.github.mortuusars.scholar.mixin.reading;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.mortuusars.scholar.Scholar;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.VillagerLikeModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.monster.zombie.ZombieVillagerModel;
import net.minecraft.client.renderer.entity.state.UndeadRenderState;
import net.minecraft.client.renderer.entity.state.ZombieVillagerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ZombieVillagerModel.class)
public abstract class ZombieVillagerModelMixin<S extends ZombieVillagerRenderState> extends HumanoidModel<S> implements VillagerLikeModel<S> {
    public ZombieVillagerModelMixin(ModelPart root) {
        super(root);
    }

    @WrapOperation(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/ZombieVillagerRenderState;)V",
          at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/AnimationUtils;animateZombieArms(Lnet/minecraft/client/model/geom/ModelPart;Lnet/minecraft/client/model/geom/ModelPart;ZLnet/minecraft/client/renderer/entity/state/UndeadRenderState;)V"))
    private void onSetupAnim(ModelPart leftArm, ModelPart rightArm, boolean isAggressive, UndeadRenderState renderState, Operation<Void> original) {
        if (renderState.getMainHandItemStack().has(Scholar.DataComponents.BOOK_OPEN)) {
            return; // Stop zombie arms animation, so it wouldn't override ours.
        }

        original.call(leftArm, rightArm, isAggressive, renderState);
    }
}
