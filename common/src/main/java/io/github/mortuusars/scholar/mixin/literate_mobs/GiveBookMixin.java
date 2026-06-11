package io.github.mortuusars.scholar.mixin.literate_mobs;

import io.github.mortuusars.scholar.world.entity.LiterateMobs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {Zombie.class, AbstractSkeleton.class, Piglin.class, ZombifiedPiglin.class, Pillager.class})
public class GiveBookMixin {
    @Inject(method = "populateDefaultEquipmentSlots", at = @At("RETURN"))
    private void onPopulateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty, CallbackInfo ci) {
        LiterateMobs.populateEquipmentSlots(random, difficulty, (Mob) (Object) this);
    }
}
