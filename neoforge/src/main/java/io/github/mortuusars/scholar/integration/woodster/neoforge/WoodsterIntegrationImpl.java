package io.github.mortuusars.scholar.integration.woodster.neoforge;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.salju.woodster.init.WoodsterBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WoodsterIntegrationImpl {
    public static Map<Block, IntList> DEFAULT_COLORS = new HashMap<>();

    static {
        DEFAULT_COLORS.put(WoodsterBlocks.ACACIA_CHISELED_BOOKSHELF.get(), IntList.of(0xFF4EE0D7, 0xFF4EE0D7, 0xFF4EE0D7, 0xFF4EE0D7, 0xFF4EE0D7, 0xFF4EE0D7));
        DEFAULT_COLORS.put(WoodsterBlocks.BAMBOO_CHISELED_BOOKSHELF.get(), IntList.of(0xFFFFFF9E, 0xFFFFFF9E, 0xFFFFFF9E, 0xFFFFFF9E, 0xFFFFFF9E, 0xFFFFFF9E));
        DEFAULT_COLORS.put(WoodsterBlocks.BIRCH_CHISELED_BOOKSHELF.get(), IntList.of(0xFF88D3DE, 0xFFCF8D8C, 0xFF88D3DE, 0xFFCF8D8C, 0xFF88D3DE, 0xFFCF8D8C));
        DEFAULT_COLORS.put(WoodsterBlocks.CHERRY_CHISELED_BOOKSHELF.get(), IntList.of(0xFFF55678, 0xFFF55678, 0xFFF55678, 0xFFF55678, 0xFFF55678, 0xFFF55678));
        DEFAULT_COLORS.put(WoodsterBlocks.CRIMSON_CHISELED_BOOKSHELF.get(), IntList.of(0xFF2B1A26, 0xFF545366, 0xFF2B1A26, 0xFF545366, 0xFF2B1A26, 0xFF545366));
        DEFAULT_COLORS.put(WoodsterBlocks.DARK_OAK_CHISELED_BOOKSHELF.get(), IntList.of(0xFF306EC5, 0xFFA743E5, 0xFFA743E5, 0xFFA743E5, 0xFF306EC5, 0xFF306EC5));
        DEFAULT_COLORS.put(WoodsterBlocks.JUNGLE_CHISELED_BOOKSHELF.get(), IntList.of(0xFF8CC647, 0xFF8CC647, 0xFF8CC647, 0xFF8CC647, 0xFF8CC647, 0xFF8CC647));
        DEFAULT_COLORS.put(WoodsterBlocks.MANGROVE_CHISELED_BOOKSHELF.get(), IntList.of(0xFF36BD48, 0xFF36BD48, 0xFF36BD48, 0xFF36BD48, 0xFF36BD48, 0xFF36BD48));
        DEFAULT_COLORS.put(WoodsterBlocks.PALE_OAK_CHISELED_BOOKSHELF.get(), IntList.of(0xFFDC4E17, 0xFFFF810D, 0xFFDC4E17, 0xFFFF810D, 0xFFDC4E17, 0xFFFF810D));
        DEFAULT_COLORS.put(WoodsterBlocks.SPRUCE_CHISELED_BOOKSHELF.get(), IntList.of(0xFF2B32FD, 0xFFFD2B4E, 0xFFFD2B4E, 0xFFFD2B4E, 0xFFFD2B4E, 0xFF2B32FD));
        DEFAULT_COLORS.put(WoodsterBlocks.WARPED_CHISELED_BOOKSHELF.get(), IntList.of(0xFF545366, 0xFF2B1A26, 0xFF545366, 0xFF2B1A26, 0xFF545366, 0xFF2B1A26));
    }

    public static List<Block> getChiseledBookshelves() {
        return List.of(
                WoodsterBlocks.ACACIA_CHISELED_BOOKSHELF.get(),
                WoodsterBlocks.BAMBOO_CHISELED_BOOKSHELF.get(),
                WoodsterBlocks.BIRCH_CHISELED_BOOKSHELF.get(),
                WoodsterBlocks.CHERRY_CHISELED_BOOKSHELF.get(),
                WoodsterBlocks.CRIMSON_CHISELED_BOOKSHELF.get(),
                WoodsterBlocks.DARK_OAK_CHISELED_BOOKSHELF.get(),
                WoodsterBlocks.JUNGLE_CHISELED_BOOKSHELF.get(),
                WoodsterBlocks.MANGROVE_CHISELED_BOOKSHELF.get(),
                WoodsterBlocks.PALE_OAK_CHISELED_BOOKSHELF.get(),
                WoodsterBlocks.SPRUCE_CHISELED_BOOKSHELF.get(),
                WoodsterBlocks.WARPED_CHISELED_BOOKSHELF.get()
        );
    }

    public static OptionalInt getDefaultTintColor(BlockState state, int slot) {
        @Nullable IntList colors = DEFAULT_COLORS.get(state.getBlock());
        if (colors == null) return OptionalInt.empty();
        return OptionalInt.of(colors.getInt(slot));
    }
}
