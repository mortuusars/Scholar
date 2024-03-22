package io.github.mortuusars.scholar;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

@Mod(Scholar.ID)
public class Scholar {
    public static final String ID = "scholar";

    public Scholar() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.Common.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.Client.SPEC);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        SoundEvents.SOUND_EVENTS.register(modEventBus);
    }

    public static ResourceLocation resource(String path) {
        return new ResourceLocation(Scholar.ID, path);
    }

    public static class SoundEvents {
        public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Scholar.ID);

        public static final Supplier<SoundEvent> FORMATTING_CLICK = register("book", "formatting_click");
        public static final Supplier<SoundEvent> BOOK_SIGNED = register("book", "signed");

        private static Supplier<SoundEvent> register(String category, String key) {
            Preconditions.checkState(category != null && !category.isEmpty(), "'category' should not be empty.");
            Preconditions.checkState(key != null && !key.isEmpty(), "'key' should not be empty.");
            String path = category + "." + key;
            return SOUND_EVENTS.register(path, () -> SoundEvent.createVariableRangeEvent(Scholar.resource(path)));
        }
    }
}

