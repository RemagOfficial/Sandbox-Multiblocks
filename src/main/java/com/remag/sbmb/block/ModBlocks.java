package com.remag.sbmb.block;

import com.remag.sbmb.SandboxMultiblocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SandboxMultiblocks.MODID);

    public static final DeferredBlock<Block> DUMMY_CENTER = BLOCKS.register("dummy_center",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE)
                    .requiresCorrectToolForDrops()
                    .strength(0.8F, 0.8F)
            ));
}
