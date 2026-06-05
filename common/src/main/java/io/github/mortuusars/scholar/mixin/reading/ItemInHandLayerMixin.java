package io.github.mortuusars.scholar.mixin.reading;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.client.ColoredBookModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public abstract class ItemInHandLayerMixin<T extends LivingEntity, M extends EntityModel<T> & ArmedModel> extends RenderLayer<T, M> {
    @Unique
    private BookModel scholar$bookModel;

    public ItemInHandLayerMixin(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(RenderLayerParent<T, M> renderer, ItemInHandRenderer itemInHandRenderer, CallbackInfo ci) {
        scholar$bookModel = new BookModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.BOOK));
    }

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void onRenderArmWithItem(LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext,
                                     HumanoidArm arm, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (Config.Common.BOOK_READING_ANIMATION.get()) {
            if (stack.getTag() != null && stack.getTag().getBoolean(Scholar.NBT.BOOK_OPEN)) {
                M parentModel = getParentModel();
                ColoredBookModel.renderInHand(entity, stack, arm, poseStack, buffer, packedLight, parentModel, scholar$bookModel);
                ci.cancel();
            }

            if (Config.Common.BOOK_READING_HIDE_OFFHAND_ITEM.get()) {
                boolean isMainArm = arm == entity.getMainArm();
                ItemStack otherHandStack = isMainArm
                      ? entity.getOffhandItem()
                      : entity.getMainHandItem();

                if (otherHandStack.getTag() != null && otherHandStack.getTag().getBoolean(Scholar.NBT.BOOK_OPEN)) {
                    ci.cancel(); // Do not render item in hand if a book is held in another
                }
            }
        }
    }
}
