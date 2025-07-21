package com.remag.sbmb;

import com.mojang.logging.LogUtils;
import com.remag.sbmb.block.ModBlocks;
import com.remag.sbmb.config.ModCommonConfigs;
import com.remag.sbmb.item.ModItems;
import com.remag.sbmb.multiblock.MultiblockRecipe;
import com.remag.sbmb.multiblock.MultiblockRecipeSerializer;
import com.remag.sbmb.recipe.ModRecipeTypes;
import com.remag.sbmb.tab.ModCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
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
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

    public static final RegistryObject<MultiblockRecipeSerializer> MULTIBLOCK_SERIALIZER =
            SERIALIZERS.register("multiblock", MultiblockRecipeSerializer::new);

    public SandboxMultiblocks(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModCreativeModeTab.TABS.register(modEventBus);
        SERIALIZERS.register(modEventBus);
        ModRecipeTypes.RECIPE_TYPES.register(modEventBus);

        context.registerConfig(ModConfig.Type.COMMON, ModCommonConfigs.COMMON_CONFIG);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
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
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
        }
    }
}
