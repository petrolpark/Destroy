package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.simibubi.create.content.fluids.drain.ItemDrainBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ItemDrainBlock.class)
public abstract class ItemDrainBlockMixin extends Block {
  
    public ItemDrainBlockMixin(Properties properties) {
        super(properties); // This should never get called
    };

    @Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		IBE.onRemove(state, worldIn, pos, newState);
	};
};
