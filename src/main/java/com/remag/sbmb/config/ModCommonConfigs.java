package com.remag.sbmb.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ModCommonConfigs {
    public static final ModConfigSpec COMMON_CONFIG;
    public static final ConfigValues COMMON;

    static {
        final Pair<ConfigValues, ModConfigSpec> specPair =
                new ModConfigSpec.Builder().configure(ConfigValues::new);
        COMMON = specPair.getLeft();
        COMMON_CONFIG = specPair.getRight();
    }

    public static class ConfigValues {
        public final ModConfigSpec.IntValue maxMultiblockSize;

        public ConfigValues(ModConfigSpec.Builder builder) {
            builder.push("multiblock");

            maxMultiblockSize = builder
                    .comment("Maximum multiblock size (must be odd number ≥ 3)")
                    .defineInRange("maxMultiblockSize", 7, 3, 21);  // Default 7, range 3 to 21

            builder.pop();
        }
    }
}
