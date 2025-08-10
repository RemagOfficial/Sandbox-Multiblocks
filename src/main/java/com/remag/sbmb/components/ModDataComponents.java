package com.remag.sbmb.components;

import com.mojang.serialization.Codec;
import com.remag.sbmb.SandboxMultiblocks;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, SandboxMultiblocks.MODID);

    // StreamCodec for integer using varint read/write
    private static final StreamCodec<RegistryFriendlyByteBuf, Integer> INT_STREAM_CODEC = StreamCodec.of(
            (buf, value) -> buf.writeVarInt(value),
            buf -> buf.readVarInt()
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MULTIBLOCK_SIZE =
            DATA_COMPONENT_TYPES.register("multiblock_size", () ->
                    DataComponentType.<Integer>builder()
                            .persistent(Codec.INT)
                            .networkSynchronized(INT_STREAM_CODEC) // Use StreamCodec here!
                            .build()
            );

    public static void register(IEventBus bus) {
        DATA_COMPONENT_TYPES.register(bus);
    }
}
