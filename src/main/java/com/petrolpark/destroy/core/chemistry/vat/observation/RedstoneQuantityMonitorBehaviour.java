package com.petrolpark.destroy.core.chemistry.vat.observation;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class RedstoneQuantityMonitorBehaviour extends BlockEntityBehaviour {

    public static final BehaviourType<RedstoneQuantityMonitorBehaviour> TYPE = new BehaviourType<>();

    @Nonnull
    public Optional<Supplier<Float>> quantityObserved;
    protected Function<Float, Component> label = f -> Component.empty();

    public float lowerThreshold;
    public float upperThreshold;
    protected int oldStrength;
    protected boolean isFirstTick;

    protected IntConsumer strengthChangeCallback = i -> {};

    public RedstoneQuantityMonitorBehaviour(SmartBlockEntity be) {
        super(be);
        quantityObserved = Optional.empty();
    };

    @Override
    public void initialize() {
        isFirstTick = true;
    }

    public RedstoneQuantityMonitorBehaviour withLabel(Function<Float, Component> label) {
        this.label = label;
        return this;
    };

    public RedstoneQuantityMonitorBehaviour onStrengthChanged(IntConsumer callback) {
        strengthChangeCallback = callback;
        return this;
    };

    public int getStrength() {
        return oldStrength;
    };

    public void update() {
        getWorld().updateNeighborsAt(getPos(), blockEntity.getBlockState().getBlock());
        //TODO update adjacent positions?
    };

    public Component getLabelledQuantity() {
        return quantityObserved.map(Supplier::get).map(label::apply).orElse(Component.empty());
    };

    @Override
    public void tick() {
        if(isFirstTick) {
            isFirstTick = false;
            return;
        }

        int strength = 0;
        if (quantityObserved.isPresent()) strength = (int)(Mth.clamp((quantityObserved.get().get() - lowerThreshold) / (upperThreshold - lowerThreshold), 0f, 1f) * 15f);
        if (strength != oldStrength) {
            oldStrength = strength;
            update();
            strengthChangeCallback.accept(strength);
        };
    };

    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        super.read(nbt, clientPacket);
        oldStrength = nbt.getInt("OldRedstoneStrength");
        lowerThreshold = nbt.getFloat("LowerObservedQuantityThreshold");
        upperThreshold = nbt.getFloat("UpperObservedQuantityThreshold");
    };

    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        super.write(nbt, clientPacket);
        nbt.putInt("OldRedstoneStrength", oldStrength);
        nbt.putFloat("LowerObservedQuantityThreshold", lowerThreshold);
        nbt.putFloat("UpperObservedQuantityThreshold", upperThreshold);
    };

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    };

    public void notifyUpdate() {
        blockEntity.notifyUpdate();
    };
    
};
