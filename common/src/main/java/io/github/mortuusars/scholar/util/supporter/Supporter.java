package io.github.mortuusars.scholar.util.supporter;

import net.minecraft.util.Util;

import java.util.UUID;

public record Supporter(String name, UUID uuid) {
    public boolean matches(UUID uuid) {
        if (uuid.equals(Util.NIL_UUID)) return false;
        return uuid().equals(uuid);
    }
}
