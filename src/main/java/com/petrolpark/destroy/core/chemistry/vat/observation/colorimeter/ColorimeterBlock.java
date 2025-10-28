package com.petrolpark.destroy.core.chemistry.vat.observation.colorimeter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.petrolpark.destroy.DestroyBlockEntityTypes;
import com.petrolpark.destroy.DestroyFluids;
import com.petrolpark.destroy.chemistry.legacy.LegacySpecies;
import com.petrolpark.destroy.chemistry.legacy.ReadOnlyMixture;
import com.petrolpark.destroy.core.chemistry.storage.IMixtureStorageItem;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;

import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.DistExecutor;

public class ColorimeterBlock extends HorizontalDirectionalBlock implements IBE<ColorimeterBlockEntity>, IWrenchable {

    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final BooleanProperty BLUSHING = BooleanProperty.create("blushing");

    public ColorimeterBlock(Properties properties) {
        super(properties);
        registerDefaultState(
            defaultBlockState()
            .setValue(FACING, Direction.NORTH)
            .setValue(BLUSHING, false)
            .setValue(POWERED, false)
        );
    };

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING).add(BLUSHING).add(POWERED);
    };

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (AllItems.WRENCH.isIn(player.getItemInHand(hand))) return InteractionResult.PASS;
        return onBlockEntityUse(level, pos, be -> {
            List<FluidStack> fluids = new ArrayList<>();
            Set<LegacySpecies> species = new HashSet<>();
            species.add(null);

            be.getVatOptional().ifPresent(vat -> {
                fluids.add(vat.getLiquidTankContents());
                fluids.add(vat.getGasTankContents());
            });

            player.getInventory().items.forEach(stack -> {
                if (stack.getItem() instanceof IMixtureStorageItem mixtureItem) fluids.add(mixtureItem.getContents(stack).orElse(FluidStack.EMPTY));
            });

            for (FluidStack fluid : fluids) {
                if (DestroyFluids.isMixture(fluid)) species.addAll(ReadOnlyMixture.readNBT(ReadOnlyMixture::new, fluid.getOrCreateChildTag("Mixture")).getContents(false));
            };

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> openScreen(be, species, player));
            return InteractionResult.SUCCESS;
        });
    };

    @OnlyIn(Dist.CLIENT)
    public void openScreen(ColorimeterBlockEntity be, Set<LegacySpecies> species, Player player) {
        if(player instanceof LocalPlayer)
            ScreenOpener.open(new ColorimeterScreen(be, new ArrayList<>(species)));
    };

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (direction != state.getValue(FACING).getOpposite()) return 0;
        return getBlockEntityOptional(level, pos).map(c -> c.redstoneMonitor.getStrength()).orElse(0);
    };

    @Override
	public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
		return getBlockEntityOptional(level, pos).map(c -> c.redstoneMonitor.getStrength()).orElse(0);
	};

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean isMoving) {
        withBlockEntityDo(level, pos, cbe -> cbe.onNeighborChanged(neighborPos));
    };

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        return checkForSmartObserver(state, level, currentPos);
    };

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return checkForSmartObserver(defaultBlockState().setValue(FACING, context.getHorizontalDirection()), context.getLevel(), context.getClickedPos());
    };

    public BlockState checkForSmartObserver(BlockState colorimeter, LevelAccessor level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (level.getBlockState(pos.relative(direction)).getBlock().equals(AllBlocks.SMART_OBSERVER.get())) return colorimeter.setValue(BLUSHING, true);
        };
        return colorimeter.setValue(BLUSHING, false);
    }

    @Override
    public Class<ColorimeterBlockEntity> getBlockEntityClass() {
        return ColorimeterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ColorimeterBlockEntity> getBlockEntityType() {
        return DestroyBlockEntityTypes.COLORIMETER.get();
    };
    
};
