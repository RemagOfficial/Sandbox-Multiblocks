package com.remag.sbmb.recipe;

import com.remag.sbmb.SandboxMultiblocks;
import com.remag.sbmb.multiblock.MultiblockRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, SandboxMultiblocks.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<MultiblockRecipe>> MULTIBLOCK_RECIPE_TYPE =
            RECIPE_TYPES.register("multiblock", () ->
                    new RecipeType<>() {
                        public String toString() {
                            return SandboxMultiblocks.MODID + ":multiblock";
                        }
                    }
            );
}
