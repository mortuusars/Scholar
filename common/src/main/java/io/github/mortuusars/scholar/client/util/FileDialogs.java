package io.github.mortuusars.scholar.client.util;

import io.github.mortuusars.scholar.Scholar;
import org.lwjgl.PointerBuffer;
import org.lwjgl.sdl.SDLDialog;
import org.lwjgl.sdl.SDL_DialogFileCallback;
import org.lwjgl.sdl.SDL_DialogFileCallbackI;
import org.lwjgl.sdl.SDL_DialogFileFilter;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.io.FileFilter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class FileDialogs {
    private static Set<DialogRequest> ACTIVE =
          ConcurrentHashMap.newKeySet();

    public static void loadFile(String defaultPath, String title, String filterDescription,
                                boolean canChooseMultiple, Consumer<List<String>> callback, String... filters) {
        /*
        String path = defaultPath;
        SDL_DialogFileCallbackI callback = (_, filelist, _) -> {
            if (filelist == MemoryUtil.NULL) {
                return;
            }
            long pathPtr = MemoryUtil.memGetAddress(filelist);
            if (pathPtr == MemoryUtil.NULL) {
                return;
            }
            String pth = MemoryUtil.memUTF8(pathPtr);
            callback1.accept(List.of(pth));
        };
        SDL_DialogFileFilter.Buffer filters = null;

        if (filters1.length > 0) {
            filters = SDL_DialogFileFilter.create(filters1.length);

            for (int i = 0; i < filters1.length; i++) {
                filters.get(i).name(MemoryStack.stackUTF8(filterDescription)).pattern(MemoryStack.stackUTF8(filters1[i]));
            }
        }

//        if (fc.getFilter() instanceof FileFilter) {
//            FileFilter ff = (FileFilter) fc.getFilter();
//            if (ff.isFolder()) {
//                SDLDialog.SDL_ShowOpenFolderDialog(callback, 0, window, path, false);
//                return future;
//            } else if(ff.getExt() != null) {
//                filters = SDL_DialogFileFilter.create(1);
//                filters.get(0).name(MemoryStack.stackUTF8(fc.getSafeDescription())).pattern(MemoryStack.stackUTF8(ff.getExt()));
//            }
//        }

        SDLDialog.SDL_ShowOpenFileDialog(callback,
              0,
              Minecrft.get().getWindow().handle(),
              filters,
              path,
              false);
//        if (fc.isSaveDialog()) {
//            SDLDialog.SDL_ShowSaveFileDialog(callback, 0, window, filters, path);
//        } else {
//        }
         */

        show(defaultPath, title, canChooseMultiple, filters, filterDescription, callback, false);
    }

    public static void saveFile(String defaultPath, String title, String filterDescription,
                                Consumer<List<String>> callback, String... filters) {
        show(defaultPath, title, false, filters, filterDescription, callback, true);
    }

    private static void show(String defaultPath, String title, boolean canChooseMultiple,
                             String[] filters, String filterDescription,
                             Consumer<List<String>> callback, boolean save) {
        defaultPath = defaultPath.replaceAll("\\\\.$", "\\\\");
        DialogRequest request = new DialogRequest(defaultPath, title, filterDescription, filters, callback);
        ACTIVE.add(request);
        request.open(canChooseMultiple, save);
    }

    private static final class DialogRequest {
        private final String defaultPath;
        private final String title;
        private final String filterDescription;
        private final String[] filters;
        private final Consumer<List<String>> callback;

        private final List<ByteBuffer> strings = new ArrayList<>();
        private SDL_DialogFileFilter.Buffer filterBuffer;
        private SDL_DialogFileCallback nativeCallback;

        private DialogRequest(String defaultPath, String title, String filterDescription,
                              String[] filters, Consumer<List<String>> callback) {
            this.defaultPath = defaultPath;
            this.title = title;
            this.filterDescription = filterDescription;
            this.filters = filters;
            this.callback = callback;
        }

        private void open(boolean canChooseMultiple, boolean save) {
            try {
                if (filters.length > 0) {
                    filterBuffer = SDL_DialogFileFilter.malloc(filters.length);

                    for (int i = 0; i < filters.length; i++) {
                        ByteBuffer name = MemoryUtil.memUTF8(filterDescription);
                        ByteBuffer pattern = MemoryUtil.memUTF8(filters[i]);

                        strings.add(name);
                        strings.add(pattern);

                        filterBuffer.get(i)
                              .name(name)
                              .pattern(pattern);
                    }
                }

                nativeCallback = SDL_DialogFileCallback.create((asd, filelist, asdas) -> {
                    try {
                        List<String> result = new ArrayList<>();

                        if (filelist != 0) {
                            PointerBuffer files = MemoryUtil.memPointerBuffer(filelist, 256);

                            for (int i = 0; i < files.capacity(); i++) {
                                long address = files.get(i);
                                if (address == 0)
                                    break;

                                result.add(MemoryUtil.memUTF8(address));
                            }
                        }

                        callback.accept(result);
                    } catch (Exception e) {
                        Scholar.LOGGER.error("Cannot choose file", e);
                        callback.accept(List.of());
                    } finally {
                        close();
                    }
                });

                if (save) {
                    SDLDialog.SDL_ShowSaveFileDialog(nativeCallback, 0, 0, filterBuffer, defaultPath);
                } else {
                    SDLDialog.SDL_ShowOpenFileDialog(nativeCallback, 0, Minecrft.get().getWindow().handle(), filterBuffer, defaultPath, canChooseMultiple);
                }
            } catch (Exception e) {
                close();
                Scholar.LOGGER.error("Cannot open file dialog", e);
                callback.accept(List.of());
            }
        }

        private void close() {
            if (nativeCallback != null) {
                nativeCallback.free();
                nativeCallback = null;
            }

            if (filterBuffer != null) {
                filterBuffer.free();
                filterBuffer = null;
            }

            strings.forEach(MemoryUtil::memFree);
            strings.clear();
        }
    }
}