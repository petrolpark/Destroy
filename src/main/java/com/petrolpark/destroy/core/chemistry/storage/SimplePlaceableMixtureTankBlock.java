package com.petrolpark.destroy.core.chemistry.storage;

import java.util.function.IntSupplier;

import org.joml.Vector3f;

import com.petrolpark.destroy.DestroyBlockEntityTypes;
import com.petrolpark.destroy.core.chemistry.storage.SimpleMixtureTankBlockEntity.SimplePlaceableMixtureTankBlockEntity;
import net.createmod.catnip.data.Couple;
import com.tterrag.registrate.util.nullness.NonNullFunction;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SimplePlaceableMixtureTankBlock extends PlaceableMixtureTankBlock<SimplePlaceableMixtureTankBlockEntity> {

    protected final IntSupplier capacity;
    protected final Couple<Vector3f> fluidBoxDimensions;
    protected final VoxelShape shape;

    public SimplePlaceableMixtureTankBlock(Properties properties, IntSupplier capacity, Couple<Vector3f> fluidBoxDimensions, VoxelShape shape) {
        super(properties);
        this.capacity = capacity;
        this.fluidBoxDimensions = fluidBoxDimensions;
        this.shape = shape;
    };

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shape;
    };

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null && be instanceof SimpleMixtureTankBlockEntity tankBE) {
            return tankBE.luminosity;
        };
        return super.getLightEmission(state, level, pos);
    };

    @Override
    public int getMixtureCapacity() {
        return capacity.getAsInt();
    };

    public static NonNullFunction<Properties, SimplePlaceableMixtureTankBlock> of(IntSupplier capacity, float lx, float ly, float lz, float ux, float uy, float uz, VoxelShape shape) {
        return (p) -> new SimplePlaceableMixtureTankBlock(p, capacity, Couple.create(new Vector3f(lx, ly, lz), new Vector3f(ux, uy, uz)), shape);
    };
    
    public Couple<Vector3f> getFluidBoxDimensions() {
        return fluidBoxDimensions;
    };

    @Override
    public Class<SimplePlaceableMixtureTankBlockEntity> getBlockEntityClass() {
        return SimplePlaceableMixtureTankBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends SimplePlaceableMixtureTankBlockEntity> getBlockEntityType() {
        return DestroyBlockEntityTypes.SIMPLE_MIXTURE_TANK.get();
    };

    
};
