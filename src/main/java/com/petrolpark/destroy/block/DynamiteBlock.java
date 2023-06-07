package com.petrolpark.destroy.block;

import org.jetbrains.annotations.Nullable;

import com.petrolpark.destroy.block.entity.DestroyBlockEntityTypes;
import com.petrolpark.destroy.block.entity.DynamiteBlockEntity;
import com.petrolpark.destroy.util.ExplosionHelper;
import com.petrolpark.destroy.world.explosion.ExcavationExplosion;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class DynamiteBlock extends Block implements IBE<DynamiteBlockEntity> {

    public DynamiteBlock(Properties properties) {
        super(properties);
    };

    public void explode(Level level, BlockPos pos, Entity source) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        //level.removeBlock(pos, false);
        withBlockEntityDo(serverLevel, pos, be -> {
            ExcavationExplosion excavationExplosion = new ExcavationExplosion(level, source, pos, new AABB(be.excavationAreaLowerCorner, be.excavationAreaUpperCorner));
            ExplosionHelper.explode(serverLevel, excavationExplosion);
        });
    };
    
    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    };

    @Override
    public void onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity igniter) {
        super.onCaughtFire(state, level, pos, direction, igniter);
        explode(level, pos, igniter);
    };

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (level.hasNeighborSignal(pos)) {
            explode(level, pos, null);  
        };
    };

    @Override
    public Class<DynamiteBlockEntity> getBlockEntityClass() {
        return DynamiteBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends DynamiteBlockEntity> getBlockEntityType() {
        return DestroyBlockEntityTypes.DYNAMITE.get();
    };
    
};
