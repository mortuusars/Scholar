package io.github.mortuusars.scholar.integration.woodworks;

import com.teamabnormals.blueprint.common.block.BlueprintChiseledBookShelfBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;

import java.util.OptionalInt;

public class WoodworksIntegration {
    public static OptionalInt getHitSlot(BlockState state, Vec2 hitPos) {
        if (state.getBlock() instanceof BlueprintChiseledBookShelfBlock block) {
            return OptionalInt.of(block.m_261279_(hitPos));
        }
        return OptionalInt.empty();
    }
}
