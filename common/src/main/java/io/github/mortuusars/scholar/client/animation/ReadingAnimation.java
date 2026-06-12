package io.github.mortuusars.scholar.client.animation;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.client.ScholarHumanoidRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WritableBookItem;

public class ReadingAnimation {
    public static final BookModel.State BOOK_OPEN_STATE = BookModel.State.forAnimation(0.0F, 0.1F, 0.9F, 1.2F);

    public static <T extends HumanoidRenderState> void poseLeftArm(T renderState, ModelPart part) {
        part.xRot = part.xRot * 0.5F - (float) (Math.PI / 5);
        part.yRot = 0F;
        // Undo most of the bobbing:
        AnimationUtils.bobModelPart(part, renderState.ageInTicks, 0.8F);
    }

    public static <T extends HumanoidRenderState> void poseRightArm(T renderState, ModelPart part) {
        part.xRot = part.xRot * 0.5F - (float) (Math.PI / 5);
        part.yRot = 0F;

        // Undo original bobbing:
        AnimationUtils.bobModelPart(part, renderState.ageInTicks, -1F);
        // Apply bobbing in the opposite direction to match left arm:
        AnimationUtils.bobModelPart(part, renderState.ageInTicks, -0.2F);

        int entityId = getEntityId(renderState);

        // Page flip animation
        int perEntityOffset = 42 * entityId;
        float ageInTicks = renderState.ageInTicks + perEntityOffset;
        int lineDuration = getLineDuration(renderState);
        int spreadDuration = lineDuration * getLinesPerPage() * 2;
        float spreadProgress = ageInTicks % spreadDuration;
        float returnDuration = lineDuration * (1 - getForwardToReturnRatio());
        float timeUntilLastReturn = spreadDuration - returnDuration;
        if (spreadProgress > timeUntilLastReturn) {
            float progress = spreadProgress - timeUntilLastReturn;
            float anim = easeInOutSine(progress / returnDuration);
            if (anim > 0.5) {
                anim = 1 - anim;
            }

            part.yRot -= anim * 2;
            part.zRot += anim * 2;
            part.xRot -= anim * 2;
        }

//
//
//        @Nullable InteractionHand openedBookHand = Reading.getOpenedBookHand(renderState.leftHandItemStack,
//              renderState.rightHandItemStack,
//              renderState.mainArm == HumanoidArm.RIGHT);
//        if (openedBookHand == null) {
//            return;
//        }

        boolean isWriting = (renderState.rightHandItemStack.has(Scholar.DataComponents.BOOK_OPEN) && renderState.rightHandItemStack.getItem() instanceof WritableBookItem)
                || (renderState.leftHandItemStack.has(Scholar.DataComponents.BOOK_OPEN) && renderState.leftHandItemStack.getItem() instanceof WritableBookItem);

        // Writing animation
        if (isWriting) {
            // Up/down
            float anim = (ageInTicks % 6) / 3;
            if (anim > 1) {
                anim = 2 - anim;
            }
            anim = easeInOutSine(anim);
            part.xRot -= 0.5f + anim * 0.01f;

            // Side to side
            float scanningForwardDuration = lineDuration * getForwardToReturnRatio();
            float scanningReturnDuration = lineDuration - scanningForwardDuration;
            float time = ageInTicks % lineDuration;
            boolean isReturning = time >= scanningForwardDuration;
            anim = isReturning
                  ? 1f - ((time - scanningForwardDuration) / scanningReturnDuration)
                  : time / scanningForwardDuration;
            anim = easeInOutSine(anim);
            part.yRot -= 0.5f - anim * 0.5f;
            part.zRot -= anim * 0.1f;
        }
    }

    /**
     * Time it takes to "read" the line and return back.
     * <br>
     * Each entity has slightly different time to read.
     */
    public static <T extends HumanoidRenderState> int getLineDuration(T renderState) {
        int entityId = getEntityId(renderState);
        return 30 + ((entityId % 5) * 5);
    }

    public static int getLinesPerPage() {
        return 5;
    }

    /**
     * Percentage of time that would be spent "reading" the line versus going back to the beginning of a new line.
     */
    public static float getForwardToReturnRatio() {
        return 0.8f;
    }

