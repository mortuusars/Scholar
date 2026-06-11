package io.github.mortuusars.scholar.mixin.reading;

import io.github.mortuusars.scholar.client.ScholarHumanoidRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("deprecation")
@Mixin(HumanoidMobRenderer.class)
public abstract class HumanoidRendererMixin <T extends Mob, S extends HumanoidRenderState, M extends HumanoidModel<S>> extends AgeableMobRenderer<T, S, M> {
    public HumanoidRendererMixin(EntityRendererProvider.Context context, M adultModel, M babyModel, float scale) {
        super(context, adultModel, babyModel, scale);
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Mob;Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;F)V",
    at = @At("RETURN"))
    private void onExtractRenderState(T mob, S humanoidRenderState, float f, CallbackInfo ci) {
        if (humanoidRenderState instanceof ScholarHumanoidRenderState state) {
            state.scholar$setEntityId(mob.getId());
        }
    }
}
