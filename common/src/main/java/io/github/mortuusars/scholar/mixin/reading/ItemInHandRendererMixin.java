package io.github.mortuusars.scholar.mixin.reading;

import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class ItemInHandRendererMixin {
//    @Unique
//    private @Nullable BookModel scholar$bookModel;


    // Doesn't worth the time to fix it. Maybe in the future.
//    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
//    private void renderItem(LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext,
//                            PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, CallbackInfo ci) {
//        if (Config.Common.BOOK_READING_ANIMATION.get() && stack.has(Scholar.DataComponents.BOOK_OPEN) && displayContext.firstPerson()) {
//            // Cannot use constructor to initialize book model due to
//            // (my guess) entity models not being available at the time of initialization.
//            if (scholar$bookModel == null) {
//                scholar$bookModel = new BookModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.BOOK));
//            }
//            ColoredBookModel.submitInFirstpersonHand(entity, stack, displayContext, poseStack, nodeCollector, packedLight, scholar$bookModel);
//            ci.cancel();
//        }
//    }
}
