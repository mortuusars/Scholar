package io.github.mortuusars.scholar.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.mortuusars.scholar.Scholar;
import net.minecraft.client.KeyMapping;

/**
 * Due to an oversight, Scholar fabric 26.3 was setting unbound key mappings to '-1', which was crashing the game on level loading.
 * And even after the fix, '-1' would remain in the options.txt (if bugged version was launched at least once).
 * This prevents wrong value being applied to the mapping. Then the game saves the correct value.
 * <br>
 * @deprecated This is only for fixing the wrong values in 'options.txt'. This class would be removed in the future.
 */
public class FixedKeyMapping extends KeyMapping {
    public FixedKeyMapping(String name, int keysym, Category category) {
        super(name, keysym, category);
    }

    @Override
    public void setKey(InputConstants.Key key) {
        if (key.getValue() == -1) {
            key = InputConstants.UNKNOWN;
            Scholar.LOGGER.info("Correcting wrong key mapping: '-1' -> 'unknown'");
        }
        super.setKey(key);
    }
}
