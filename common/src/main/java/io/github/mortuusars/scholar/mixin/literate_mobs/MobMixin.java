package io.github.mortuusars.scholar.mixin.literate_mobs;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.world.entity.ReadBookGoal;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity implements EquipmentUser, Leashable, Targeting {
    @Shadow
    public abstract void setDropChance(EquipmentSlot slot, float percent);

    @Shadow
    @Final
    protected GoalSelector goalSelector;

    protected MobMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;registerGoals()V"))
    private void onRegisterGoals(Mob instance, Operation<Void> original) {
        original.call(instance);
        if (is(Scholar.Tags.EntityTypes.LITERATE)) {
            goalSelector.addGoal(ReadBookGoal.getPriority(instance), new ReadBookGoal((Mob) (Object) this));
        }
    }
}
