package com.remag.sbmb.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ModCommonConfigs {
    public static final ForgeConfigSpec COMMON_CONFIG;
    public static final ConfigValues COMMON;

    static {
        final Pair<ConfigValues, ForgeConfigSpec> specPair =
                new ForgeConfigSpec.Builder().configure(ConfigValues::new);
        COMMON = specPair.getLeft();
        COMMON_CONFIG = specPair.getRight();
    }

    public static class ConfigValues {
        public final ForgeConfigSpec.IntValue maxMultiblockSize;

        public ConfigValues(ForgeConfigSpec.Builder builder) {
            builder.push("multiblock");

            maxMultiblockSize = builder
                    .comment("Maximum multiblock size (must be odd number ≥ 3)")
                    .defineInRange("maxMultiblockSize", 7, 3, 99);  // Default 7, range 3 to 99

            builder.pop();
        }
    }
}
