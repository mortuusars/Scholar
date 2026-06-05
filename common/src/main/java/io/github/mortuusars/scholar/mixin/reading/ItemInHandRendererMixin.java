package io.github.mortuusars.scholar.mixin.reading;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.client.ColoredBookModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Unique
    private @Nullable BookModel scholar$bookModel;

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void renderItem(LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext, boolean leftHand,
                            PoseStack poseStack, MultiBufferSource buffer, int seed, CallbackInfo ci) {
        if (Config.Common.BOOK_READING_ANIMATION.get()
              && stack.getTag() != null && stack.getTag().getBoolean(Scholar.NBT.BOOK_OPEN)
              && displayContext.firstPerson()) {
            // Cannot use constructor to initialize book model due to
            // (my guess) entity models not being available at the time of initialization.
            if (scholar$bookModel == null) {
                scholar$bookModel = new BookModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.BOOK));
            }
            ColoredBookModel.renderInFirstpersonHand(entity, stack, displayContext, leftHand, poseStack, buffer, seed, scholar$bookModel);
            ci.cancel();
        }
    }
}
