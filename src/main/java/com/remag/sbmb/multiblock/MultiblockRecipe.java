package com.remag.sbmb.multiblock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.remag.sbmb.SandboxMultiblocks;
import com.remag.sbmb.recipe.ModRecipeTypes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class MultiblockRecipe implements Recipe<Container> {
    public final ResourceLocation id;
    public int size; // can ignore for now
    public List<List<List<String>>> pattern;
    public String result;
    public int count;

    public MultiblockRecipe(ResourceLocation id, int size, List<List<List<String>>> pattern, String result, int count) {
        this.id = id;
        this.size = size;
        this.pattern = pattern;
        this.result = result;
        this.count = count;
    }

    @Override
    public boolean matches(Container inv, Level world) {
        // You can return false or implement logic if needed
        return false;
    }

    @Override
    public ItemStack assemble(Container inv, RegistryAccess access) {
        Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(result));
        return item == null ? ItemStack.EMPTY : new ItemStack(item, count);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        // Return true if the recipe can be crafted in given crafting grid size
        // You can simplify to true or check size if you want
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(result));
        return item == null ? ItemStack.EMPTY : new ItemStack(item, count);
    }

    @Override
    public ResourceLocation getId() {
        // Return the recipe ID; you can store it as a field or pass via constructor
        // For now return null or a dummy one; you might want to add this to your class
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        // Return your recipe serializer instance
        return SandboxMultiblocks.MULTIBLOCK_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        // Return your recipe type instance
        return ModRecipeTypes.MULTIBLOCK_RECIPE_TYPE.get();
    }

    public record PartialMultiblockRecipe(int size, List<List<List<String>>> pattern, String result, int count) {
        public static final Codec<PartialMultiblockRecipe> PARTIAL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("size").forGetter(PartialMultiblockRecipe::size),
                Codec.STRING.listOf().listOf().listOf().fieldOf("pattern").forGetter(PartialMultiblockRecipe::pattern),
                Codec.STRING.fieldOf("result").forGetter(PartialMultiblockRecipe::result),
                Codec.INT.optionalFieldOf("count", 1).forGetter(PartialMultiblockRecipe::count)  // optional with default=1
        ).apply(instance, PartialMultiblockRecipe::new));
    }
}
