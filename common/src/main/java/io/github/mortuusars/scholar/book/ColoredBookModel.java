package io.github.mortuusars.scholar.book;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.client.animation.ReadingAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ColoredBookModel {
    public static final Material BOOK_COVER_LOCATION =
          new Material(InventoryMenu.BLOCK_ATLAS, Scholar.resource("block/lectern_book_cover"));
    public static final Material BOOK_PAGES_LOCATION =
          new Material(InventoryMenu.BLOCK_ATLAS, Scholar.resource("block/lectern_book_pages"));

    public static void renderOnLectern(BlockState blockState, LecternBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                                       MultiBufferSource bufferSource, int packedLight, int packedOverlay, BookModel bookModel) {
        if (!blockEntity.getBook().isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 1.0625F, 0.5F);
            float f = blockState.getValue(LecternBlock.FACING).getClockWise().toYRot();
            poseStack.mulPose(Axis.YP.rotationDegrees(-f));
            poseStack.mulPose(Axis.ZP.rotationDegrees(67.5F));
            poseStack.translate(0.0F, -0.125F, 0.0F);
            bookModel.setupAnim(0.0F, 0.1F, 0.9F, 1.2F);
            VertexConsumer coverVertexConsumer = BOOK_COVER_LOCATION.buffer(bufferSource, RenderType::entityCutout);
            bookModel.render(poseStack, coverVertexConsumer, packedLight, packedOverlay, BookColor.of(blockEntity.getBook()));
            VertexConsumer pagesVertexConsumer = BOOK_PAGES_LOCATION.buffer(bufferSource, RenderType::entityCutout);
            bookModel.render(poseStack, pagesVertexConsumer, packedLight, packedOverlay, -1);
            poseStack.popPose();
        }
    }

    public static <M extends EntityModel<?> & ArmedModel> void renderInHand(LivingEntity entity, ItemStack stack, HumanoidArm arm,
                                                                            PoseStack poseStack, MultiBufferSource buffer,
                                                                            int packedLight, M entityModel, BookModel bookModel) {
        poseStack.pushPose();

        // We attach the book to the left hand, to use the right hand for page flipping animation (to not move the book with the arm).
        // Unless the entity is currently swinging - in that case we still use original arm for it, to "open" the book in the correct hand.
        if (entity.swinging) {
            entityModel.translateToHand(arm, poseStack);
            poseStack.translate(arm == HumanoidArm.LEFT ? -0.285F : 0.285F, 0.625, -0.38);
        } else {
            entityModel.translateToHand(HumanoidArm.LEFT, poseStack);
            poseStack.translate(-0.285F, 0.625, -0.38);
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(-90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-90));

        ReadingAnimation.poseBook(entity, stack, arm, entityModel, bookModel);

        VertexConsumer coverVertexConsumer = ColoredBookModel.BOOK_COVER_LOCATION.buffer(buffer, RenderType::entityCutout);
        bookModel.render(poseStack, coverVertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, BookColor.of(stack));
        VertexConsumer pagesVertexConsumer = ColoredBookModel.BOOK_PAGES_LOCATION.buffer(buffer, RenderType::entityCutout);
        bookModel.render(poseStack, pagesVertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        poseStack.popPose();

        if (stack.getItem() instanceof WritableBookItem) {
            HumanoidArm mainArm = entity.getMainArm();
            boolean isLeftArm = mainArm == HumanoidArm.LEFT;

            poseStack.pushPose();
            entityModel.translateToHand(mainArm, poseStack);
            poseStack.translate((float)(isLeftArm ? -1 : 1) / 16.0F - 0.05, 0.5F,
                  entityModel instanceof SkeletonModel<?> ? -0.15F : -0.22F);
            poseStack.mulPose(Axis.XP.rotationDegrees(150.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(40.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(-90));
            poseStack.scale(0.8f, 0.8f, 0.8f);

            ItemDisplayContext context = mainArm == HumanoidArm.LEFT
                  ? ItemDisplayContext.THIRD_PERSON_LEFT_HAND
                  : ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
            Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer()
                  .renderItem(entity, new ItemStack(Items.FEATHER), context, isLeftArm, poseStack, buffer, packedLight);
            poseStack.popPose();
        }
    }

    public static void renderInFirstpersonHand(LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext, boolean leftHand,
                                               PoseStack poseStack, MultiBufferSource buffer, int packedLight, BookModel bookModel) {
        poseStack.pushPose();

        if (leftHand) {
            poseStack.mulPose(Axis.YP.rotationDegrees(135));
            poseStack.mulPose(Axis.ZP.rotationDegrees(135));
        } else {
            poseStack.mulPose(Axis.YP.rotationDegrees(45));
            poseStack.mulPose(Axis.ZP.rotationDegrees(135));
        }

        bookModel.setupAnim(0.0F, 0.1F, 0.9F, 1.2F);

        VertexConsumer coverVertexConsumer = ColoredBookModel.BOOK_COVER_LOCATION.buffer(buffer, RenderType::entityCutout);
        bookModel.render(poseStack, coverVertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, BookColor.of(stack));
        VertexConsumer pagesVertexConsumer = ColoredBookModel.BOOK_PAGES_LOCATION.buffer(buffer, RenderType::entityCutout);
        bookModel.render(poseStack, pagesVertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        poseStack.popPose();
    }
}
