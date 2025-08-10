package com.remag.sbmb.recipe;

import com.remag.sbmb.SandboxMultiblocks;
import com.remag.sbmb.multiblock.MultiblockRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, SandboxMultiblocks.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MultiblockRecipe>> MULTIBLOCK_SERIALIZER =
            SERIALIZERS.register("multiblock", MultiblockRecipe.Serializer::new);
}
