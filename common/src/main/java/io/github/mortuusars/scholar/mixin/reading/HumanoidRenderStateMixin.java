package io.github.mortuusars.scholar.mixin.reading;

import io.github.mortuusars.scholar.client.BookRenderState;
import io.github.mortuusars.scholar.client.ScholarHumanoidRenderState;
import io.github.mortuusars.scholar.client.lectern.ScholarBookHolderRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(HumanoidRenderState.class)
public abstract class HumanoidRenderStateMixin implements ScholarHumanoidRenderState, ScholarBookHolderRenderState {
    @Unique
    private int scholar$entityId;
    @Unique
    private @NotNull BookRenderState scholar$bookRenderState = BookRenderState.NO_BOOK;

    @Override
    public int scholar$getEntityId() {
        return scholar$entityId;
    }

    @Override
    public void scholar$setEntityId(int id) {
        scholar$entityId = id;
    }

    @Override
    public BookRenderState scholar$getBookRenderState() {
        return scholar$bookRenderState;
    }

    @Override
    public void scholar$setBookRenderState(@NotNull BookRenderState bookRenderState) {
        scholar$bookRenderState = bookRenderState;
    }
}
