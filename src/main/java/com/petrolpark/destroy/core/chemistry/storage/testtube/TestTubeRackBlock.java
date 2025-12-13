package com.petrolpark.destroy.core.chemistry.storage.testtube;

import java.util.List;

import com.petrolpark.destroy.DestroyBlockEntityTypes;
import com.petrolpark.destroy.DestroyVoxelShapes;
import com.petrolpark.destroy.compat.vs2.DestroyVSUtil;
import com.petrolpark.destroy.core.chemistry.storage.IMixtureStorageItem;
import com.petrolpark.destroy.core.chemistry.storage.ISpecialMixtureContainerBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.item.ItemHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.ModList;

public class TestTubeRackBlock extends Block implements IBE<TestTubeRackBlockEntity>, IWrenchable, ISpecialMixtureContainerBlock {

    public static final BooleanProperty X = BooleanProperty.create("x");

    public TestTubeRackBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(X, true));
    };

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(X);
    };

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(X, context.getHorizontalDirection().getAxis() == Axis.Z);
    };

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(X) ? DestroyVoxelShapes.TEST_TUBE_RACK_X : DestroyVoxelShapes.TEST_TUBE_RACK_Z;
    };

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide() || hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        int tube = getTargetedTube(level, state, pos, hit);
        if (tube == -1) return InteractionResult.PASS;
        ItemStack stack = player.getItemInHand(hand);
        return onBlockEntityUse(level, pos, be -> {
            ItemStack oldStack = be.inv.getStackInSlot(tube).copy();
            if (stack.isEmpty() && oldStack.isEmpty()) return InteractionResult.PASS;
            if (!be.inv.isItemValid(tube, stack) && !stack.isEmpty()) return InteractionResult.FAIL;
            be.inv.setStackInSlot(tube, stack.copy());
            stack.shrink(1);
            if (!oldStack.isEmpty()) {
                if (stack.isEmpty()) {
                    player.setItemInHand(hand, oldStack); 
                } else {
                    player.getInventory().placeItemBackInInventory(oldStack);
                };
            };
            return InteractionResult.SUCCESS;
        });
    };

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        withBlockEntityDo(world, pos, be -> ItemHelper.dropContents(world, pos, be.inv));
        super.onRemove(state, world, pos, newState, isMoving);
    };

    /**
     * @param state
     * @param pos
     * @param result
     * @return {@code -1} if there is no collision or {@code 0} to {@code 3} depending on which tube is hit
     */
    public static int getTargetedTube(Level level, BlockState state, BlockPos pos, BlockHitResult result) {
        Vec3 hit = result.getLocation();

        List<AABB> boxes = List.of(
                getTubeSelectionBox(state, pos, 0),
                getTubeSelectionBox(state, pos, 1),
                getTubeSelectionBox(state, pos, 2),
                getTubeSelectionBox(state, pos, 3)
        );

        int hitIndex = -1;
        double minDist = Double.MAX_VALUE;

        for (int i = 0; i < boxes.size(); i++) {
            AABB box = boxes.get(i);

            if (ModList.get().isLoaded("valkyrienskies")) {
                // center of THIS box
                Vec3 center = new Vec3(
                        (box.minX + box.maxX) * 0.5,
                        (box.minY + box.maxY) * 0.5,
                        (box.minZ + box.maxZ) * 0.5
                );
                box = DestroyVSUtil.AABBtoWorld(level, center, box);
            }
            if (!box.contains(hit)) continue;

            double d = hit.distanceToSqr(box.getCenter());
            if (d < minDist) {
                minDist = d;
                hitIndex = i;
            }
        }

        return hitIndex;
    };

    public static AABB getTubeBox(BlockState state, BlockPos pos, int tube) {
        if (tube <0 || tube >= 4) return new AABB(0d, 0d, 0d, 0d, 0d, 0d);
        boolean x = state.getValue(X);
        double boxStart = tube * 4 /16d;
        return new AABB(Vec3.atLowerCornerOf(pos).add(x ? boxStart + 0.5 /16d: 6.5 / 16d, 2.1 / 16d, x ? 6.5 / 16d : boxStart + 0.5 / 16d), Vec3.atLowerCornerOf(pos).add(x ? boxStart + 3.5 / 16d: 9.5 / 16d, 10 / 16d, x ? 9.5 / 16d : boxStart + 3.5 / 16d));
    };

    public static AABB getTubeSelectionBox(BlockState state, BlockPos pos, int tube) {
        if (tube < 0 || tube >= 4) return new AABB(0d, 0d, 0d, 0d, 0d, 0d);
        boolean x = state.getValue(X);
        double boxStart = tube * 4 / 16d;

        double alongMin = boxStart + 0.5 / 16d;
        double alongMax = boxStart + 3.5 / 16d;

        double perpMin = 1.0 / 16d;
        double perpMax = 15.0 / 16d;

        double minX = x ? alongMin : perpMin;
        double maxX = x ? alongMax : perpMax;
        double minZ = x ? perpMin : alongMin;
        double maxZ = x ? perpMax : alongMax;

        return new AABB(
                Vec3.atLowerCornerOf(pos).add(minX, 2.1 / 16d, minZ),
                Vec3.atLowerCornerOf(pos).add(maxX, 10   / 16d, maxZ)
        );
    }


    @Override
    public Class<TestTubeRackBlockEntity> getBlockEntityClass() {
        return TestTubeRackBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends TestTubeRackBlockEntity> getBlockEntityType() {
        return DestroyBlockEntityTypes.TEST_TUBE_RACK.get();
    }

    @Override
    public IFluidHandler getTankForMixtureStorageItems(IMixtureStorageItem item, Level level, BlockPos pos, BlockState state, Direction face, Player player, InteractionHand hand, ItemStack stack, boolean rightClick) {
//        TestTubeRackBlockEntity be = getBlockEntity(level, pos);
//        if (be == null) return null;
//        int tube = getTargetedTube(state, pos, player);
//        if (tube == -1) return null;
//        ItemStack tubeStack = be.inv.getStackInSlot(tube);
//        return tubeStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        TestTubeRackBlockEntity be = getBlockEntity(level, pos);
        if (be == null) return null;

        // Do a proper block pick to get a BlockHitResult
        // 0f partialTicks is fine here since this is a discrete interaction
        BlockHitResult hit = (BlockHitResult) player.pick(player.getBlockReach(), 0f, false);

        // Make sure we're actually targeting this block
        if (!hit.getBlockPos().equals(pos)) return null;

        // Use the ship-safe hit-based tube selection
        int tube = getTargetedTube(level, state, pos, hit);
        if (tube == -1) return null;

        ItemStack tubeStack = be.inv.getStackInSlot(tube);
        if (tubeStack.isEmpty()) return null;

        return tubeStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM)
                .orElse(null);
    };
    
};
