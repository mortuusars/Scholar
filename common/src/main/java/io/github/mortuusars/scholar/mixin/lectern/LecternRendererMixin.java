package io.github.mortuusars.scholar.mixin.lectern;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.book.BookColor;
import io.github.mortuusars.scholar.client.BookRenderState;
import io.github.mortuusars.scholar.client.ColoredBookModel;
import io.github.mortuusars.scholar.client.lectern.ScholarBookHolderRenderState;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.LecternRenderer;
import net.minecraft.client.renderer.blockentity.state.LecternRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.phys.Vec3;
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

    @Shadow
    @Final
    private MaterialSet materials;

    @Shadow
    @Final
    private BookModel.State bookState;

    @Inject(method = "extractRenderState(Lnet/minecraft/world/level/block/entity/LecternBlockEntity;Lnet/minecraft/client/renderer/blockentity/state/LecternRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
          at = @At("RETURN"))
    private void onExtractRenderState(LecternBlockEntity lecternBlockEntity, LecternRenderState lecternRenderState, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, CallbackInfo ci) {
        if (lecternRenderState instanceof ScholarBookHolderRenderState state) {
            if (Config.Common.LECTERN_COLORED_BOOK_MODEL.get() && !lecternBlockEntity.getBook().isEmpty()) {
                state.scholar$setBookRenderState(new BookRenderState(
                      lecternBlockEntity.getBook().has(Scholar.DataComponents.BOOK_GOLDEN),
                      BookColor.of(lecternBlockEntity.getBook())));
            } else {
                state.scholar$setBookRenderState(BookRenderState.NO_BOOK);
            }
        }
    }

    @Inject(method = "submit(Lnet/minecraft/client/renderer/blockentity/state/LecternRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
          at = @At("HEAD"),
          cancellable = true)
    private void onSubmit(LecternRenderState lecternRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
                          CameraRenderState cameraRenderState, CallbackInfo ci) {
        if (Config.Common.LECTERN_COLORED_BOOK_MODEL.get()) {
            ColoredBookModel.submitOnLectern(lecternRenderState, poseStack, submitNodeCollector, cameraRenderState, materials, bookModel, bookState);
            ci.cancel();
        }
    }
}
