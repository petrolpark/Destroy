package com.petrolpark.destroy.content.processing.trypolithography.keypunch;

import com.petrolpark.compat.create.block.entity.behaviour.AbstractRememberPlacerBehaviour;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.DestroyBlockEntityTypes;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;

import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

public class KeypunchBlock extends HorizontalKineticBlock implements IBE<KeypunchBlockEntity>, ICogWheel {
    
    public static final ResourceLocation NAME_LIST_ID = Destroy.asResource("keypunch");

    public KeypunchBlock(Properties properties) {
        super(properties);
    };

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(HORIZONTAL_FACING, Direction.NORTH);
    };

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        AbstractRememberPlacerBehaviour.setPlacedBy(level, pos, placer);
    };

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (AllItems.WRENCH.isIn(player.getItemInHand(hand))) return InteractionResult.PASS;

        return onBlockEntityUse(level, pos, be -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> displayScreen(be, player));
            return InteractionResult.SUCCESS;
        });
    };

    @Override
    public Axis getRotationAxis(BlockState state) {
        return Axis.Y;
    };

    @Override
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
		return false;
	};

    @OnlyIn(value = Dist.CLIENT)
	protected void displayScreen(KeypunchBlockEntity be, Player player) {
		if (!(player instanceof LocalPlayer)) return;
		if (be.getBlockState() == null) return;
		ScreenOpener.open(new KeypunchScreen(be));
	};

    @Override
    public Class<KeypunchBlockEntity> getBlockEntityClass() {
        return KeypunchBlockEntity.class;
    };

    @Override
    public BlockEntityType<? extends KeypunchBlockEntity> getBlockEntityType() {
        return DestroyBlockEntityTypes.KEYPUNCH.get();
    };
    
};
