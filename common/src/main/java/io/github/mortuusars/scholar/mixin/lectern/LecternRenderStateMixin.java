package io.github.mortuusars.scholar.mixin.lectern;

import io.github.mortuusars.scholar.client.BookRenderState;
import io.github.mortuusars.scholar.client.lectern.ScholarBookHolderRenderState;
import net.minecraft.client.renderer.blockentity.state.LecternRenderState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;

@Mixin(LecternRenderState.class)
public class LecternRenderStateMixin implements ScholarBookHolderRenderState {
    @Unique
    private @NotNull BookRenderState scholar$bookRenderState = BookRenderState.NO_BOOK;

    @Override
    public @NotNull BookRenderState scholar$getBookRenderState() {
        return Objects.requireNonNull(scholar$bookRenderState, "ScholarBookRenderState was not initialized properly or called too early.");
    }

    @Override
    public void scholar$setBookRenderState(BookRenderState bookRenderState) {
        scholar$bookRenderState = bookRenderState;
    }
}
