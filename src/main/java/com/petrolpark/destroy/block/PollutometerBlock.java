package com.petrolpark.destroy.block;

import com.petrolpark.destroy.block.entity.DestroyBlockEntities;
import com.petrolpark.destroy.block.entity.PollutometerBlockEntity;
import com.simibubi.create.foundation.block.ITE;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class PollutometerBlock extends Block implements ITE<PollutometerBlockEntity> {

    public static final DirectionProperty DIRECTION = BlockStateProperties.HORIZONTAL_FACING;

    public PollutometerBlock(Properties properties) {
        super(properties);
    };

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DIRECTION);
    };

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = this.defaultBlockState().setValue(DIRECTION, context.getHorizontalDirection().getOpposite());
        return blockstate;
    };

    @Override
    public Class<PollutometerBlockEntity> getTileEntityClass() {
        return PollutometerBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends PollutometerBlockEntity> getTileEntityType() {
        return DestroyBlockEntities.POLLUTOMETER.get();
    };
    
};
