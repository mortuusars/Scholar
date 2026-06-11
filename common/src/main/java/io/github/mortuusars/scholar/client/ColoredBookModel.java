package io.github.mortuusars.scholar.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.book.BookColor;
import io.github.mortuusars.scholar.client.animation.ReadingAnimation;
import io.github.mortuusars.scholar.client.lectern.ScholarBookHolderRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.model.monster.skeleton.SkeletonModel;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.LecternRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WritableBookItem;
import org.jetbrains.annotations.NotNull;

public class ColoredBookModel {
    public static final Material BOOK_COVER_LOCATION = Sheets.BLOCK_ENTITIES_MAPPER.apply(Scholar.resource("book_cover"));
    public static final Material BOOK_COVER_GOLDEN_LOCATION = Sheets.BLOCK_ENTITIES_MAPPER.apply(Scholar.resource("book_cover_golden"));
    public static final Material BOOK_PAGES_LOCATION = Sheets.BLOCK_ENTITIES_MAPPER.apply(Scholar.resource("book_pages"));

    public static void submitOnLectern(LecternRenderState lecternRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
                                       CameraRenderState cameraRenderState, MaterialSet materials, BookModel bookModel, BookModel.State bookState) {
        if (lecternRenderState instanceof ScholarBookHolderRenderState state && state.scholar$getBookRenderState().hasBook()) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 1.0625F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(-lecternRenderState.yRot));
            poseStack.mulPose(Axis.ZP.rotationDegrees(67.5F));
            poseStack.translate(0.0F, -0.125F, 0.0F);

            Material coverMaterial = getBookCoverMaterial(state.scholar$getBookRenderState());
            submitNodeCollector.submitModel(bookModel, bookState, poseStack,
                  coverMaterial.renderType(RenderTypes::entityCutout),
                  lecternRenderState.lightCoords,
                  OverlayTexture.NO_OVERLAY,
                  state.scholar$getBookRenderState().coverTintColor(),
                  materials.get(coverMaterial),
                  0,
                  lecternRenderState.breakProgress
            );

            Material pagesMaterial = getBookPagesMaterial(state.scholar$getBookRenderState());
            submitNodeCollector.submitModel(bookModel, bookState, poseStack,
                  pagesMaterial.renderType(RenderTypes::entityCutout),
                  lecternRenderState.lightCoords,
                  OverlayTexture.NO_OVERLAY,
                  0xFFFFFFFF,
                  materials.get(pagesMaterial),
                  0,
                  lecternRenderState.breakProgress
            );

