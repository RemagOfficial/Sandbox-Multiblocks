package com.remag.sbmb.item.customItems;

import com.remag.sbmb.SandboxMultiblocks;
import com.remag.sbmb.multiblock.MultiblockRecipe;
import com.remag.sbmb.recipe.ModRecipeTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class CraftingHammerItem extends Item {
    private static final String SIZE_KEY = "MultiblockSize";

    public CraftingHammerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Only cycle if shift is held and air is targeted (use() is only called when not targeting a block)
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
        if (tag != null && tag.contains(SIZE_KEY)) {
            int size = tag.getInt(SIZE_KEY);
            tooltip.add(Component.translatable("tooltip." + SandboxMultiblocks.MODID + ".hammer_size", size)
                    .withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("tooltip." + SandboxMultiblocks.MODID + ".hammer_size", 3)  // Default if unset
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide()) {
            BlockPos center = context.getClickedPos();
            Direction face = context.getClickedFace();
            BlockPos centerPos = center.relative(face.getOpposite());
            Level level = context.getLevel();
            Player player = context.getPlayer();

            // Get multiblock size from item NBT (default to 3 if missing or invalid)
            ItemStack stack = context.getItemInHand();
            int size = 3;
            if (stack.hasTag() && stack.getTag().contains("MultiblockSize")) {
                size = stack.getTag().getInt("MultiblockSize");
                if (size % 2 == 0 || size < 3) size = 3; // Ensure odd size >= 3
            }

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
                        serverPlayer.sendSystemMessage(Component.literal("§cError: Cannot use the hammer on the top or bottom of a multiblock!"));
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

                if (size == 7 && player instanceof ServerPlayer serverPlayer) {
                    // serverPlayer.sendSystemMessage(Component.literal("§eLayer " + (depth + 1) + " of detected pattern:"));
                    System.out.println("Layer " + (depth + 1) + ":");
                    for (String[] row : grid) {
                        StringBuilder sb = new StringBuilder();
                        for (String entry : row) {
                            sb.append(String.format("%-15s", entry));
                        }
                        String rowStr = sb.toString();
                        // serverPlayer.sendSystemMessage(Component.literal(rowStr));
                        System.out.println(rowStr);
                    }
                }
            }

            List<MultiblockRecipe> recipes = level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.MULTIBLOCK_RECIPE_TYPE.get());

            for (MultiblockRecipe recipe : recipes) {
                if (matchesRecipe(detectedPattern, recipe.pattern)) {
                    removeMatchedBlocks(level, center, face, size);
                    spawnResultItem(level, center, recipe.result, recipe.count);
                    spawnParticleRing((ServerLevel) level, centerPos, ParticleTypes.END_ROD, 1.0, 32);
                    return InteractionResult.SUCCESS;
                }
            }

            if (size == 7 && player instanceof ServerPlayer serverPlayer) {
                System.out.println("No matching 7x7x7 multiblock recipe found.");
                // serverPlayer.sendSystemMessage(Component.literal("§cNo matching 7x7x7 multiblock recipe found."));
            }

            return InteractionResult.FAIL;
        }

        return InteractionResult.SUCCESS;
    }

    private int getNextSize(int currentSize) {
        // Cycle between 3x3x3, 5x5x5, and 7x7x7 for example
        return switch (currentSize) {
            case 3 -> 5;
            case 5 -> 7;
            default -> 3;
        };
    }

    public static int getSelectedSize(ItemStack stack) {
        return stack.getOrCreateTag().getInt(SIZE_KEY);
    }

    /**
     * Calculates a new position offset in a plane defined by two axes.
     */
    private BlockPos offsetInPlane(BlockPos origin, Direction.Axis axis1, Direction.Axis axis2, int off1, int off2) {
        int x = origin.getX();
        int y = origin.getY();
        int z = origin.getZ();

        // Offset along axis1
        switch (axis1) {
            case X -> x += off1;
            case Y -> y += off1;
            case Z -> z += off1;
        }

        // Offset along axis2
        switch (axis2) {
            case X -> x += off2;
            case Y -> y += off2;
            case Z -> z += off2;
        }

        return new BlockPos(x, y, z);
    }

    public boolean matchesRecipe(String[][][] detected, List<List<List<String>>> recipe) {
        if (detected.length != recipe.size()) return false;

        for (int z = 0; z < recipe.size(); z++) {
            List<List<String>> layer = recipe.get(z);
            if (detected[z].length != layer.size()) return false;

            for (int y = 0; y < layer.size(); y++) {
                List<String> row = layer.get(y);
                if (detected[z][y].length != row.size()) return false;

                for (int x = 0; x < row.size(); x++) {
                    String recipeBlock = row.get(x);
                    String detectedBlock = detected[z][y][x];

                    if (!recipeBlock.equals(detectedBlock)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void removeMatchedBlocks(Level level, BlockPos origin, Direction direction, int size) {
        Vec3i dirVec = direction.getNormal();

        // Perpendicular vector (90° rotated around Y axis)
        Vec3i perpendicular = switch (direction) {
            case NORTH -> new Vec3i(1, 0, 0);  // +X
            case SOUTH -> new Vec3i(-1, 0, 0); // -X
            case EAST  -> new Vec3i(0, 0, 1);  // +Z
            case WEST  -> new Vec3i(0, 0, -1); // -Z
            default -> throw new IllegalArgumentException("Unsupported direction: " + direction);
        };

        int half = size / 2;

        for (int y = 0; y < size; y++) {
            int dy = y - half;

            for (int z = 0; z < size; z++) {
                for (int x = 0; x < size; x++) {
                    int dx = (x - half) * perpendicular.getX();
                    int dz = (x - half) * perpendicular.getZ();
                    int backX = -dirVec.getX() * z;
                    int backZ = -dirVec.getZ() * z;

                    BlockPos target = origin.offset(dx + backX, dy, dz + backZ);
                    level.setBlock(target, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }

    public void spawnResultItem(Level level, BlockPos origin, String resultItemId, int count) {
        Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(resultItemId));
        if (item != null) {
            ItemStack stack = new ItemStack(item, count);
            ItemEntity itemEntity = new ItemEntity(level,
                    origin.getX() + 0.5,
                    origin.getY() + 1,
                    origin.getZ() + 0.5,
                    stack);
            level.addFreshEntity(itemEntity);
        } else {
            System.out.println("Unknown item ID: " + resultItemId);
        }
    }

    public void spawnParticleRing(ServerLevel level, BlockPos center, ParticleOptions particleType, double radius, int count) {
        double cx = center.getX() + 0.5;
        double cy = center.getY() + 1.0; // fixed spawn height
        double cz = center.getZ() + 0.5;

        for (int i = 0; i < count; i++) {
            double angle = 2 * Math.PI * i / count;
            double x = cx + radius * Math.cos(angle);
            double z = cz + radius * Math.sin(angle);

            level.sendParticles(particleType, x, cy, z, 1, 0, 0, 0, 0);
        }
    }
}
