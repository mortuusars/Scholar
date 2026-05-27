package io.github.mortuusars.scholar.client.animation;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WritableBookItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReadingAnimation {
    public static @Nullable InteractionHand getOpenedBookHand(LivingEntity entity) {
        if (!Config.Common.BOOK_READING_ANIMATION.get()) return null;
        if (entity instanceof EnderMan) return null; // They cannot hold items as other mobs do.
        if (entity.getMainHandItem().has(Scholar.DataComponents.BOOK_OPEN)) return InteractionHand.MAIN_HAND;
        if (entity.getOffhandItem().has(Scholar.DataComponents.BOOK_OPEN)) return InteractionHand.OFF_HAND;
        return null;
    }

    public static <T extends LivingEntity> void poseLeftArm(T entity, ModelPart part, boolean isHoldingArm) {
        part.xRot = part.xRot * 0.5F - (float) (Math.PI / 5);
        part.yRot = 0F;
        // Undo most of the bobbing:
        AnimationUtils.bobModelPart(part, entity.tickCount + getPartialTick(), 0.8F);
    }

    public static <T extends LivingEntity> void poseRightArm(T entity, ModelPart part, boolean isHoldingArm) {
        part.xRot = part.xRot * 0.5F - (float) (Math.PI / 5);
        part.yRot = 0F;

        // Undo original bobbing:
        AnimationUtils.bobModelPart(part, entity.tickCount + getPartialTick(), -1F);
        // Apply bobbing in the opposite direction to match left arm:
        AnimationUtils.bobModelPart(part, entity.tickCount + getPartialTick(), -0.2F);

        // Page flip animation
        int perEntityOffset = 42 * entity.getId();
        float ageInTicks = entity.tickCount + getPartialTick() + perEntityOffset;
        int lineDuration = getLineDuration(entity);
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

        // Writing animation
        if (getOpenedBookHand(entity) instanceof InteractionHand hand
              && entity.getItemInHand(hand).getItem() instanceof WritableBookItem) {
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
    public static int getLineDuration(LivingEntity entity) {
        return 30 + ((entity.getId() % 5) * 5);
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

    public static <T extends LivingEntity> void poseHead(T entity, float ageInTicks, float headPitch,
                                                         @NotNull InteractionHand openedBookHand, ModelPart body, ModelPart head) {
        float amplitude = 0.25f; // How much head turns when reading the line
        float forwardToReturnRatio = getForwardToReturnRatio();
        float turnOffset = 0.05f; // Slight head rotation towards the current book side
        float pitchPerLine = 0.1f;

        int lineDuration = getLineDuration(entity);
        int lines = getLinesPerPage();
        int pageDuration = lineDuration * lines;
        int spreadDuration = pageDuration * 2;

        double age = ageInTicks + entity.getId() * 42.0; // Animations should be unique per entity. Age would be similar after world load.
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

        if (entity.attackAnim > 0) {
            float anim = easeInOutSine(entity.getAttackAnim(getPartialTick()));
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

        if (entity.attackAnim > 0) {
            float anim = easeInOutSine(entity.getAttackAnim(getPartialTick()));
            head.xRot = Mth.lerp(anim, head.yRot, pitch);
        } else {
            head.xRot = pitch;
        }
    }

    public static <M extends EntityModel<?> & ArmedModel> void poseBook(LivingEntity entity, ItemStack stack,
                                                                        HumanoidArm arm, M entityModel, BookModel bookModel) {
        int perEntityOffset = 42 * entity.getId();
        float ageInTicks = entity.tickCount + getPartialTick() + perEntityOffset;
        int lineDuration = getLineDuration(entity);
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
            bookModel.setupAnim(0.0F, 0.1F, flip, 1.2F);
        } else {
            bookModel.setupAnim(0.0F, 0.1F, 0.9F, 1.2F);
        }
    }

    public static float easeInOutSine(float x) {
        return (float) (-(Math.cos(Math.PI * x) - 1) / 2);
    }

    public static float getPartialTick() {
        return Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
    }
}
