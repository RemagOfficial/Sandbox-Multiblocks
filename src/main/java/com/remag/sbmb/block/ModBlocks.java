package com.remag.sbmb.block;

import com.remag.sbmb.SandboxMultiblocks;
import com.remag.sbmb.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SandboxMultiblocks.MODID);

    public static final DeferredBlock<Block> DUMMY_CENTER = registerBlock("dummy_center",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE)
                    .requiresCorrectToolForDrops()
                    .strength(0.8F, 0.8F)
            ));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
