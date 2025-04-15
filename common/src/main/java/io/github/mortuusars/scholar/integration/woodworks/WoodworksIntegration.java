package io.github.mortuusars.scholar.integration.woodworks;

//import com.teamabnormals.blueprint.common.block.BlueprintChiseledBookShelfBlock;
//import dev.architectury.injectables.annotations.ExpectPlatform;
//import io.github.mortuusars.scholar.Scholar;
//import io.github.mortuusars.scholar.client.render.ChiseledBookShelf;
//import net.minecraft.client.color.block.BlockColor;
//import net.minecraft.client.renderer.RenderType;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.phys.Vec2;
//
//import java.util.List;
//import java.util.OptionalInt;
//import java.util.function.BiConsumer;

public class WoodworksIntegration {
//    public static void registerBlockColors(BiConsumer<BlockColor, Block> consumer) {
//        Scholar.LOGGER.info("Registering chiseled bookshelf block colors to 'Woodworks'. " +
//                "If Woodworks bookshelves displaying wrongly - try without Scholar installed, and if incompatibility " +
//                "is confirmed - report to Scholar github.");
//
//        getChiseledBookshelves().forEach(block -> {
//            consumer.accept(ChiseledBookShelf::getSlotTintColor, block);
//        });
//    }
//
//    public static void setRenderLayer(BiConsumer<Block, RenderType> consumer) {
//        Scholar.LOGGER.info("Setting 'cutout' render type for 'Woodworks'. " +
//                "If Woodworks bookshelves displaying wrongly - try without Scholar installed, and if incompatibility " +
//                "is confirmed - report to Scholar github.");
//
//        getChiseledBookshelves().forEach(block -> {
//            consumer.accept(block, RenderType.cutout());
//        });
//    }
//
//    @ExpectPlatform
//    public static List<Block> getChiseledBookshelves() {
//        throw new AssertionError();
//    }
//
//    @ExpectPlatform
//    public static OptionalInt getDefaultTintColor(BlockState state, int slot) {
//        throw new AssertionError();
//    }
//
//    public static OptionalInt getHitSlot(BlockState state, Vec2 hitPos) {
//        if (state.getBlock() instanceof BlueprintChiseledBookShelfBlock block) {
//            return OptionalInt.of(block.m_261279_(hitPos));
//        }
//        return OptionalInt.empty();
//    }
}
