package com.remag.sbmb.compat;

import com.remag.sbmb.SandboxMultiblocks;
import com.remag.sbmb.item.ModItems;
import com.remag.sbmb.multiblock.MultiblockRecipe;
import com.remag.sbmb.recipe.ModRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@JeiPlugin
public class SBMBJEIPlugin implements IModPlugin {

    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(SandboxMultiblocks.MODID, "multiblocks");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new MultiblockRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            List<MultiblockRecipe> recipes = minecraft.level.getRecipeManager()
                    .getAllRecipesFor(ModRecipeTypes.MULTIBLOCK_RECIPE_TYPE.get());
            registration.addRecipes(RecipeType.create(SandboxMultiblocks.MODID, "multiblock", MultiblockRecipe.class), recipes);
        }
    }

    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModItems.CRAFTING_HAMMER.get()), RecipeType.create(SandboxMultiblocks.MODID, "multiblock", MultiblockRecipe.class));
    }
}
