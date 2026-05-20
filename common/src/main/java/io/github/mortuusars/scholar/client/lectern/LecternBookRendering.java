package io.github.mortuusars.scholar.client.lectern;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.book.BookColor;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LecternBookRendering {
    public static final Material BOOK_COVER_LOCATION = new Material(InventoryMenu.BLOCK_ATLAS, Scholar.resource("block/lectern_book_cover"));
    public static final Material BOOK_PAGES_LOCATION = new Material(InventoryMenu.BLOCK_ATLAS, Scholar.resource("block/lectern_book_pages"));

    public static void render(BlockState blockState, LecternBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, BookModel bookModel) {
        if (blockEntity.getBook().isEmpty()) {
            return;
        }

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
