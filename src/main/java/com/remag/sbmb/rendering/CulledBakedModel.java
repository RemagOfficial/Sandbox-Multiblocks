package com.remag.sbmb.rendering;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CulledBakedModel implements BakedModel {
    private final BakedModel original;
    private final Set<Direction> facesToCull;

    public CulledBakedModel(BakedModel original, Set<Direction> facesToCull) {
        this.original = original;
        this.facesToCull = facesToCull;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        if (side != null && facesToCull.contains(side)) {
            return Collections.emptyList(); // skip quads for this face
        }
        return original.getQuads(state, side, rand);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return original.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return original.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return original.getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        return original.getOverrides();
    }
}
