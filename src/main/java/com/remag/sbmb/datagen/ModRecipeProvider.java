package com.remag.sbmb.datagen;

import com.remag.sbmb.block.ModBlocks;
import com.remag.sbmb.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.DUMMY_CENTER.get(), 4)
                .define('S', Blocks.SANDSTONE)
                .define('T', Blocks.TARGET)
                .pattern("SSS")
                .pattern("STS")
                .pattern("SSS")
                .unlockedBy("has", has(ModItems.DUMMY_CENTER.get()))
                .save(pWriter, "dummy_center");
    }
}
