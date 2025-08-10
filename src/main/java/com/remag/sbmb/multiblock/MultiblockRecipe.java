package com.remag.sbmb.multiblock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.remag.sbmb.SandboxMultiblocks;
import com.remag.sbmb.recipe.ModRecipeSerializers;
import com.remag.sbmb.recipe.ModRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

import static com.remag.sbmb.SandboxMultiblocks.LOGGER;

public record MultiblockRecipe(int size, List<List<List<String>>> pattern, String result, int count) implements Recipe<RecipeInput> {

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider provider) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(result));
        return item == null ? ItemStack.EMPTY : new ItemStack(item, count);
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return false;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        // Return true if the recipe can be crafted in given crafting grid size
        // You can simplify to true or check size if you want
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(result));
        return item == null ? ItemStack.EMPTY : new ItemStack(item, count);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        // Return your recipe serializer instance
        return ModRecipeSerializers.MULTIBLOCK_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        // Return your recipe type instance
        return ModRecipeTypes.MULTIBLOCK_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<MultiblockRecipe> {
        public static final MapCodec<MultiblockRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.INT.fieldOf("size").forGetter(MultiblockRecipe::size),
                Codec.STRING.listOf().listOf().listOf().fieldOf("pattern").forGetter(MultiblockRecipe::pattern),
                Codec.STRING.fieldOf("result").forGetter(MultiblockRecipe::result),
                Codec.INT.optionalFieldOf("count", 1).forGetter(MultiblockRecipe::count)
        ).apply(inst, (size, pattern, result, count) -> {
            LOGGER.info("Loading multiblock recipe: size={}, result={}", size, result);
            return new MultiblockRecipe(size, pattern, result, count);
        }));

        public static final StreamCodec<RegistryFriendlyByteBuf, Integer> INT_CODEC = new StreamCodec<>() {
            @Override
            public Integer decode(RegistryFriendlyByteBuf input) {
                return input.readInt();
            }
            @Override
            public void encode(RegistryFriendlyByteBuf output, Integer value) {
                output.writeInt(value);
            }
        };

        public static final StreamCodec<RegistryFriendlyByteBuf, String> STRING_CODEC = new StreamCodec<>() {
            @Override
            public String decode(RegistryFriendlyByteBuf input) {
                return input.readUtf();
            }
            @Override
            public void encode(RegistryFriendlyByteBuf output, String value) {
                output.writeUtf(value);
            }
        };

        public static <T> StreamCodec<RegistryFriendlyByteBuf, List<T>> list(StreamCodec<RegistryFriendlyByteBuf, T> elementCodec) {
            return new StreamCodec<>() {
                @Override
                public List<T> decode(RegistryFriendlyByteBuf input) {
                    int size = input.readVarInt();
                    List<T> list = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) {
                        list.add(elementCodec.decode(input));
                    }
                    return list;
                }

                @Override
                public void encode(RegistryFriendlyByteBuf output, List<T> list) {
                    output.writeVarInt(list.size());
                    for (T item : list) {
                        elementCodec.encode(output, item);
                    }
                }
            };
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, List<List<List<String>>>> PATTERN_CODEC =
                list(
                        list(
                                list(STRING_CODEC)
                        )
                );

        public static final StreamCodec<RegistryFriendlyByteBuf, MultiblockRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        INT_CODEC, MultiblockRecipe::size,
                        PATTERN_CODEC, MultiblockRecipe::pattern,
                        STRING_CODEC, MultiblockRecipe::result,
                        INT_CODEC, MultiblockRecipe::count,
                        MultiblockRecipe::new
                );

        @Override
        public MapCodec<MultiblockRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MultiblockRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
