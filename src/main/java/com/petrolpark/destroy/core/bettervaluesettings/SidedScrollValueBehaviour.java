package com.petrolpark.destroy.core.bettervaluesettings;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.petrolpark.destroy.MoveToPetrolparkLibrary;
import com.petrolpark.destroy.client.DestroyLang;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;

@MoveToPetrolparkLibrary
public class SidedScrollValueBehaviour extends BlockEntityBehaviour implements BetterValueSettingsBehaviour {

    public static final BehaviourType<SidedScrollValueBehaviour> TYPE = new BehaviourType<>();

	protected ValueBoxTransform slotPositioning;
	protected Vec3 textShift;

	protected int min = 0;
	protected int max = 1;
	public int[] values = {0, 0, 0, 0, 0, 0};
    protected Direction lastSideAccessed;
	public Component label;
	protected BiConsumer<Direction, Integer> callback;
	public boolean oppositeSides;
	protected Function<Integer, String> formatter;
	private Supplier<Boolean> isActive;
	protected boolean needsWrench;

	public SidedScrollValueBehaviour(Component label, SmartBlockEntity be, ValueBoxTransform slot) {
		super(be);
		this.setLabel(label);
		slotPositioning = slot;
		callback = (d, i) -> {};
		oppositeSides = true;
		formatter = i -> Integer.toString(i);
		isActive = () -> true;
	};

    @Override
	public boolean isSafeNBT() {
		return true;
	};

	@Override
	public void write(CompoundTag nbt, boolean clientPacket) {
		nbt.putIntArray("Values", values);
		super.write(nbt, clientPacket);
	};

	@Override
	public void read(CompoundTag nbt, boolean clientPacket) {
		values = nbt.getIntArray("Values");
		super.read(nbt, clientPacket);
	};

	public SidedScrollValueBehaviour withCallback(BiConsumer<Direction, Integer> valueCallback) {
		callback = valueCallback;
		return this;
	};

	/**
	 * Label the scroll box on one face of the Block
	 * with the <em>opposite</em> side of the Block (e.g.
	 * the north side is labelled "South").
	 */
	public SidedScrollValueBehaviour oppositeSides() {
		oppositeSides = true;
		return this;
	};

	public SidedScrollValueBehaviour between(int min, int max) {
		this.min = min;
		this.max = max;
		return this;
	};

	public SidedScrollValueBehaviour requiresWrench() {
		this.needsWrench = true;
		return this;
	};

	public SidedScrollValueBehaviour withFormatter(Function<Integer, String> formatter) {
		this.formatter = formatter;
		return this;
	}

	public SidedScrollValueBehaviour onlyActiveWhen(Supplier<Boolean> condition) {
		isActive = condition;
		return this;
	}

    public void setValue(Direction direction, int newValue) {
        newValue= Mth.clamp(newValue, min, max);
        if (newValue == values[direction.ordinal()]) return;
        values[direction.ordinal()] = newValue;
        callback.accept(direction, newValue);
		blockEntity.setChanged();
		blockEntity.sendData();
    };

    public int getValue(Direction side) {
		return values[side.ordinal()];
	};

	public String formatValue(Direction side) {
		return formatter.apply(values[side.ordinal()]);
	};

    @Override
    public boolean testHit(Vec3 hit) {
        BlockState state = blockEntity.getBlockState();
		Vec3 localHit = hit.subtract(Vec3.atLowerCornerOf(blockEntity.getBlockPos()));
		return slotPositioning.testHit(blockEntity.getLevel(), blockEntity.getBlockPos(), state, localHit);
    };

    @Override
    public boolean isActive() {
        return isActive.get();
    };

    public void setLabel(Component label) {
		this.label = label;
	};

    @Override
    public ValueBoxTransform getSlotPositioning() {
        return slotPositioning;
    };

	@Override
	public boolean readFromClipboard(CompoundTag tag, Player player, Direction side, boolean simulate) {
		if (!acceptsValueSettings()) return false;
		if (!tag.contains("Values")) return false;
		if (simulate) return true;
		int[] newValues = tag.getIntArray("Values");
		for (Direction direction : Direction.values()) {
			setValue(direction, newValues[direction.ordinal()]);
		};
		playFeedbackSound(this);
		return true;
	};

	@Override
	public boolean writeToClipboard(CompoundTag tag, Direction side) {
		if (!acceptsValueSettings())
			return false;
		tag.putIntArray("Values", values);
		return true;
	};

    @Override
    public void onShortInteract(Player player, InteractionHand hand, Direction side, BlockHitResult hitResult) {
        lastSideAccessed = side;
        if (player instanceof FakePlayer)
			blockEntity.getBlockState().use(getWorld(), player, hand, hitResult);
    };

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        lastSideAccessed = hitResult.getDirection();
        return new ValueSettingsBoard(label, max, 10,
			ImmutableList.of(DestroyLang.direction(oppositeSides ? lastSideAccessed.getOpposite() : lastSideAccessed).component()),
			new ValueSettingsFormatter(ValueSettings::format)
		);
    };

	@Override
	public void acceptAccessInformation(InteractionHand interactionHand, Direction face) {
		lastSideAccessed = face;
	};

    @Override
    public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlDown) {
        setValue(lastSideAccessed, valueSetting.value());
        playFeedbackSound(this);
    };

    @Override
    public ValueSettings getValueSettings() {
        return new ValueSettings(0, values[lastSideAccessed.ordinal()]);
    };

    @Override
	public boolean onlyVisibleWithWrench() {
		return needsWrench;
	};

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    };

    
};
