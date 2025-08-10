package com.remag.sbmb.datagen;

import com.remag.sbmb.block.ModBlocks;
import com.remag.sbmb.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(pOutput, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.DUMMY_CENTER.get(), 4)
                .define('S', Blocks.SANDSTONE)
                .define('T', Blocks.TARGET)
                .pattern("SSS")
                .pattern("STS")
                .pattern("SSS")
                .unlockedBy("has", has(ModBlocks.DUMMY_CENTER.asItem()))
                .save(recipeOutput, "dummy_center");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CRAFTING_HAMMER.get())
                .define('S', Items.STICK)
                .define('I', Items.IRON_INGOT)
                .pattern("III")
                .pattern("ISI")
                .pattern(" S ")
                .unlockedBy("has", has(ModItems.CRAFTING_HAMMER.get()))
                .save(recipeOutput, "multiblock_crafting_hammer");
    }
}
