package com.petrolpark.destroy.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.destroy.core.pollution.PollutionHelper;
import com.petrolpark.destroy.DestroyTags;
import com.petrolpark.destroy.core.pollution.Pollution.PollutionType;
import net.createmod.catnip.data.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Block.class)
public class BlockMixin {
    
    @Inject(method = "handlePrecipitation", at = @At(value = "HEAD"), cancellable = true)
    public void inHandlePrecipitation(BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation, CallbackInfo ci) {
        if (precipitation != Biome.Precipitation.RAIN) return;
        for (Pair<BlockState, BlockPos> pair : List.of(Pair.of(level.getBlockState(pos.above()), pos.above()), Pair.of(state, pos))) { // As only solid (non-plant) Blocks on the surface are checked for weather, check the Block above as well
            if (state.isAir()) continue;
            if (!(DestroyTags.Blocks.ACID_RAIN_DESTRUCTIBLE.matches(pair.getFirst().getBlock()) || DestroyTags.Blocks.ACID_RAIN_DIRT_REPLACEABLE.matches(pair.getFirst().getBlock()))) continue;
            if (level.random.nextInt(PollutionType.ACID_RAIN.max) <= PollutionHelper.getPollution(level, pos, PollutionType.ACID_RAIN)) {
                if (DestroyTags.Blocks.ACID_RAIN_DESTRUCTIBLE.matches(pair.getFirst().getBlock())) {
                    level.destroyBlock(pair.getSecond(), false);
                } else if (DestroyTags.Blocks.ACID_RAIN_DIRT_REPLACEABLE.matches(pair.getFirst().getBlock()) && level.random.nextInt(10) == 0) {
                    level.setBlockAndUpdate(pair.getSecond(), Blocks.DIRT.defaultBlockState());
                };
                if (state == pair.getFirst()) ci.cancel();
            };
        };
    };
};
