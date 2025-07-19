package com.remag.sbmb.item;

import com.remag.sbmb.SandboxMultiblocks;
import com.remag.sbmb.block.ModBlocks;
import com.remag.sbmb.item.customItems.CraftingHammerItem;
import com.remag.sbmb.item.customItems.DebugWrenchItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.remag.sbmb.tab.ModCreativeModeTab.addToTab;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, SandboxMultiblocks.MODID);

    public static final RegistryObject<Item> CRAFTING_HAMMER = addToTab(ITEMS.register("crafting_hammer",
            () -> new CraftingHammerItem(new Item.Properties())));

    public static final RegistryObject<Item> DEBUG_WRENCH = addToTab(ITEMS.register("debug_wrench",
            () -> new DebugWrenchItem(new Item.Properties())));

    public static final RegistryObject<Item> DUMMY_CENTER = addToTab(ITEMS.register("dummy_center",
            () -> new BlockItem(ModBlocks.DUMMY_CENTER.get(),
                    new Item.Properties()
            )
    ));
}
