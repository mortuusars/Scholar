package io.github.mortuusars.scholar.client.util;

import io.github.mortuusars.scholar.Scholar;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.util.Optional;

public class FileDialogs {
    public static Optional<String> loadFile(String defaultPath, String title, String filterDescription,
                                            boolean canChooseMultiple, String... filters) {
        defaultPath = defaultPath.replaceAll("\\\\.$", "\\\\");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            return Optional.ofNullable(TinyFileDialogs.tinyfd_openFileDialog(
                    title,
                    defaultPath,
                    createFiltersBuffer(filters, stack),
                    filterDescription,
                    canChooseMultiple
            ));
        } catch (Exception e) {
            Scholar.LOGGER.error("Cannot choose file to load: ", e);
            return Optional.empty();
        }
    }

    public static Optional<String> saveFile(String defaultPath, String title, String filterDescription, String... filters) {
        defaultPath = defaultPath.replaceAll("\\\\.$", "\\\\");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            return Optional.ofNullable(TinyFileDialogs.tinyfd_saveFileDialog(
                    title,
                    defaultPath,
                    createFiltersBuffer(filters, stack),
                    filterDescription
            ));
        } catch (Exception e) {
            Scholar.LOGGER.error("Cannot choose file to load: ", e);
            return Optional.empty();
        }
    }

    private static @Nullable PointerBuffer createFiltersBuffer(String[] filters, MemoryStack stack) {
        if (Minecraft.ON_OSX || filters.length == 0) { // MacOS has some issues with filters. It's better to not use them at all right now.
            return null;
        }

        PointerBuffer filtersBuffer = stack.mallocPointer(filters.length);
        for (String filter : filters) {
            filtersBuffer.put(stack.UTF8(filter));
        }
        filtersBuffer.flip(); // Reset position for reading
        return filtersBuffer;
    }
}
