package io.github.mortuusars.scholar.integration.woodworks.forge;

import com.teamabnormals.woodworks.core.registry.WoodworksBlocks;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WoodworksIntegrationImpl {
    public static Map<Block, IntList> DEFAULT_COLORS = new HashMap<>();

    static {
        DEFAULT_COLORS.put(WoodworksBlocks.CHISELED_ACACIA_BOOKSHELF.get(), IntList.of(0xFFFF9E0B, 0xFF2FAEA6, 0xFFD43137, 0xFF4158CC, 0xFFDB5D0F, 0xFFFFC22A));
        DEFAULT_COLORS.put(WoodworksBlocks.CHISELED_BAMBOO_BOOKSHELF.get(), IntList.of(0xFFC86D5A, 0xFF8CB34A, 0xFF51C15F, 0xFF95AC1C, 0xFFBE8F20, 0xFFE15549));
        DEFAULT_COLORS.put(WoodworksBlocks.CHISELED_BIRCH_BOOKSHELF.get(), IntList.of(0xFF59ADB1, 0xFFC21C93, 0xFF6397C1, 0xFFD5740C, 0xFF5F42E9, 0xFFF23B09));
        DEFAULT_COLORS.put(WoodworksBlocks.CHISELED_CHERRY_BOOKSHELF.get(), IntList.of(0xFF8CAEFF, 0xFF9ADDE1, 0xFFD9DC8E, 0xFFFF8759, 0xFFE58FDE, 0xFFCDD8C3));
        DEFAULT_COLORS.put(WoodworksBlocks.CHISELED_CRIMSON_BOOKSHELF.get(), IntList.of(0xFF14B7BF, 0xFF8D33E8, 0xFFC58242, 0xFFE81752, 0xFFC443A2, 0xFF93BE33));
        DEFAULT_COLORS.put(WoodworksBlocks.CHISELED_DARK_OAK_BOOKSHELF.get(), IntList.of(0xFF4493C7, 0xFF7BB939, 0xFF725CCA, 0xFFEB2561, 0xFFCD7460, 0xFF945BC1));
        DEFAULT_COLORS.put(WoodworksBlocks.CHISELED_JUNGLE_BOOKSHELF.get(), IntList.of(0xFFCE2C00, 0xFFFF7309, 0xFFF97C12, 0xFF9C15DA, 0xFF353CD2, 0xFF2FB937));
        DEFAULT_COLORS.put(WoodworksBlocks.CHISELED_MANGROVE_BOOKSHELF.get(), IntList.of(0xFFD73F7C, 0xFFDE662A, 0xFFD17C1F, 0xFF9DA92E, 0xFF829B8C, 0xFFE4583E));
        DEFAULT_COLORS.put(WoodworksBlocks.CHISELED_SPRUCE_BOOKSHELF.get(), IntList.of(0xFFE46924, 0xFF58B567, 0xFF7DAB5D, 0xFF5F49F2, 0xFFD300B9, 0xFFEC001D));
        DEFAULT_COLORS.put(WoodworksBlocks.CHISELED_WARPED_BOOKSHELF.get(), IntList.of(0xFF6A64EA, 0xFFC66495, 0xFF72AD80, 0xFFC39732, 0xFFD17738, 0xFFCF2F4A));
    }

    public static List<Block> getChiseledBookshelves() {
        return List.of(
                WoodworksBlocks.CHISELED_ACACIA_BOOKSHELF.get(),
                WoodworksBlocks.CHISELED_BAMBOO_BOOKSHELF.get(),
                WoodworksBlocks.CHISELED_BIRCH_BOOKSHELF.get(),
                WoodworksBlocks.CHISELED_CHERRY_BOOKSHELF.get(),
                WoodworksBlocks.CHISELED_CRIMSON_BOOKSHELF.get(),
                WoodworksBlocks.CHISELED_DARK_OAK_BOOKSHELF.get(),
                WoodworksBlocks.CHISELED_JUNGLE_BOOKSHELF.get(),
                WoodworksBlocks.CHISELED_MANGROVE_BOOKSHELF.get(),
                WoodworksBlocks.CHISELED_SPRUCE_BOOKSHELF.get(),
                WoodworksBlocks.CHISELED_WARPED_BOOKSHELF.get()
        );
    }

    public static OptionalInt getDefaultTintColor(BlockState state, int slot) {
        @Nullable IntList colors = DEFAULT_COLORS.get(state.getBlock());
        if (colors == null) return OptionalInt.empty();
        return OptionalInt.of(colors.getInt(slot));
    }
}
