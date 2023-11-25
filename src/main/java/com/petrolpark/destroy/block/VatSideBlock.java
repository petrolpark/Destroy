package com.petrolpark.destroy.block;

import javax.annotation.Nullable;

import com.petrolpark.destroy.block.entity.DestroyBlockEntityTypes;
import com.petrolpark.destroy.block.entity.VatSideBlockEntity;
import com.petrolpark.destroy.block.entity.VatSideBlockEntity.DisplayType;
import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import com.simibubi.create.content.decoration.copycat.CopycatBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntityTicker;

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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

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
                case PIPE: {
                    return InteractionResult.PASS;
                } case NORMAL: {
                    vatSide.setDisplayType(vatSide.direction == Direction.UP ? DisplayType.OPEN_VENT : DisplayType.THERMOMETER);
                    return InteractionResult.SUCCESS;
                } case THERMOMETER: {
                    vatSide.setDisplayType(DisplayType.BAROMETER);
                    return InteractionResult.SUCCESS;
                } case BAROMETER: case OPEN_VENT: case CLOSED_VENT: {
                    vatSide.setDisplayType(DisplayType.NORMAL);
                    return InteractionResult.SUCCESS;
                } default:
                    return InteractionResult.PASS;  
            }
        });
    };

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
		return InteractionResult.PASS;
	};

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        withBlockEntityDo(level, currentPos, be -> {
            if (!(be instanceof VatSideBlockEntity vatSide)) return;
            vatSide.updateRedstone();
            if (facing != vatSide.direction) return;
            vatSide.updateDisplayType(facingPos);
            vatSide.setPowerFromAdjacentBlock(facingPos);   
        });
        return state;
    };

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        withBlockEntityDo(level, pos, be -> {
            if (!(be instanceof VatSideBlockEntity vatSide) || vatSide.direction == null) return;
            if (!pos.relative(vatSide.direction).equals(neighbor)) return;
            vatSide.updateDisplayType(neighbor);
            vatSide.setPowerFromAdjacentBlock(neighbor);   
        });
        super.onNeighborChange(state, level, pos, neighbor);
    };

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        withBlockEntityDo(level, pos, be -> {
            if (!(be instanceof VatSideBlockEntity vatSide)) return;
            vatSide.updateRedstone();
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
