package com.petrolpark.destroy.content.logistics.siphon;

import java.util.List;

import com.ibm.icu.text.DecimalFormat;
import com.petrolpark.destroy.DestroyAdvancementTrigger;
import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.core.block.entity.IHaveLabGoggleInformation;
import com.petrolpark.destroy.core.data.advancement.DestroyAdvancementBehaviour;
import com.petrolpark.destroy.core.fluid.GeniusFluidTankBehaviour;
import com.petrolpark.destroy.core.pollution.PollutingBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.Lang;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class SiphonBlockEntity extends SmartBlockEntity implements IHaveLabGoggleInformation {

    public SiphonFluidTankBehaviour tank;
    public int leftToDrain = 0;

    public ScrollValueBehaviour settings;

    public DestroyAdvancementBehaviour advancementBehaviour;
    private boolean giveAdvancementWhenEmptied = false;

    public SiphonBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = new SiphonFluidTankBehaviour();
        behaviours.add(tank);

        settings = new SiphonScrollValueBehaviour()
            .between(1, 10000);
        settings.setValue(1000);
        behaviours.add(settings);

        behaviours.add(new PollutingBehaviour(this));
        advancementBehaviour = new DestroyAdvancementBehaviour(this, DestroyAdvancementTrigger.SIPHON);
        behaviours.add(advancementBehaviour);
    };

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        leftToDrain = tag.getInt("LeftToDrain");
        if (tag.contains("GiveAdvancementWhenEmptied", Tag.TAG_BYTE)) giveAdvancementWhenEmptied = tag.getBoolean("GiveAdvancementWhenEmptied"); 
    };

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("LeftToDrain", leftToDrain);
        if (advancementBehaviour.getPlayer() != null) tag.putBoolean("GiveAdvanementWhenEmptied", giveAdvancementWhenEmptied);
    };

    @Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == ForgeCapabilities.FLUID_HANDLER) return tank.getCapability().cast();
		return super.getCapability(cap, side);
	}

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        containedFluidTooltip(tooltip, isPlayerSneaking, getCapability(ForgeCapabilities.FLUID_HANDLER));
        DestroyLang.builder().add(Component.translatable("block.destroy.siphon.drain_amount_remaining", leftToDrain)).style(ChatFormatting.WHITE).forGoggles(tooltip);
        return true;
    };

    public class SiphonFluidTankBehaviour extends GeniusFluidTankBehaviour {

        public SiphonFluidTankBehaviour() {
            super(GeniusFluidTankBehaviour.TYPE, SiphonBlockEntity.this, 1, DestroyAllConfigs.SERVER.blocks.siphonCapacity.get(), false);
            capability = LazyOptional.of(() -> new SiphonFluidHandler(getPrimaryHandler()));
        };

        public class SiphonFluidHandler extends InternalFluidHandler {

            public SiphonFluidHandler(IFluidHandler... handlers) {
                super(handlers, false);
            };

            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                maxDrain = Math.min(maxDrain, leftToDrain);
                FluidStack drained = super.drain(maxDrain, action);
                if (action.execute()) leftToDrain -= drained.getAmount();
                checkAdvancement();
                return drained;
            };

            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                FluidStack toDrain = resource.copy();
                if (toDrain.isEmpty()) return FluidStack.EMPTY;
                toDrain.setAmount(Math.min(resource.getAmount(), leftToDrain));
                FluidStack drained = super.drain(toDrain, action);
                if (action.execute()) leftToDrain -= drained.getAmount();
                checkAdvancement();
                return drained;
            };

            private void checkAdvancement() {
                if (giveAdvancementWhenEmptied && leftToDrain == 0) {
                    advancementBehaviour.awardDestroyAdvancement(DestroyAdvancementTrigger.SIPHON);
                };
            };

        };
    };

    public class SiphonScrollValueBehaviour extends ScrollValueBehaviour {

        private static DecimalFormat df = new DecimalFormat();
        static {
            df.setMinimumFractionDigits(1);
            df.setMaximumFractionDigits(1);
        };

        public SiphonScrollValueBehaviour() {
            super(Component.translatable("block.destroy.siphon.drain_amount"), SiphonBlockEntity.this, new SiphonSlot());
        };

        @Override
        public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
            return new ValueSettingsBoard(label, 100, 10,
			DestroyLang.translatedOptions("generic.unit", "millibuckets", "buckets"),
			new ValueSettingsFormatter(this::formatSettings));
        };

        @Override
        public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlHeld) {
            int value = valueSetting.value();
            int multiplier = valueSetting.row() == 0 ? 1 : 100;
            if (!valueSetting.equals(getValueSettings())) playFeedbackSound(this);
            setValue(Math.max(1, value) * multiplier);
        };

        @Override
        public ValueSettings getValueSettings() {
            int row = 0;
            int value = this.value;

            if (value > 100) {
                value = value / 100;
                row = 1;
            };

            return new ValueSettings(row, value);
        };

        public MutableComponent formatSettings(ValueSettings settings) {
            return Component.literal(switch (settings.row()) {
                case 0 -> String.valueOf(settings.value()) + CreateLang.translateDirect("generic.unit.millibuckets").getString();
                case 1 -> df.format(settings.value() / 10f) + CreateLang.translateDirect("generic.unit.buckets").getString();
                default -> String.valueOf(settings.value());
            });
        };

        public static class SiphonSlot extends ValueBoxTransform.Sided {

            @Override
            protected Vec3 getSouthLocation() {
                return VecHelper.voxelSpace(8d, 8d, 14.5d);  
            };
    
            @Override
            public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
                if (getSide().getAxis() == Direction.Axis.Y) {
                    return VecHelper.voxelSpace(8d, getSide() == Direction.UP ? 15.5d : 0.5d, 8d);
                };
                return super.getLocalOffset(level, pos, state);
            };
            
        };

    };
    
};
