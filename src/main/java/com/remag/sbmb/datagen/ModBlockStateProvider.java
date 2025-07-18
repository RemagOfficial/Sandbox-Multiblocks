package com.remag.sbmb.datagen;

import com.remag.sbmb.SandboxMultiblocks;
import com.remag.sbmb.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, SandboxMultiblocks.MODID, exFileHelper);

    }

    @Override
    protected void registerStatesAndModels() {
        // generate block states for all blocks
        ModBlocks.BLOCKS.getEntries().forEach(this::blockWithItem);
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}
