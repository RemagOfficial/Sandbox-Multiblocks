package com.remag.sbmb.compat;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.remag.sbmb.SandboxMultiblocks;
import com.remag.sbmb.item.ModItems;
import com.remag.sbmb.multiblock.MultiblockRecipe;
import com.remag.sbmb.rendering.CulledBakedModel;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.inputs.IJeiGuiEventListener;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class MultiblockRecipeCategory implements IRecipeCategory<MultiblockRecipe> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(SandboxMultiblocks.MODID, "multiblock");
    private final IDrawable background;
    private final IDrawable icon;

    public MultiblockRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.createBlankDrawable(150, 50);
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.CRAFTING_HAMMER.get()));
    }

    @Override
    public void draw(MultiblockRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        RenderSystem.enableDepthTest();
        Minecraft mc = Minecraft.getInstance();
        mc.gameRenderer.lightTexture().turnOffLightLayer(); // Disable lighting

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        poseStack.translate(40, 22, 150);
        poseStack.mulPose(Axis.XP.rotationDegrees(330));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        int size = recipe.size;
        float baseScale = 10f;
        float scale = baseScale * (3f / size);

        poseStack.scale(scale, -scale, scale); // Flip Y, apply scale


        BlockRenderDispatcher dispatcher = mc.getBlockRenderer();
        ModelBlockRenderer modelRenderer = dispatcher.getModelRenderer();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        for (int y = 0; y < recipe.pattern.size(); y++) {
            for (int z = 0; z < recipe.pattern.get(y).size(); z++) {
                for (int x = 0; x < recipe.pattern.get(y).get(z).size(); x++) {
                    String id = recipe.pattern.get(y).get(z).get(x);
                    if (!id.equals("minecraft:air")) {
                        Block block = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(id));
                        if (block != null) {
                            BlockState state = block.defaultBlockState();
                            Set<Direction> facesToCull = new HashSet<>();
                            for (Direction face : Direction.values()) {
                                if (!shouldRenderFace(recipe, x, y, z, face)) {
                                    facesToCull.add(face);
                                }
                            }
                            BakedModel model = new CulledBakedModel(dispatcher.getBlockModel(state), facesToCull);

                            poseStack.pushPose();
                            poseStack.translate(x, -y, z);

                            for (RenderType renderType : model.getRenderTypes(state, RandomSource.create(), ModelData.EMPTY)) {
                                VertexConsumer buffer = bufferSource.getBuffer(renderType);

                                modelRenderer.renderModel(
                                        poseStack.last(),
                                        buffer,
                                        state,
                                        model,
                                        1f, 1f, 1f,
                                        LightTexture.FULL_BRIGHT,
                                        OverlayTexture.NO_OVERLAY,
                                        ModelData.EMPTY,
                                        renderType
                                );
                            }

                            poseStack.popPose();
                        }
                    }
                }
            }
        }

        bufferSource.endBatch(); // Flush all layers

        poseStack.popPose();
        mc.gameRenderer.lightTexture().turnOnLightLayer(); // Restore lighting
        RenderSystem.disableDepthTest();

    }

    private boolean shouldRenderFace(MultiblockRecipe recipe, int x, int y, int z, Direction face) {
        int nx = x + face.getStepX();
        int ny = y - face.getStepY();
        int nz = z + face.getStepZ();

        if (ny < 0 || ny >= recipe.pattern.size()) return true; // no neighbor, render face
        if (nz < 0 || nz >= recipe.pattern.get(ny).size()) return true;
        if (nx < 0 || nx >= recipe.pattern.get(ny).get(nz).size()) return true;

        String neighborId = recipe.pattern.get(ny).get(nz).get(nx);
        if (neighborId.equals("minecraft:air")) return true; // neighbor air, render face

        // If neighbor is same block or something opaque, do not render this face
        if (neighborId.equals(recipe.pattern.get(y).get(z).get(x))) return false;

        Block neighborBlock = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(neighborId));
        return neighborBlock == null || !neighborBlock.defaultBlockState().isSolidRender(null, null);// else render face
    }


    @Override
    public RecipeType<MultiblockRecipe> getRecipeType() {
        return RecipeType.create(SandboxMultiblocks.MODID, "multiblock", MultiblockRecipe.class);
    }

    @Override
    public Component getTitle() {
        return Component.literal("Multiblock Crafting");
    }

    @SuppressWarnings("removal")
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MultiblockRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 15)
                .addItemStack(new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(recipe.result))), recipe.count));
    }
}
