package com.petrolpark.destroy.block.entity;

import java.util.List;

import com.petrolpark.destroy.block.CoolerBlock;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.foundation.utility.animation.LerpedFloat.Chaser;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class CoolerBlockEntity extends SmartTileEntity implements IHaveGoggleInformation {

    private static final int TANK_CAPACITY = 1000;

    private SmartFluidTankBehaviour tank;
    protected LazyOptional<IFluidHandler> fluidCapability;

    private int coolingTicks; // How many ticks this Cooler has left of cooling
    protected LerpedFloat headAnimation;
	protected LerpedFloat headAngle;

    public CoolerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        coolingTicks = 0;
        headAnimation = LerpedFloat.linear();
		headAngle = LerpedFloat.angular();

        headAngle.startWithValue((AngleHelper.horizontalAngle(Direction.NORTH) + 180) % 360);
    };

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {
        tank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, TANK_CAPACITY, true)
            .whenFluidUpdates(this::consumeFluid)
            .forbidExtraction();
        behaviours.add(tank);
        fluidCapability = LazyOptional.of(() -> {
            return new CombinedTankWrapper(tank.getCapability().orElse(null));
        });
    };

    /**
     * Use up the Fluid in this Cooler, turning it into ticks of cooling
     */
    private void consumeFluid() {

    };

    @Override
    @SuppressWarnings("null")
    public void tick() {
        super.tick();
        if (!hasLevel()) return;
        if (getLevel().isClientSide()) { // Level is not null I checked
            tickAnimation();
            return;
        };
        if (coolingTicks > 0) {
            coolingTicks--;
        };
    };

    public SmartFluidTank getInputTank() {
        return tank.getPrimaryHandler();
    };

    public LerpedFloat getHeadAnimation() {
        return headAnimation;
    };

    public LerpedFloat getHeadAngle() {
        return headAngle;
    };

    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("resource")
	private void tickAnimation() {

        float target = 0;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && !player.isInvisible()) {
            double x;
            double z;
            if (isVirtual()) {
                x = -4;
                z = -10;
            } else {
                x = player.getX();
                z = player.getZ();
            };
            double dx = x - (getBlockPos().getX() + 0.5);
            double dz = z - (getBlockPos().getZ() + 0.5);
            target = AngleHelper.deg(-Mth.atan2(dz, dx)) - 90;
        };
        target = headAngle.getValue() + AngleHelper.getShortestAngleDiff(headAngle.getValue(), target);
        headAngle.chase(target, 0.25f, Chaser.exp(5)); // Follow the Player
        headAngle.tickChaser();

		headAnimation.chase(validBlockAbove() ? 1 : 0, 0.25f, Chaser.exp(0.25f)); // Ensure the Head is at the right level if there's a Basin above
		headAnimation.tickChaser();
	};

    /**
     * Whether the Cooler interacts with the Block State above it.
     */
    @SuppressWarnings("null")
    private boolean validBlockAbove() {
        if (!hasLevel()) return false;
        BlockState blockState = getLevel().getBlockState(worldPosition.above()); // Level isn't null I checked
		return AllBlocks.BASIN.has(blockState) || blockState.getBlock() instanceof FluidTankBlock;
    };

    @Override
    public void invalidate() {
        super.invalidate();
        fluidCapability.invalidate();
    };

    public ColdnessLevel getColdnessFromBlock() {
        return CoolerBlock.getColdnessLevelOf(getBlockState());
    };

    public static enum ColdnessLevel implements StringRepresentable {
        NONE,
        IDLE,
        FROSTING;

        @Override
        public String getSerializedName() {
            return Lang.asId(name());
        };
    };

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return false;
    };
    
};
