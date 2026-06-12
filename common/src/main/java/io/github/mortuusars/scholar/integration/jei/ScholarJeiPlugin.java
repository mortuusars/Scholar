package io.github.mortuusars.scholar.integration.jei;

import io.github.mortuusars.scholar.Scholar;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class ScholarJeiPlugin implements IModPlugin {
    private static final Identifier ID = Scholar.identifier("jei_plugin");

    @Override
    public @NotNull Identifier getPluginUid() {
        return ID;
    }
}