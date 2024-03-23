package io.github.mortuusars.scholar;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class Scholar {
    public static final String ID = "scholar";

    public static ResourceLocation resource(String path) {
        return new ResourceLocation(Scholar.ID, path);
    }

    public static void init() {
        SoundEvents.init();
    }

    public static class SoundEvents {
        public static final Supplier<SoundEvent> FORMATTING_CLICK = register("book", "formatting_click");
        public static final Supplier<SoundEvent> BOOK_SIGNED = register("book", "signed");

        @SuppressWarnings("SameParameterValue")
        private static Supplier<SoundEvent> register(String category, String key) {
            Preconditions.checkState(category != null && !category.isEmpty(), "'category' should not be empty.");
            Preconditions.checkState(key != null && !key.isEmpty(), "'key' should not be empty.");
            String path = category + "." + key;
            return Register.soundEvent(path, () -> SoundEvent.createVariableRangeEvent(Scholar.resource(path)));
        }

        public static void init() { }
    }
}

