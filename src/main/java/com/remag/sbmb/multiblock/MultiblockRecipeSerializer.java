package com.remag.sbmb.multiblock;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class MultiblockRecipeSerializer implements RecipeSerializer<MultiblockRecipe> {

    @Override
    public MultiblockRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        DataResult<MultiblockRecipe.PartialMultiblockRecipe> result = MultiblockRecipe.PartialMultiblockRecipe.PARTIAL_CODEC.parse(JsonOps.INSTANCE, json);
        MultiblockRecipe.PartialMultiblockRecipe partial = result.getOrThrow(false, error -> {
            throw new RuntimeException("Failed to decode multiblock recipe json: " + error);
        });

        return new MultiblockRecipe(recipeId, partial.size(), partial.pattern(), partial.result(), partial.count());
    }

    @Override
    public MultiblockRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        String jsonString = buffer.readUtf(Short.MAX_VALUE);
        Gson gson = new Gson();
        MultiblockRecipe.PartialMultiblockRecipe partial = gson.fromJson(jsonString, MultiblockRecipe.PartialMultiblockRecipe.class);
        return new MultiblockRecipe(recipeId, partial.size(), partial.pattern(), partial.result(), partial.count());
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, MultiblockRecipe recipe) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(recipe);
        buffer.writeUtf(jsonString);
    }
}