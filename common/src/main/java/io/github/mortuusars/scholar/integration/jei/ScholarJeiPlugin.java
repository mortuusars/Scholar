package io.github.mortuusars.scholar.integration.jei;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.*;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class ScholarJeiPlugin implements IModPlugin {
    private static final ResourceLocation ID = Scholar.resource("jei_plugin");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        if (!Config.Common.JEI_DYEING_RECIPES.get()) {
            return;
        }

        List<CraftingRecipe> recipes = new ArrayList<>();

        for (DyeColor color : DyeColor.values()) {
            for (Item item : BuiltInRegistries.ITEM.stream().toList()) {
                if (!(item instanceof DyeableLeatherItem)) {
                    continue;
                }

                if (Config.Common.JEI_DYEING_RECIPES_ONLY_BOOKS.get()
                      && !(item instanceof WritableBookItem)
                      && !(item instanceof WrittenBookItem)) {
                    continue;
                }

                DyeItem dye = DyeItem.byColor(color);
                NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY,
                      Ingredient.of(item),
                      Ingredient.of(dye));
                ItemStack result = DyeableLeatherItem.dyeArmor(new ItemStack(item), List.of(dye));
                String id = "dyeing_" + item.toString().replace(':', '_') + "_with_" + color.getName();
                ShapelessRecipe recipe = new ShapelessRecipe(Scholar.resource(id), "dyeing_" + color.getName(),
                      CraftingBookCategory.MISC, result, inputs);
                recipes.add(recipe);
            }
        }

        if (!recipes.isEmpty()) {
            registration.addRecipes(RecipeTypes.CRAFTING, recipes);
        }
    }
}