    public static <T extends HumanoidRenderState> void poseHead(T renderState, ModelPart body, ModelPart head) {
        float amplitude = 0.25f; // How much head turns when reading the line
        float forwardToReturnRatio = getForwardToReturnRatio();
        float turnOffset = 0.05f; // Slight head rotation towards the current book side
        float pitchPerLine = 0.1f;

        int lineDuration = getLineDuration(renderState);
        int lines = getLinesPerPage();
        int pageDuration = lineDuration * lines;
        int spreadDuration = pageDuration * 2;

        int entityId = getEntityId(renderState);

        double age = renderState.ageInTicks + entityId * 42.0; // Animations should be unique per entity. Age would be similar after world load.
        float spreadTime = (float) (age % spreadDuration);
        float pageTime = spreadTime % pageDuration;
        float lineTime = pageTime % lineDuration;
        float forwardDuration = lineDuration * forwardToReturnRatio;
        float returnDuration = lineDuration - forwardDuration;

        // Yaw
        boolean isOnTheRightPage = spreadTime >= pageDuration;
        boolean isOnTheLastLine = pageTime >= pageDuration - lineDuration;
        boolean isReturning = lineTime >= forwardDuration;

        float scanningAnim = isReturning
              ? 1f - ((lineTime - forwardDuration) / returnDuration)
              : lineTime / forwardDuration;

        if (!isOnTheRightPage) {
            scanningAnim = 1 - scanningAnim;
        }

        scanningAnim = easeInOutSine(scanningAnim);

        float turn = isOnTheRightPage
              ? turnOffset + scanningAnim * amplitude
              : -turnOffset + -scanningAnim * amplitude;

        // Fixes jumping when transitioning to the other book side
        if (isReturning && isOnTheLastLine) {
            float target = isOnTheRightPage ? -turnOffset - amplitude : turnOffset;
            turn = Mth.lerp(isOnTheRightPage ? 1 - scanningAnim : scanningAnim, turn, target);
        }

        if (renderState.attackTime > 0) {
            float anim = easeInOutSine(renderState.attackTime);
            head.yRot = Mth.lerp(anim, head.yRot, body.yRot + turn);
        } else {
            head.yRot = body.yRot + turn;
        }

        // Pitch
        // Purpose of this is to pitch the head down after each line has been read, and return back to top.
        float min = 0.5f;
        float transitionPart = 1 - forwardToReturnRatio;

        float linesPassed = pageTime / lineDuration;

        int current = (int) linesPassed % lines;
        int next = (current + 1) % lines;

        float lineProgress = linesPassed - (int) linesPassed;

        float currentPitch = min + current * pitchPerLine;
        float nextPitch = min + next * pitchPerLine;

        float pitch;

        if (lineProgress < 1.0 - transitionPart) {
            pitch = currentPitch;
        } else {
            float t = easeInOutSine((lineProgress - (1f - transitionPart)) / transitionPart);
            pitch = Mth.lerp(t, currentPitch, nextPitch);
        }

        if (renderState.attackTime > 0) {
            float anim = easeInOutSine(renderState.attackTime);
            head.xRot = Mth.lerp(anim, head.yRot, pitch);
        } else {
            head.xRot = pitch;
        }
    }

    public static <S extends HumanoidRenderState> BookModel.State createBookRenderState(S renderState, ItemStack stack,
                                                                                        HumanoidArm arm, BookModel bookModel) {
        int perEntityOffset = 42 * getEntityId(renderState);
        float ageInTicks = renderState.ageInTicks + perEntityOffset;
        int lineDuration = getLineDuration(renderState);
        int spreadDuration = lineDuration * getLinesPerPage() * 2;
        float spreadProgress = ageInTicks % spreadDuration;
        float returnDuration = lineDuration * (1 - getForwardToReturnRatio());
        float timeUntilLastReturn = spreadDuration - returnDuration;

        // Flip the page on the last return
        if (spreadProgress > timeUntilLastReturn) {
            float progress = spreadProgress - timeUntilLastReturn;
            float anim = easeInOutSine(progress / returnDuration);
            float flip = anim < 0.9f
                  ? 0.9F - anim * 0.9f
                  : 0.9f + (1f - anim);
            return BookModel.State.forAnimation(0.0F, 0.1F, flip, 1.2F);
        } else {
            return BOOK_OPEN_STATE;
        }
    }

    @SuppressWarnings("rawtypes")
    public static <S extends HumanoidRenderState, M extends EntityModel<?> & ArmedModel> void poseBook(S renderState, ItemStack stack,
                                                                                                       HumanoidArm arm, M entityModel, BookModel bookModel) {
        int perEntityOffset = 42 * getEntityId(renderState);
        float ageInTicks = renderState.ageInTicks + perEntityOffset;
        int lineDuration = getLineDuration(renderState);
        int spreadDuration = lineDuration * getLinesPerPage() * 2;
        float spreadProgress = ageInTicks % spreadDuration;
        float returnDuration = lineDuration * (1 - getForwardToReturnRatio());
        float timeUntilLastReturn = spreadDuration - returnDuration;

        // Flip the page on the last return
        if (spreadProgress > timeUntilLastReturn) {
            float progress = spreadProgress - timeUntilLastReturn;
            float anim = easeInOutSine(progress / returnDuration);
            float flip = anim < 0.9f
                  ? 0.9F - anim * 0.9f
                  : 0.9f + (1f - anim);
            bookModel.setupAnim(BookModel.State.forAnimation(0.0F, 0.1F, flip, 1.2F));
        } else {
            bookModel.setupAnim(BOOK_OPEN_STATE);
        }
    }

    public static float easeInOutSine(float x) {
        return (float) (-(Math.cos(Math.PI * x) - 1) / 2);
    }

    public static float getPartialTick() {
        return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
    }

    public static <T extends HumanoidRenderState> int getEntityId(T renderState) {
        return renderState instanceof ScholarHumanoidRenderState state ? state.scholar$getEntityId() : 0;
    }
}
