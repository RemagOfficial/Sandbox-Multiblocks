package com.remag.sbmb.item.customItems;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.remag.sbmb.SandboxMultiblocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DebugWrenchItem extends Item {
    private static final String SIZE_KEY = "MultiblockSize";

    public DebugWrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            CompoundTag tag = stack.getOrCreateTag();
            int currentSize = tag.getInt(SIZE_KEY);
            int newSize = getNextSize(currentSize);
            tag.putInt(SIZE_KEY, newSize);

            if (!level.isClientSide) {
                player.displayClientMessage(Component.literal("Multiblock size set to: " + newSize), true);
            }

            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getTag();
        int size = tag != null && tag.contains(SIZE_KEY) ? tag.getInt(SIZE_KEY) : 3;
        tooltip.add(Component.translatable("tooltip." + SandboxMultiblocks.MODID + ".hammer_size", size)
                .withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide()) {
            BlockPos center = context.getClickedPos();
            Direction face = context.getClickedFace();
            BlockPos centerPos = center.relative(face.getOpposite());
            Level level = context.getLevel();
            Player player = context.getPlayer();

            ItemStack stack = context.getItemInHand();
            int size = getSelectedSize(stack);

            Direction.Axis axis1, axis2;
            switch (face) {
                case NORTH, SOUTH -> {
                    axis1 = Direction.Axis.X;
                    axis2 = Direction.Axis.Y;
                }
                case EAST, WEST -> {
                    axis1 = Direction.Axis.Z;
                    axis2 = Direction.Axis.Y;
                }
                case UP, DOWN -> {
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.sendSystemMessage(Component.literal("§cCannot use the wrench on the top or bottom of a multiblock."));
                    }
                    return InteractionResult.FAIL;
                }
                default -> {
                    return InteractionResult.PASS;
                }
            }

            Direction opposite = face.getOpposite();
            String[][][] detectedPattern = new String[size][size][size];
            int half = size / 2;

            for (int depth = 0; depth < size; depth++) {
                BlockPos sliceCenter = center.relative(opposite, depth);
                String[][] grid = new String[size][size];

                for (int dy = -half; dy <= half; dy++) {
                    for (int dx = -half; dx <= half; dx++) {
                        int row = dy + half;
                        int col = dx + half;
                        BlockPos offset = offsetInPlane(sliceCenter, axis1, axis2, dx, dy);
                        BlockState state = level.getBlockState(offset);
                        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(state.getBlock());

                        String idStr = id != null ? id.toString() : "null";
                        grid[row][col] = id != null ? id.getPath() : "null";
                        detectedPattern[depth][row][col] = idStr;
                    }
                }
            }
            assert player != null;
            JsonObject recipe = createMultiblockRecipe(player.getOffhandItem(), size, detectedPattern);
            saveMultiblockRecipe(recipe);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.SUCCESS;
    }

    private int getNextSize(int currentSize) {
        return switch (currentSize) {
            case 3 -> 5;
            case 5 -> 7;
            default -> 3;
        };
    }

    public static int getSelectedSize(ItemStack stack) {
        return stack.getOrCreateTag().getInt(SIZE_KEY);
    }

    private BlockPos offsetInPlane(BlockPos origin, Direction.Axis axis1, Direction.Axis axis2, int off1, int off2) {
        int x = origin.getX();
        int y = origin.getY();
        int z = origin.getZ();

        switch (axis1) {
            case X -> x += off1;
            case Y -> y += off1;
            case Z -> z += off1;
        }

        switch (axis2) {
            case X -> x += off2;
            case Y -> y += off2;
            case Z -> z += off2;
        }

        return new BlockPos(x, y, z);
    }

    public JsonObject createMultiblockRecipe(ItemStack offhandItem, int size, String[][][] detectedPattern) {
        JsonObject recipe = new JsonObject();

        // Basic fields
        recipe.addProperty("type", "sbmb:multiblock");
        recipe.addProperty("size", size);

        // Pattern
        JsonArray patternArray = new JsonArray();
        for (int layer = 0; layer < size; layer++) {
            JsonArray layerArray = new JsonArray();
            for (int row = 0; row < size; row++) {
                JsonArray rowArray = new JsonArray();
                for (int col = 0; col < size; col++) {
                    rowArray.add(detectedPattern[layer][row][col]);
                }
                layerArray.add(rowArray);
            }
            patternArray.add(layerArray);
        }
        recipe.add("pattern", patternArray);

        // Optional comments
        JsonArray comments = new JsonArray();
        for (int i = 0; i < size; i++) {
            comments.add("Layer " + (i + 1) + ": " +
                    (i == 0 ? "Closest to the player (the face clicked)" :
                            i == 1 ? "One block further away" :
                                    i == 2 ? "Farthest from the player" : "Further layer " + (i + 1)));
        }
        recipe.add("layer_comments", comments);

        // Result item
        Item resultItem = offhandItem.getItem();
        ResourceLocation resultId = ForgeRegistries.ITEMS.getKey(resultItem);
        assert resultId != null;
        recipe.addProperty("result", resultId.toString());

        // Count if greater than 1
        int count = offhandItem.getCount();
        if (count > 1) {
            recipe.addProperty("count", count);
        }

        return recipe;
    }

    public void saveMultiblockRecipe(JsonObject recipeJsonObject) {
        File mcRoot = FMLPaths.GAMEDIR.get().toFile();
        File multiblockFolder = new File(mcRoot, "multiblocks");

        if (!multiblockFolder.exists()) {
            if (!multiblockFolder.mkdirs()) {
                SandboxMultiblocks.LOGGER.error("Failed to create directory for multiblock recipes: {}", multiblockFolder.getAbsolutePath());
                return; // or throw
            }
        }

        String fullResultId = recipeJsonObject.get("result").getAsString();
        String resultName = fullResultId.contains(":") ? fullResultId.split(":")[1] : fullResultId;
        int size = recipeJsonObject.get("size").getAsInt();
        int count = recipeJsonObject.has("count") ? recipeJsonObject.get("count").getAsInt() : 1;

        String baseName = resultName + "_" + size + "x" + size + "x" + size;
        File outputFile = new File(multiblockFolder, baseName + ".json");
        int index = 1;
        while (outputFile.exists()) {
            outputFile = new File(multiblockFolder, baseName + "_" + index + ".json");
            index++;
        }

        try (JsonWriter writer = new JsonWriter(new FileWriter(outputFile))) {
            writer.setIndent("  ");
            writer.beginObject();

            for (Map.Entry<String, com.google.gson.JsonElement> entry : recipeJsonObject.entrySet()) {
                String key = entry.getKey();

                if (key.equals("result") || key.equals("count")) continue;

                writer.name(key);

                if ("pattern".equals(key)) {
                    // Write pattern specially for compact formatting
                    writePatternCompact(writer, entry.getValue().getAsJsonArray());
                } else {
                    com.google.gson.internal.Streams.write(entry.getValue(), writer);
                }
            }

            // Write result last
            writer.name("result").value(fullResultId);

            if (recipeJsonObject.has("count")) {
                writer.name("count").value(count);
            }

            writer.endObject();

            System.out.println("Recipe saved to " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            SandboxMultiblocks.LOGGER.error("Failed to save multiblock recipe to file: {}", outputFile.getAbsolutePath(), e);
        }
    }


    /**
     * Writes the 'pattern' array with innermost arrays inline (compact style)
     * pattern is a 3-level nested array JsonArray (size x size x size)
     */
    private void writePatternCompact(JsonWriter writer, com.google.gson.JsonArray pattern) throws IOException {
        writer.beginArray(); // outermost (layers)
        for (com.google.gson.JsonElement layerElem : pattern) {
            com.google.gson.JsonArray layer = layerElem.getAsJsonArray();
            writer.beginArray(); // layer array
            for (com.google.gson.JsonElement rowElem : layer) {
                com.google.gson.JsonArray row = rowElem.getAsJsonArray();
                writer.beginArray(); // innermost row array - write inline
                for (com.google.gson.JsonElement blockElem : row) {
                    writer.value(blockElem.getAsString());
                }
                writer.endArray();
            }
            writer.endArray();
        }
        writer.endArray();
    }
}
