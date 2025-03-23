package io.github.mortuusars.scholar.forge.mixin;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import io.github.mortuusars.scholar.client.resource.BuiltInResourcePacks;
import io.github.mortuusars.scholar.forge.resource.ProcessedBuiltInResourcePackTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.Options;

@Mixin(Options.class)
public class GameOptionsMixin {
    @Shadow
    public List<String> resourcePacks;

    /**
     * Enables resource packs that should be enabled by default, but only once (to not enable it again if user disabled it)
     * This is here because forge does not do this by itself (fabric does, though).
     */
    @Inject(method = "load(Z)V", at = @At("RETURN"), remap = false)
    private void onLoad(boolean limited, CallbackInfo ci) {
        if (limited) return;

        Set<String> processedPacks = ProcessedBuiltInResourcePackTracker.getPacks(); // Packs, that were enabled before.
        Set<String> resourcePacks = new LinkedHashSet<>(this.resourcePacks);

        List<BuiltInResourcePacks.Pack> packs = BuiltInResourcePacks.get();
        for (BuiltInResourcePacks.Pack pack : packs) {
            String id = pack.id().toString();
            if (pack.activation().forge() == BuiltInResourcePacks.ActivationType.DEFAULT_ENABLED) {
                if (processedPacks.add(id)) { // If added - pack was not added before.
                    resourcePacks.add(id);
                }
            } else { // Activation does not match.
                // Remove to enable later (if activation changes back to DEFAULT_ENABLED)
                processedPacks.remove(id);
            }
        }

        // Remove non-existent packs
        processedPacks.removeIf(pack ->
                packs.stream().noneMatch(p ->
                        p.id().toString().equals(pack)));

        ProcessedBuiltInResourcePackTracker.savePacks(processedPacks);

        this.resourcePacks = new ArrayList<>(resourcePacks);
    }
}
