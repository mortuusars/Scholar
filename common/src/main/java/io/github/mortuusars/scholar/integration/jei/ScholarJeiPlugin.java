package io.github.mortuusars.scholar.integration.jei;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.integration.jei.recipe.NbtTransferringShapelessExtension;
import io.github.mortuusars.scholar.recipe.NbtTransferringRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class ScholarJeiPlugin implements IModPlugin {
    public static final ResourceLocation ID = Scholar.resource("jei_plugin");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory()
                .addCategoryExtension(NbtTransferringRecipe.class, NbtTransferringShapelessExtension::new);
    }
}