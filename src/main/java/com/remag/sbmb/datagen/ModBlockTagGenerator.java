package com.remag.sbmb.datagen;

import com.remag.sbmb.SandboxMultiblocks;
import com.remag.sbmb.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {

    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, SandboxMultiblocks.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        // add mineable with pickaxe tag to all blocks using a loop
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.DUMMY_CENTER.get());
    }

}
