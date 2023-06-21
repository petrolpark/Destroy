package com.petrolpark.destroy.block;

import javax.annotation.Nullable;

import com.petrolpark.destroy.block.entity.DestroyBlockEntityTypes;
import com.petrolpark.destroy.block.entity.VatSideBlockEntity;
import com.petrolpark.destroy.block.entity.VatSideBlockEntity.DisplayType;
import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import com.simibubi.create.content.decoration.copycat.CopycatBlockEntity;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntityTicker;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;

public class VatSideBlock extends CopycatBlock {

    public VatSideBlock(Properties properties) {
        super(properties);
    };

    @Override
    @Nullable
    public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level level, BlockState state, BlockEntityType<S> blockEntityType) {
        return new SmartBlockEntityTicker<>();
    };

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.PASS;
    };

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return onBlockEntityUse(context.getLevel(), context.getClickedPos(), be -> {
            if (!(be instanceof VatSideBlockEntity vatSide)) return InteractionResult.PASS;
            switch (vatSide.getDisplayType()) {
                case PIPE:
                        return InteractionResult.PASS;
                case NORMAL: {
                    vatSide.setDisplayType(DisplayType.THERMOMETER);
                    return InteractionResult.SUCCESS;
                } case THERMOMETER: {
                    vatSide.setDisplayType(DisplayType.BAROMETER);
                    return InteractionResult.SUCCESS;
                } case BAROMETER: {
                    vatSide.setDisplayType(DisplayType.NORMAL);
                    return InteractionResult.SUCCESS;
                } default:
                    return InteractionResult.PASS;
            }
        });
    };

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        FluidTransportBehaviour behaviour = BlockEntityBehaviour.get(level, neighbor, FluidTransportBehaviour.TYPE);
        withBlockEntityDo(level, pos, be -> {
            if (!(be instanceof VatSideBlockEntity vatSide)) return;
            boolean nextToPipe = behaviour == null ? false : behaviour.canHaveFlowToward(state, vatSide.direction) || behaviour.canPullFluidFrom(FluidStack.EMPTY, state, vatSide.direction);
            if (vatSide.getDisplayType() == DisplayType.NORMAL && nextToPipe) {
                vatSide.setDisplayType(DisplayType.PIPE);
            } else if (vatSide.getDisplayType() == DisplayType.PIPE && !nextToPipe) {
                vatSide.setDisplayType(DisplayType.NORMAL);
            };
        });
    };

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        IBE.onRemove(state, level, pos, newState);
        super.onRemove(state, level, pos, newState, isMoving);
    };

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        // Do nothing (this Block should never be placed by a Player)
    };

    @Override
    public boolean canFaceBeOccluded(BlockState state, Direction face) {
        return true;
    };

    @Override
    public boolean canConnectTexturesToward(BlockAndTintGetter reader, BlockPos fromPos, BlockPos toPos, BlockState state) {
        return true;
    };

    @Override
    @SuppressWarnings("deprecation")
    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
        return getMaterial(level, pos).getBlock().skipRendering(state, neighborState, dir);
    };

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return getMaterial(level, pos).getShadeBrightness(level, pos);
    };
  
    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return getMaterial(reader, pos).propagatesSkylightDown(reader, pos);
    };

    @Override
    public BlockEntityType<? extends CopycatBlockEntity> getBlockEntityType() {
        return DestroyBlockEntityTypes.VAT_SIDE.get();
    };
    
};
