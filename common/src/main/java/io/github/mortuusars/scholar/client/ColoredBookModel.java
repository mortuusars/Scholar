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
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WritableBookItem;
import org.jetbrains.annotations.NotNull;

public class ColoredBookModel {
    public static final SpriteId BOOK_COVER_SPRITE = Sheets.BLOCK_ENTITIES_MAPPER.apply(Scholar.identifier("book_cover"));
    public static final SpriteId BOOK_COVER_GOLDEN_SPRITE = Sheets.BLOCK_ENTITIES_MAPPER.apply(Scholar.identifier("book_cover_golden"));
    public static final SpriteId BOOK_PAGES_LOCATION = Sheets.BLOCK_ENTITIES_MAPPER.apply(Scholar.identifier("book_pages"));

    public static void submitOnLectern(LecternRenderState lecternRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
                                       CameraRenderState camera, SpriteGetter sprites, BookModel bookModel, BookModel.State bookState) {
        if (lecternRenderState instanceof ScholarBookHolderRenderState state && state.scholar$getBookRenderState().hasBook()) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 1.0625F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(-lecternRenderState.yRot));
            poseStack.mulPose(Axis.ZP.rotationDegrees(67.5F));
            poseStack.translate(0.0F, -0.125F, 0.0F);

            SpriteId coverSprite = getBookCoverSprite(state.scholar$getBookRenderState());
            submitNodeCollector.submitModel(bookModel, bookState, poseStack,
                  coverSprite.renderType(RenderTypes::entityCutout),
                  lecternRenderState.lightCoords,
                  OverlayTexture.NO_OVERLAY,
                  state.scholar$getBookRenderState().coverTintColor(),
                  sprites.get(coverSprite),
                  0,
                  lecternRenderState.breakProgress
            );

            SpriteId pagesSprite = getBookPagesSprite(state.scholar$getBookRenderState());
            submitNodeCollector.submitModel(bookModel, bookState, poseStack,
                  pagesSprite.renderType(RenderTypes::entityCutout),
                  lecternRenderState.lightCoords,
                  OverlayTexture.NO_OVERLAY,
                  0xFFFFFFFF,
                  sprites.get(pagesSprite),
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

        SpriteId coverSprite = getBookCoverSprite(bookRenderState);
        submitNodeCollector.submitModel(bookModel, bookState, poseStack,
              coverSprite.renderType(RenderTypes::entityCutout),
              packedLight,
              OverlayTexture.NO_OVERLAY,
              bookRenderState.coverTintColor(),
              Minecraft.getInstance().getAtlasManager().get(coverSprite),
              0,
              null
        );

        SpriteId pagesSprite = getBookPagesSprite(bookRenderState);
        submitNodeCollector.submitModel(bookModel, bookState, poseStack,
              pagesSprite.renderType(RenderTypes::entityCutout),
              packedLight,
              OverlayTexture.NO_OVERLAY,
              0xFFFFFFFF,
              Minecraft.getInstance().getAtlasManager().get(pagesSprite),
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

        SpriteId coverSprite = getBookCoverSprite(bookRenderState);
        submitNodeCollector.submitModel(bookModel, ReadingAnimation.BOOK_OPEN_STATE, poseStack,
              coverSprite.renderType(RenderTypes::entityCutout),
              packedLight,
              OverlayTexture.NO_OVERLAY,
              bookRenderState.coverTintColor(),
              Minecraft.getInstance().getAtlasManager().get(coverSprite),
              0,
              null
        );

        SpriteId pagesSprite = getBookPagesSprite(bookRenderState);
        submitNodeCollector.submitModel(bookModel, ReadingAnimation.BOOK_OPEN_STATE, poseStack,
              pagesSprite.renderType(RenderTypes::entityCutout),
              packedLight,
              OverlayTexture.NO_OVERLAY,
              0xFFFFFFFF,
              Minecraft.getInstance().getAtlasManager().get(pagesSprite),
              0,
              null
        );

        poseStack.popPose();
    }

    public static @NotNull SpriteId getBookCoverSprite(BookRenderState renderState) {
        return renderState.isGolden()
              ? BOOK_COVER_GOLDEN_SPRITE
              : BOOK_COVER_SPRITE;
    }

    public static @NotNull SpriteId getBookPagesSprite(BookRenderState renderState) {
        return ColoredBookModel.BOOK_PAGES_LOCATION;
    }
}
