package com.petrolpark.destroy.block;

import javax.annotation.Nullable;

import com.petrolpark.destroy.block.entity.DestroyBlockEntityTypes;
import com.petrolpark.destroy.block.entity.ExtrusionDieBlockEntity;
import com.petrolpark.destroy.block.entity.behaviour.DestroyAdvancementBehaviour;
import com.petrolpark.destroy.block.shape.DestroyShapes;
import com.petrolpark.destroy.world.DestroyDamageSources;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ExtrusionDieBlock extends RotatedPillarBlock implements IBE<ExtrusionDieBlockEntity>, IWrenchable {

    public ExtrusionDieBlock(Properties properties) {
        super(properties);
    };

    @Nullable
	@Override
	public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level pLevel, BlockState pState, BlockEntityType<S> pBlockEntityType) {
		return null;
	};

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return DestroyShapes.EXTRUSION_DIE.get(state.getValue(AXIS));
    };

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        DestroyAdvancementBehaviour.setPlacedBy(level, pos, placer);
        super.setPlacedBy(level, pos, state, placer, stack);
    };

    /**
     * Copied from the {@link net.minecraft.world.level.block.SweetBerryBushBlock#entityInside Minecraft source code}.
     */
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity) {
            entity.makeStuckInBlock(state, new Vec3((double)0.8F, 0.75D, (double)0.8F));
            if (!level.isClientSide() && (entity.xOld != entity.getX() || entity.zOld != entity.getZ())) {
                double d0 = Math.abs(entity.getX() - entity.xOld);
                double d1 = Math.abs(entity.getZ() - entity.zOld);
                if (d0 >= (double)0.003f || d1 >= (double)0.003f) {
                    entity.hurt(DestroyDamageSources.extrusionDie(level), 3f);
                };
            };
        };
    }

    @Override
    public Class<ExtrusionDieBlockEntity> getBlockEntityClass() {
        return ExtrusionDieBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends ExtrusionDieBlockEntity> getBlockEntityType() {
        return DestroyBlockEntityTypes.EXTRUSION_DIE.get();
    };

};
