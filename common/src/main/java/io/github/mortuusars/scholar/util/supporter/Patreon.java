package io.github.mortuusars.scholar.util.supporter;

import io.github.mortuusars.scholar.Scholar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.*;

public class Patreon {
    private long lastQueryTime = -1L;
    private @Nullable Map<Tier, List<Supporter>> patrons = null;

    public boolean canQuery() {
        return System.currentTimeMillis() - lastQueryTime > 60000; // 1 min
    }

    public @NotNull Map<Tier, List<Supporter>> getOrQuery() {
        if (patrons != null) return patrons;
        if (!canQuery()) return Collections.emptyMap();
        return query();
    }

    public @NotNull Map<Tier, List<Supporter>> query() {
        lastQueryTime = System.currentTimeMillis();
        try {
            Supporters.Loader loader = new Supporters.Loader();

            if (patrons != null) {
                patrons.clear();
            }

            new Thread(() -> {
                for (Tier patreonTier : Tier.values()) {
                    String json = loader.readFileFromURL(patreonTier.getUuidsUri());
                    if (json == null) return;

                    List<Supporter> parsedSupporters = loader.parseSupporters(json);

                    if (patrons == null) {
                        patrons = new HashMap<>();
                    }

                    patrons.put(patreonTier, parsedSupporters);
                }
            }).start();
        } catch (Exception e) {
            Scholar.LOGGER.warn("Cannot get list of supporters.", e);
        }

        if (patrons == null) {
            return Collections.emptyMap();
        }

        return patrons;
    }

    // --

    public boolean hasAccessToGoldenSkin(UUID uuid) {
        Map<Tier, List<Supporter>> patrons = getOrQuery();
        return patrons.getOrDefault(Tier.GOLD, Collections.emptyList()).stream().anyMatch(s -> s.matches(uuid))
                || patrons.getOrDefault(Tier.DIAMOND, Collections.emptyList()).stream().anyMatch(s -> s.matches(uuid));
    }

    public enum Tier {
        COPPER,
        IRON,
        GOLD,
        DIAMOND;

        public URI getUuidsUri() {
            return URI.create("https://raw.githubusercontent.com/mortuusars/resources/refs/heads/main/supporters/patreon/uuids/"
                    + name().toLowerCase() + ".json");
        }
    }
}
