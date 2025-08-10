package com.remag.sbmb;

import com.mojang.logging.LogUtils;
import com.remag.sbmb.block.ModBlocks;
import com.remag.sbmb.config.ModCommonConfigs;
import com.remag.sbmb.item.ModItems;
import com.remag.sbmb.multiblock.MultiblockRecipe;
import com.remag.sbmb.multiblock.MultiblockRecipeSerializer;
import com.remag.sbmb.recipe.ModRecipeTypes;
import com.remag.sbmb.tab.ModCreativeModeTab;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.Collection;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SandboxMultiblocks.MODID)
public class SandboxMultiblocks
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "sbmb";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MultiblockRecipe>> MULTIBLOCK_SERIALIZER =
            SERIALIZERS.register("multiblock", MultiblockRecipeSerializer::new);

    public SandboxMultiblocks(IEventBus modEventBus, ModContainer modContainer)
    {

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModCreativeModeTab.TABS.register(modEventBus);
        SERIALIZERS.register(modEventBus);
        ModRecipeTypes.RECIPE_TYPES.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, ModCommonConfigs.COMMON_CONFIG);

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        RecipeManager recipeManager = event.getServer().getRecipeManager();

        Collection<MultiblockRecipe> multiblockRecipes = recipeManager.getAllRecipesFor(ModRecipeTypes.MULTIBLOCK_RECIPE_TYPE.get());

        if (multiblockRecipes.isEmpty())
        {
            LOGGER.info("No multiblock recipes found in the server");
        } else {
            LOGGER.info("Found {} multiblock recipes in the server", multiblockRecipes.size());
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
        }
    }
}
