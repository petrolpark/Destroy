package com.petrolpark.destroy.block.entity;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.petrolpark.destroy.block.CoolerBlock;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.util.DestroyLang;
import com.petrolpark.destroy.util.PollutionHelper;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.foundation.utility.animation.LerpedFloat.Chaser;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class CoolerBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    public static final int MAX_COOLING_TICKS = 12000; // 10 minutes; how much the Cooler will fill before stopping any excess Fluid
    private static final int TANK_CAPACITY = 1000;

    private SmartFluidTankBehaviour tank;

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
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 1, TANK_CAPACITY, true)
            .whenFluidUpdates(this::consumeFluid)
            .forbidExtraction();
        behaviours.add(tank);
    };

    /**
     * Use up the Fluid in this Cooler, turning it into ticks of cooling.
     */
    private void consumeFluid() {
        if (!hasLevel()) return;

        FluidStack fluidStack = tank.getPrimaryHandler().getFluid();
        if (DestroyFluids.isMixture(fluidStack)) {

            int amount = fluidStack.getAmount();
            ReadOnlyMixture mixture = ReadOnlyMixture.readNBT(ReadOnlyMixture::new, fluidStack.getOrCreateChildTag("Mixture"));

            float totalMolesPerBucket = 0f;
            float totalRefrigerantMolesPerBucket = 0f;
            float coolingPower = 0f;
            for (Molecule molecule : mixture.getContents(true)) {
                float concentration = mixture.getConcentrationOf(molecule);
                totalMolesPerBucket += concentration;
                if (molecule.hasTag(DestroyMolecules.Tags.REFRIGERANT)) {
                    totalRefrigerantMolesPerBucket += concentration;
                    coolingPower += concentration * amount * molecule.getMolarHeatCapacity() / 100;
                };
            };

            coolingPower *= totalRefrigerantMolesPerBucket / totalMolesPerBucket; // Scale the effectiveness of the refrigerant with its purity

            if (coolingPower > 0f) {

                // Begin cooling
                setColdnessOfBlock(ColdnessLevel.FROSTING);
                coolingTicks += coolingPower;

                // Stop insertion if necessary
                if (coolingTicks >= MAX_COOLING_TICKS) {
                    tank.forbidInsertion();
                };
            };
        };

        // Discard the Fluid
        tank.getPrimaryHandler().drain(TANK_CAPACITY, FluidAction.EXECUTE);
        PollutionHelper.pollute(getLevel(), fluidStack);

        notifyUpdate();
    };

    @Override
    @SuppressWarnings("null")
    public void tick() {
        super.tick();

        if (!hasLevel()) return;

        if (getLevel().isClientSide()) { // Level is not null I checked
            tickAnimation();
            if (!isVirtual()) {
                spawnParticles(getColdnessFromBlock());
            };
            return;
        };
        
        if (coolingTicks > 0) {
            coolingTicks--;
            if (coolingTicks < MAX_COOLING_TICKS) {
                tank.allowInsertion();
            };
            if (coolingTicks <= 0) {
                setColdnessOfBlock(ColdnessLevel.IDLE);
            };
            sendData();
        };
    };

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        coolingTicks = tag.getInt("Timer");
    };

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("Timer", coolingTicks);
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

    /**
     * Essentially trick the game into thinking this is a Blaze Burner.
     * The 'FROSTING' value gets {@link com.petrolpark.destroy.mixin.HeatLevelMixin mixed in} to the Heat Level enum.
     * @param coldnessLevel
     */
    @SuppressWarnings("null")
    public void updateHeatLevel(ColdnessLevel coldnessLevel) {
        if (!hasLevel()) return;
        BlockState newState = getBlockState().setValue(BlazeBurnerBlock.HEAT_LEVEL, coldnessLevel == ColdnessLevel.FROSTING ? HeatLevel.valueOf("FROSTING") : HeatLevel.NONE);
        if (!newState.equals(getBlockState())) {
            getLevel().setBlockAndUpdate(getBlockPos(), newState); // Level is not null
        };
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

    @SuppressWarnings("null")
    protected void spawnParticles(ColdnessLevel coldnessLevel) {
		if (!hasLevel()) return;
		if (coldnessLevel == ColdnessLevel.NONE) return;

		RandomSource r = getLevel().getRandom(); // It thinks getLevel() might be null (it's not)

		Vec3 c = VecHelper.getCenterOf(getBlockPos());
		Vec3 v = c.add(VecHelper.offsetRandomly(Vec3.ZERO, r, .125f)
			.multiply(1, 0, 1));
		
		if (r.nextInt(coldnessLevel == ColdnessLevel.IDLE ? 32 : 2) != 0) return; // Spawn more Particles if we're Frosting vs not doing anything

		boolean empty = level.getBlockState(getBlockPos().above()) // If the Block above is occupied we want to spawn fewer Particles
			.getCollisionShape(getLevel(), getBlockPos().above())
			.isEmpty();
		
		if (empty || r.nextInt(8) == 0) getLevel().addParticle(ParticleTypes.SNOWFLAKE, v.x, v.y, v.z, 0, 0.07D, 0);
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
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER && side == Direction.DOWN) {
            return tank.getCapability().cast();
        };
        return super.getCapability(cap, side);
    };

    @Override
    public void invalidate() {
        super.invalidate();
        tank.getCapability().invalidate();
    };

    public ColdnessLevel getColdnessFromBlock() {
        return CoolerBlock.getColdnessLevelOf(getBlockState());
    };

    @SuppressWarnings("null")
    public void setColdnessOfBlock(ColdnessLevel coldnessLevel) {
        if (!hasLevel()) return;
        getLevel().setBlockAndUpdate(getBlockPos(), getBlockState().setValue(CoolerBlock.COLD_LEVEL, coldnessLevel)); // It thinks getLevel() might be null (it's not)
        updateHeatLevel(coldnessLevel);
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
        if (coolingTicks <= 0) return false;
        final String timeRemaining;
        int seconds = (coolingTicks % 1200) / 20;
        if (coolingTicks < 72000) {
            timeRemaining = "" + coolingTicks / 1200 + ":" + (seconds < 10 ? "0" : "") + seconds;
        } else {
            timeRemaining = DestroyLang.translate("tooltip.cooler.long_time_remaining").string();
        };
        DestroyLang.translate("tooltip.cooler.time_remaining", timeRemaining)
            .forGoggles(tooltip);
        return true;
    };
    
};
