package io.github.mortuusars.scholar.neoforge.resource;

import io.github.mortuusars.scholar.Scholar;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.*;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ProcessedBuiltInResourcePackTracker {
    public static Set<String> getPacks() {
        Set<String> savedPacks = new HashSet<>();

        getStateFile().ifPresent(file -> {
            if (!file.exists()) return;
            try {
                CompoundTag data = NbtIo.readCompressed(file.toPath(), NbtAccounter.unlimitedHeap());
                ListTag values = data.getList("packs", Tag.TAG_STRING);

                for (int i = 0; i < values.size(); i++) {
                    savedPacks.add(values.getString(i));
                }
            } catch (IOException e) {
                Scholar.LOGGER.warn("[Scholar Default Built-in Resource Packs] Could not read {}", file.getAbsolutePath(), e);
            }
        });

        return savedPacks;
    }

    public static void savePacks(Set<String> processedPacks) {
        getStateFile().ifPresent(file -> {
            try {
                ListTag values = new ListTag();

                for (String id : processedPacks) {
                    values.add(StringTag.valueOf(id));
                }

                CompoundTag nbt = new CompoundTag();
                nbt.put("packs", values);


                NbtIo.writeCompressed(nbt, file.toPath());
            } catch (IOException e) {
                Scholar.LOGGER.warn("[Scholar Default Built-in Resource Packs] Could not write to {}", file.getAbsolutePath(), e);
            }
        });
    }

    private static Optional<File> getStateFile() {
        try {
            File dataFolder = Minecraft.getInstance().gameDirectory.toPath().resolve("data").toFile();

            if (!dataFolder.exists() && !dataFolder.mkdirs()) {
                Scholar.LOGGER.warn("[Scholar Default Built-in Resource Packs] Could not create data directory: {}", dataFolder.getAbsolutePath());
            }

            return Optional.of(new File(dataFolder, "scholar_loaded_built_in_resource_packs.dat"));
        } catch (Exception e) {
            Scholar.LOGGER.warn("[Scholar Default Built-in Resource Packs] Could not create state file: ", e);
            return Optional.empty();
        }
    }
}
