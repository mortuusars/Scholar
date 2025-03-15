package io.github.mortuusars.scholar.fabric;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.renderer.VanillaModelEncoder;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A wrapper for chiseled bookshelf baked models that removes tinted quads when the corresponding chiseled bookshelf
 * slot contains an untinted item, allowing the vanilla book slot texture to show.
 */
public class ChiseledBookshelfBakedModelFabric extends ForwardingBakedModel {
    public static final Object2IntMap<ResourceLocation> CHISELED_BOOKSHELF_OCCUPIED_SLOTS = Util.make(new Object2IntArrayMap<>(),
            map -> {
                map.put(new ResourceLocation("block/chiseled_bookshelf_occupied_slot_top_left"), 0);
                map.put(new ResourceLocation("block/chiseled_bookshelf_occupied_slot_top_mid"), 1);
                map.put(new ResourceLocation("block/chiseled_bookshelf_occupied_slot_top_right"), 2);
                map.put(new ResourceLocation("block/chiseled_bookshelf_occupied_slot_bottom_left"), 3);
                map.put(new ResourceLocation("block/chiseled_bookshelf_occupied_slot_bottom_mid"), 4);
                map.put(new ResourceLocation("block/chiseled_bookshelf_occupied_slot_bottom_right"), 5);
            });

    private final Map<Direction, List<BakedQuad>> quadMap;
    private final int slot;

    public ChiseledBookshelfBakedModelFabric(BakedModel bakedModel, Map<Direction, List<BakedQuad>> quadMap, int slot) {
        this.wrapped = bakedModel;
        this.quadMap = quadMap;
        this.slot = slot;
    }

    public static BakedModel modifyModelAfterBake(@Nullable BakedModel bakedModel, ModelModifier.AfterBake.Context context) {
        if (bakedModel != null && CHISELED_BOOKSHELF_OCCUPIED_SLOTS.containsKey(context.id())) {
            Map<Direction, List<BakedQuad>> quadMap = new HashMap<>();
            getUntintedQuads(bakedModel, null, quadMap);
            for (Direction direction : Direction.values()) {
                getUntintedQuads(bakedModel, direction, quadMap);
            }
            return new ChiseledBookshelfBakedModelFabric(bakedModel,
                    quadMap,
                    CHISELED_BOOKSHELF_OCCUPIED_SLOTS.getInt(context.id()));
        } else {
            return bakedModel;
        }
    }

    static void getUntintedQuads(BakedModel bakedModel, @Nullable Direction direction, Map<Direction, List<BakedQuad>> quadMap) {
        List<BakedQuad> quads = new ArrayList<>(bakedModel.getQuads(null, direction, RandomSource.create()));
        quads.removeIf(BakedQuad::isTinted);
        quadMap.put(direction, quads);
    }

    @Override
    public List<BakedQuad> getQuads(BlockState blockState, Direction face, RandomSource rand) {
        return this.quadMap.get(face);
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        if (blockView.getBlockEntity(pos) instanceof ChiseledBookShelfBlockEntity blockEntity &&
                (blockEntity.getItem(this.slot).is(Items.WRITABLE_BOOK) ||  blockEntity.getItem(this.slot).is(Items.WRITTEN_BOOK))) {
            super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
        } else {
            VanillaModelEncoder.emitBlockQuads(this, state, randomSupplier, context, context.getEmitter());
        }
    }
}
