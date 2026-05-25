package io.github.mortuusars.scholar.mixin.lectern;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.book.ColoredBookModel;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.LecternRenderer;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LecternRenderer.class)
public class LecternRendererMixin {
    @Shadow
    @Final
    private BookModel bookModel;

    @Inject(method = "render(Lnet/minecraft/world/level/block/entity/LecternBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
          at = @At("HEAD"),
          cancellable = true)
    private void onRender(LecternBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                          MultiBufferSource bufferSource, int packedLight, int packedOverlay, CallbackInfo ci) {
        if (Config.Common.LECTERN_COLORED_BOOK_MODEL.get()) {
            ColoredBookModel.renderOnLectern(blockEntity.getBlockState(), blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay, bookModel);
            ci.cancel();
        }
    }
}
