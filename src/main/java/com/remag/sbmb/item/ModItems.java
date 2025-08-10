package com.remag.sbmb.item;

import com.remag.sbmb.SandboxMultiblocks;
import com.remag.sbmb.block.ModBlocks;
import com.remag.sbmb.item.customItems.CraftingHammerItem;
import com.remag.sbmb.item.customItems.DebugWrenchItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.remag.sbmb.tab.ModCreativeModeTab.addToTab;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SandboxMultiblocks.MODID);

    public static final DeferredItem<Item> CRAFTING_HAMMER = addToTab(ITEMS.register("crafting_hammer",
            () -> new CraftingHammerItem(new Item.Properties())));

    public static final DeferredItem<Item> DEBUG_WRENCH = addToTab(ITEMS.register("debug_wrench",
            () -> new DebugWrenchItem(new Item.Properties())));
}
