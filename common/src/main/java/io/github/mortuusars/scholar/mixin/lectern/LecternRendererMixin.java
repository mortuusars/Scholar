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
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
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
    private SpriteGetter sprites;

    @Shadow
    @Final
    private static BookModel.State BOOK_STATE;

    @Inject(method = "extractRenderState(Lnet/minecraft/world/level/block/entity/LecternBlockEntity;Lnet/minecraft/client/renderer/blockentity/state/LecternRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V",
          at = @At("RETURN"))
    private void onExtractRenderState(LecternBlockEntity blockEntity, LecternRenderState state, float partialTicks,
                                      Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress, CallbackInfo ci) {
        if (state instanceof ScholarBookHolderRenderState bookHolderState) {
            if (Config.Common.LECTERN_COLORED_BOOK_MODEL.get() && !blockEntity.getBook().isEmpty()) {
                bookHolderState.scholar$setBookRenderState(new BookRenderState(
                      blockEntity.getBook().has(Scholar.DataComponents.BOOK_GOLDEN),
                      BookColor.of(blockEntity.getBook())));
            } else {
                bookHolderState.scholar$setBookRenderState(BookRenderState.NO_BOOK);
            }
        }
    }

    @Inject(method = "submit(Lnet/minecraft/client/renderer/blockentity/state/LecternRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
          at = @At("HEAD"),
          cancellable = true)
    private void onSubmit(LecternRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
                          CameraRenderState camera, CallbackInfo ci) {
        if (Config.Common.LECTERN_COLORED_BOOK_MODEL.get()) {
            ColoredBookModel.submitOnLectern(state, poseStack, submitNodeCollector, camera, sprites, bookModel, BOOK_STATE);
            ci.cancel();
        }
    }
}
