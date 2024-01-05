package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.simibubi.create.content.fluids.spout.SpoutBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(SpoutBlock.class)
public abstract class SpoutBlockMixin extends Block {
  
    public SpoutBlockMixin(Properties properties) {
        super(properties); //This should never get called
    };

    @Override
    @SuppressWarnings("deprecation")
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, worldIn, pos, newState, isMoving);
		IBE.onRemove(state, worldIn, pos, newState);
	};
};
