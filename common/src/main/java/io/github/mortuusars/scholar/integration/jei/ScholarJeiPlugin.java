package io.github.mortuusars.scholar.integration.jei;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.*;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class ScholarJeiPlugin implements IModPlugin {
    private static final Identifier ID = Scholar.resource("jei_plugin");

    @Override
    public @NotNull Identifier getPluginUid() {
        return ID;
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        if (!Config.Common.JEI_DYEING_RECIPES.get()) {
            return;
        }

        List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();

        for (DyeColor color : DyeColor.values()) {
            for (Holder<Item> itemHolder : BuiltInRegistries.ITEM.getTagOrEmpty(ItemTags.DYEABLE)) {
                if (Config.Common.JEI_DYEING_RECIPES_ONLY_BOOKS.get()
                      && !(itemHolder.value() instanceof WritableBookItem)
                      && !(itemHolder.value() instanceof WrittenBookItem)) {
                    continue;
                }

                DyeItem dye = DyeItem.byColor(color);
                List<Ingredient> inputs = List.of(Ingredient.of(itemHolder.value()), Ingredient.of(dye));
                ItemStack result = DyedItemColor.applyDyes(new ItemStack(itemHolder), List.of(dye));
                String id = "dyeing_" + itemHolder.value().toString().replace(':', '_') + "_with_" + color.getName();
                ShapelessRecipe recipe = new ShapelessRecipe("dyeing_" + color.getName(),
                      CraftingBookCategory.MISC, result, inputs);
                recipes.add(new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, Identifier.withDefaultNamespace(id)), recipe));
            }
        }

        if (!recipes.isEmpty()) {
            registration.addRecipes(RecipeTypes.CRAFTING, recipes);
        }
    }
}