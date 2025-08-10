package com.remag.sbmb.datagen;

import com.remag.sbmb.SandboxMultiblocks;
import com.remag.sbmb.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, SandboxMultiblocks.MODID, exFileHelper);

    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.DUMMY_CENTER);
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
}
