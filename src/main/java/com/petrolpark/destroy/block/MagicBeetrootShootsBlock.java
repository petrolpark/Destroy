package com.petrolpark.destroy.block;

import com.petrolpark.destroy.block.shape.DestroyShapes;
import com.petrolpark.destroy.item.DestroyItems;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.PlantType;

public class MagicBeetrootShootsBlock extends BushBlock implements BonemealableBlock {

    public MagicBeetrootShootsBlock(Properties properties) {
        super(properties);
    };

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return DestroyShapes.MAGIC_BEETROOT_SEEDS;
    };

    @Override
    public PlantType getPlantType(BlockGetter level, BlockPos pos) {
        return PlantType.CROP;
    };

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return (level.getRawBrightness(pos, 0) >= 8 || level.canSeeSky(pos)) && super.canSurvive(state, level, pos);
    };

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(Blocks.FARMLAND);
    };

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof Ravager && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(level, entity)) {
            level.destroyBlock(pos, true, entity);
        };

        super.entityInside(state, level, pos, entity);
    };

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (level.getRawBrightness(pos, 0) >= 9) {
            if (ForgeHooks.onCropsGrowPre(level, pos, state, random.nextInt(26) == 0)) {
                level.setBlockAndUpdate(pos, getGrowthResult(random));
                ForgeHooks.onCropsGrowPost(level, pos, state);
            };
        };
    };

    @Override
    public ItemStack getCloneItemStack(BlockGetter pLevel, BlockPos pPos, BlockState pState) {
        return DestroyItems.MAGIC_BEETROOT_SEEDS.asStack();
    };

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    };

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    };

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        level.setBlockAndUpdate(pos, getGrowthResult(random));
    };

    public static BlockState getGrowthResult(RandomSource random) {
        int roll = random.nextInt(22);
        if (roll <= 1) {
            return DestroyBlocks.COAL_INFUSED_BEETROOT.getDefaultState();
        } else if (roll <= 3) {
            return DestroyBlocks.COPPER_INFUSED_BEETROOT.getDefaultState();
        } else if (roll <= 4) {
            return DestroyBlocks.DIAMOND_INFUSED_BEETROOT.getDefaultState();
        } else if (roll <= 5) {
            return DestroyBlocks.EMERALD_INFUSED_BEETROOT.getDefaultState();
        } else if (roll <= 8) {
            return DestroyBlocks.FLUORITE_INFUSED_BEETROOT.getDefaultState();
        } else if (roll <= 10) {
            return DestroyBlocks.GOLD_INFUSED_BEETROOT.getDefaultState();
        } else if (roll <= 12) {
            return DestroyBlocks.IRON_INFUSED_BEETROOT.getDefaultState();
        } else if (roll <= 15) {
            return DestroyBlocks.LAPIS_INFUSED_BEETROOT.getDefaultState();
        } else if (roll <= 17) {
            return DestroyBlocks.NICKEL_INFUSED_BEETROOT.getDefaultState();
        } else if (roll <= 19) {
            return DestroyBlocks.REDSTONE_INFUSED_BEETROOT.getDefaultState();
        } else {
            return DestroyBlocks.ZINC_INFUSED_BEETROOT.getDefaultState();
        }
    };
    
};
