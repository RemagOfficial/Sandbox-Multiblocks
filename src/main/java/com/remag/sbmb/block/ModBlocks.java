package com.remag.sbmb.block;

import com.remag.sbmb.SandboxMultiblocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SandboxMultiblocks.MODID);

    public static final RegistryObject<Block> DUMMY_CENTER = BLOCKS.register("dummy_center",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.SANDSTONE)
                    .requiresCorrectToolForDrops()
                    .strength(0.8F, 0.8F)
            ));
}
