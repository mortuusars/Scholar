package io.github.mortuusars.scholar.mixin.reading;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.book.BookColor;
import io.github.mortuusars.scholar.client.BookRenderState;
import io.github.mortuusars.scholar.client.ColoredBookModel;
import io.github.mortuusars.scholar.client.lectern.ScholarBookHolderRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("rawtypes")
@Mixin(ItemInHandLayer.class)
public abstract class ItemInHandLayerMixin<S extends ArmedEntityRenderState, M extends EntityModel<S> & ArmedModel> extends RenderLayer<S, M> {
    @Unique
    private BookModel scholar$bookModel;

    public ItemInHandLayerMixin(RenderLayerParent<S, M> renderer) {
        super(renderer);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(RenderLayerParent<S, M> renderLayerParent, CallbackInfo ci) {
        scholar$bookModel = new BookModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.BOOK));
    }

    @Inject(method = "submitArmWithItem", at = @At("HEAD"), cancellable = true)
    private void onRenderArmWithItem(S entityRenderState, ItemStackRenderState stackRenderState, ItemStack stack,
                                     HumanoidArm arm, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
                                     int packedLight, CallbackInfo ci) {
        if (Config.Common.BOOK_READING_ANIMATION.get()
              && entityRenderState instanceof HumanoidRenderState humanoidRenderState
              && getParentModel() instanceof HumanoidModel<?> humanoidModel) {
            if (stack.has(Scholar.DataComponents.BOOK_OPEN)) {
                BookRenderState bookRenderState = new BookRenderState(stack.has(Scholar.DataComponents.BOOK_GOLDEN), BookColor.of(stack));
                ColoredBookModel.submitInHand(humanoidRenderState, bookRenderState,
                      stackRenderState, stack, arm, poseStack, submitNodeCollector, packedLight, humanoidModel, scholar$bookModel);
                ci.cancel();
            } else if (Config.Common.BOOK_READING_HIDE_OFFHAND_ITEM.get()) {
                boolean isInLeftHand = arm == HumanoidArm.LEFT;
                ItemStack otherHandStack = isInLeftHand
                      ? entityRenderState.rightHandItemStack
                      : entityRenderState.leftHandItemStack;

                if (otherHandStack.has(Scholar.DataComponents.BOOK_OPEN)) {
                    ci.cancel(); // Do not render item in hand if a book is held in another
                }
            }
        }
    }
}
