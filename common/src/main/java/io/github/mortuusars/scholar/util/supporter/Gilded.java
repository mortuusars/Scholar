package io.github.mortuusars.scholar.util.supporter;

import io.github.mortuusars.scholar.Scholar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Gilded {
    private long lastQueryTime = -1L;
    private @Nullable List<Supporter> gildedSupporters = null;

    public boolean canQuery() {
        return System.currentTimeMillis() - lastQueryTime > 60000; // 1 min
    }

    public @NotNull List<Supporter> getOrQuery() {
        if (gildedSupporters != null) return gildedSupporters;
        if (!canQuery()) return Collections.emptyList();
        return query();
    }

    public @NotNull List<Supporter> query() {
        lastQueryTime = System.currentTimeMillis();
        try {
            Supporters.Loader loader = new Supporters.Loader();

            new Thread(() -> {
                String json = loader.readFileFromURL(getUuidsUri());
                if (json == null) return;
                gildedSupporters = loader.parseSupporters(json);
            }).start();
        } catch (Exception e) {
            Scholar.LOGGER.warn("Cannot get list of supporters.", e);
        }

        if (gildedSupporters == null) {
            return Collections.emptyList();
        }

        return gildedSupporters;
    }

    protected URI getUuidsUri() {
        return URI.create("https://raw.githubusercontent.com/mortuusars/resources/refs/heads/main/supporters/uuids/gilded.json");
    }

    // --

    public boolean hasAccessToGoldenSkin(UUID uuid) {
        return getOrQuery().stream().anyMatch(s -> s.matches(uuid));
    }
}