            poseStack.popPose();
        }
    }

    @SuppressWarnings("rawtypes")
    public static <S extends HumanoidRenderState, M extends HumanoidModel & ArmedModel> void submitInHand(
          S humanoidRenderState, BookRenderState bookRenderState, ItemStackRenderState stackRenderState, ItemStack stack, HumanoidArm arm, PoseStack poseStack,
          SubmitNodeCollector submitNodeCollector, int packedLight, M entityModel, BookModel bookModel) {
        poseStack.pushPose();

        // We attach the book to the left hand, to use the right hand for page flipping animation (to not move the book with the arm).
        // Unless the entity is currently swinging - in that case we still use original arm for it, to "open" the book in the correct hand.
        if (humanoidRenderState.attackTime > 0) {
            entityModel.translateToHand(humanoidRenderState, arm, poseStack);
            poseStack.translate(arm == HumanoidArm.LEFT ? -0.285F : 0.285F, 0.625, -0.38);
        } else {
            entityModel.translateToHand(humanoidRenderState, HumanoidArm.LEFT, poseStack);
            poseStack.translate(-0.285F, 0.625, -0.38);
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(-90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-90));

        BookModel.State bookState = ReadingAnimation.createBookRenderState(humanoidRenderState, stack, arm, bookModel);

        ReadingAnimation.poseBook(humanoidRenderState, stack, arm, entityModel, bookModel);

        Material coverMaterial = getBookCoverMaterial(bookRenderState);
        submitNodeCollector.submitModel(bookModel, bookState, poseStack,
              coverMaterial.renderType(RenderTypes::entityCutout),
              packedLight,
              OverlayTexture.NO_OVERLAY,
              bookRenderState.coverTintColor(),
              Minecraft.getInstance().getAtlasManager().get(coverMaterial),
              0,
              null
        );

        Material pagesMaterial = getBookPagesMaterial(bookRenderState);
        submitNodeCollector.submitModel(bookModel, bookState, poseStack,
              pagesMaterial.renderType(RenderTypes::entityCutout),
              packedLight,
              OverlayTexture.NO_OVERLAY,
              0xFFFFFFFF,
              Minecraft.getInstance().getAtlasManager().get(pagesMaterial),
              0,
              null
        );

        poseStack.popPose();

        if (stack.getItem() instanceof WritableBookItem) {
            HumanoidArm mainArm = humanoidRenderState.mainArm;
            boolean isLeftArm = mainArm == HumanoidArm.LEFT;

            poseStack.pushPose();
            entityModel.translateToHand(humanoidRenderState, HumanoidArm.RIGHT, poseStack);
            poseStack.translate((float) (isLeftArm ? -1 : 1) / 16.0F - 0.05, 0.5F,
                  entityModel instanceof SkeletonModel<?> ? -0.15F : -0.22F);
            poseStack.mulPose(Axis.XP.rotationDegrees(150.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(40.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(-90));
            poseStack.scale(0.8f, 0.8f, 0.8f);

            ItemDisplayContext context = mainArm == HumanoidArm.LEFT
                  ? ItemDisplayContext.THIRD_PERSON_LEFT_HAND
                  : ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;

            ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
            Minecraft.getInstance().getItemModelResolver().updateForTopItem(itemStackRenderState,
                  new ItemStack(Items.FEATHER), context, null, null, 0);
            itemStackRenderState.submit(poseStack, submitNodeCollector, packedLight, OverlayTexture.NO_OVERLAY, 0);

            poseStack.popPose();
        }
    }

    public static void renderInFirstpersonHand(LivingEntity entity, ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
                                               SubmitNodeCollector submitNodeCollector, int packedLight, BookModel bookModel) {
        poseStack.pushPose();

        boolean isInMainHand = entity.getMainHandItem() == stack;
        HumanoidArm arm = isInMainHand
              ? entity.getMainArm()
              : entity.getMainArm().getOpposite();

        if (arm == HumanoidArm.LEFT) {
            poseStack.mulPose(Axis.YP.rotationDegrees(135));
            poseStack.mulPose(Axis.ZP.rotationDegrees(135));
        } else {
            poseStack.mulPose(Axis.YP.rotationDegrees(45));
            poseStack.mulPose(Axis.ZP.rotationDegrees(135));
        }

        BookRenderState bookRenderState = new BookRenderState(stack.has(Scholar.DataComponents.BOOK_GOLDEN), BookColor.of(stack));

        Material coverMaterial = getBookCoverMaterial(bookRenderState);
        submitNodeCollector.submitModel(bookModel, ReadingAnimation.BOOK_OPEN_STATE, poseStack,
              coverMaterial.renderType(RenderTypes::entityCutout),
              packedLight,
              OverlayTexture.NO_OVERLAY,
              bookRenderState.coverTintColor(),
              Minecraft.getInstance().getAtlasManager().get(coverMaterial),
              0,
              null
        );

        Material pagesMaterial = getBookPagesMaterial(bookRenderState);
        submitNodeCollector.submitModel(bookModel, ReadingAnimation.BOOK_OPEN_STATE, poseStack,
              pagesMaterial.renderType(RenderTypes::entityCutout),
              packedLight,
              OverlayTexture.NO_OVERLAY,
              0xFFFFFFFF,
              Minecraft.getInstance().getAtlasManager().get(pagesMaterial),
              0,
              null
        );

        poseStack.popPose();
    }

    public static @NotNull Material getBookCoverMaterial(BookRenderState renderState) {
        return renderState.isGolden()
              ? BOOK_COVER_GOLDEN_LOCATION
              : BOOK_COVER_LOCATION;
    }

    public static @NotNull Material getBookPagesMaterial(BookRenderState renderState) {
        return ColoredBookModel.BOOK_PAGES_LOCATION;
    }
}
