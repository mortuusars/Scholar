package io.github.mortuusars.scholar.mixin;

import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.WritableBookItem;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Adds DyeableLeatherItem interface to writable books. This makes it handle most of the coloring stuff automatically.
 */
@Mixin(WritableBookItem.class)
public abstract class WritableBookItemMixin implements DyeableLeatherItem {

}
