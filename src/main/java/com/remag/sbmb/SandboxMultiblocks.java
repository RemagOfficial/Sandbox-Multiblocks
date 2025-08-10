package com.remag.sbmb;

import com.mojang.logging.LogUtils;
import com.remag.sbmb.block.ModBlocks;
import com.remag.sbmb.components.ModDataComponents;
import com.remag.sbmb.config.ModCommonConfigs;
import com.remag.sbmb.item.ModItems;
import com.remag.sbmb.multiblock.MultiblockRecipe;
import com.remag.sbmb.recipe.ModRecipeSerializers;
import com.remag.sbmb.recipe.ModRecipeTypes;
import com.remag.sbmb.tab.ModCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
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
import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SandboxMultiblocks.MODID)
public class SandboxMultiblocks
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "sbmb";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public SandboxMultiblocks(IEventBus modEventBus, ModContainer modContainer)
    {

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ModDataComponents.DATA_COMPONENT_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModCreativeModeTab.TABS.register(modEventBus);
        ModRecipeTypes.RECIPE_TYPES.register(modEventBus);
        ModRecipeSerializers.SERIALIZERS.register(modEventBus);

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

        List<RecipeHolder<MultiblockRecipe>> recipes = recipeManager.getAllRecipesFor(ModRecipeTypes.MULTIBLOCK_RECIPE_TYPE.get());

        if (recipes.isEmpty())
        {
            LOGGER.info("No multiblock recipes found in the server");
        } else {
            LOGGER.info("Found {} multiblock recipes in the server", recipes.size());
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
