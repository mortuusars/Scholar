package io.github.mortuusars.scholar.client.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public class HexColor {
    public static final Codec<Integer> CODEC = Codec.STRING.comapFlatMap(
          hex -> {
              try {
                  return DataResult.success(fromString(hex));
              } catch (NumberFormatException e) {
                  return DataResult.error(() -> hex + " cannot be parsed as int (argb) color.");
              }
          },
          Integer::toHexString
    );

    public static int fromString(String hex) {
        if (hex.charAt(0) == '#') {
            hex = hex.substring(1);
        }
        // Can't parse straight to int because of how integers are interpreted
        // 0xFFFFFFFF will throw for example
        long longValue = Long.parseLong(hex, 16);
        return (int)longValue;
    }
}
