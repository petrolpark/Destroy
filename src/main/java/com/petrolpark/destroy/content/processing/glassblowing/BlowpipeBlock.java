package com.petrolpark.destroy.content.processing.glassblowing;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.petrolpark.compat.create.block.entity.behaviour.AbstractRememberPlacerBehaviour;
import com.petrolpark.destroy.DestroyBlockEntityTypes;
import com.petrolpark.destroy.DestroyBlocks;
import com.petrolpark.destroy.DestroyVoxelShapes;
import com.petrolpark.destroy.core.block.IPickUpPutDownBlock;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.simibubi.create.foundation.block.IBE;

import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlowpipeBlock extends DirectionalBlock implements IBE<BlowpipeBlockEntity>, IPickUpPutDownBlock {

    public BlowpipeBlock(Properties properties) {
        super(properties);
    };

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(FACING);
    };

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        AbstractRememberPlacerBehaviour.setPlacedBy(level, pos, placer);
        withBlockEntityDo(level, pos, be -> {
            be.readBlowing(stack.getOrCreateTag());
        });
    };

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getClickedFace();
        if (context.getPlayer() instanceof DeployerFakePlayer deployer) {
            DeployerBlockEntity be = getDeployerPlacer(context.getLevel(), deployer);
            if (be != null) facing = Direction.get(be.getSpeed() < 0f ? AxisDirection.NEGATIVE : AxisDirection.POSITIVE, AllBlocks.DEPLOYER.get().getRotationAxis(be.getBlockState()));
        };
        return super.getStateForPlacement(context).setValue(FACING, facing);
    };

    public static DeployerBlockEntity getDeployerPlacer(Level level, DeployerFakePlayer player) {
        for (Direction direction : Iterate.directions) {
            BlockPos pos = player.blockPosition().relative(direction, 2);
            Optional<DeployerBlockEntity> op = level.getBlockEntity(pos, AllBlockEntityTypes.DEPLOYER.get());
            if (op.filter(be -> be.getPlayer() == player).isPresent()) {
                if (op.get().getLevel() == level) return op.get();
                break;
            };
        };
        return null;
    };

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return DestroyVoxelShapes.BLOWPIPE.get(state.getValue(FACING).getAxis());
    };

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos, DestroyBlockEntityTypes.BLOWPIPE.get()).map(b -> b.luminosity).orElse(super.getLightEmission(state, level, pos));
    };

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        BlockEntity be = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        return Collections.singletonList(getItemStack(be));
    };

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return getItemStack(level.getBlockEntity(pos));
    };

    public ItemStack getItemStack(BlockEntity be) {
        ItemStack stack = DestroyBlocks.BLOWPIPE.asStack();
        if (be instanceof BlowpipeBlockEntity blowpipe) blowpipe.writeBlowing(stack.getOrCreateTag(), true);
        return stack;
    };

    @Override
    public Class<BlowpipeBlockEntity> getBlockEntityClass() {
        return BlowpipeBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends BlowpipeBlockEntity> getBlockEntityType() {
        return DestroyBlockEntityTypes.BLOWPIPE.get();
    };
    
};
