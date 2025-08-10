package com.remag.sbmb.tab;

import com.remag.sbmb.SandboxMultiblocks;
import com.remag.sbmb.item.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModCreativeModeTab {

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SandboxMultiblocks.MODID);

    public static final List<Supplier<? extends ItemLike>> SBMB_TABS = new ArrayList<>();

    public static final Supplier<CreativeModeTab> SBMB_TAB = TABS.register("sbmb_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.sbmb_tab"))
                    .icon(ModItems.CRAFTING_HAMMER.get().asItem()::getDefaultInstance)
                    .displayItems((params, output) -> {
                        for (Supplier<? extends ItemLike> itemSupplier : ModItems.ITEMS.getEntries()) {
                            Item item = itemSupplier.get().asItem();
                            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
                            if (id != null) {
                                output.accept(item);
                            }
                        }
                    })
                    .build()
    );

    public static <T extends Item> DeferredItem<T> addToTab(DeferredItem<T> itemLike) {
        SBMB_TABS.add(itemLike);
        return itemLike;
    }
}
