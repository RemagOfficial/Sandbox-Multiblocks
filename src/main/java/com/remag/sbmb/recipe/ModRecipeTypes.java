package com.remag.sbmb.recipe;

import com.remag.sbmb.SandboxMultiblocks;
import com.remag.sbmb.multiblock.MultiblockRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, SandboxMultiblocks.MODID);

    public static final RegistryObject<RecipeType<MultiblockRecipe>> MULTIBLOCK_RECIPE_TYPE =
            RECIPE_TYPES.register("multiblock", () ->
                    new RecipeType<>() {
                        public String toString() {
                            return SandboxMultiblocks.MODID + ":multiblock";
                        }
                    }
            );
}
