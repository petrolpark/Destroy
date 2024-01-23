package com.petrolpark.destroy.block.entity.behaviour;

import java.util.function.BooleanSupplier;

import com.petrolpark.destroy.util.RedstoneProgram;
import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Couple;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class RedstoneProgrammerBehaviour extends BlockEntityBehaviour implements ClipboardCloneable {

    public static BehaviourType<RedstoneProgrammerBehaviour> TYPE = new BehaviourType<>();

    protected BooleanSupplier powerChecker;
    public BehaviourRedstoneProgram program;

    public RedstoneProgrammerBehaviour(SmartBlockEntity be, BooleanSupplier powerChecker) {
        super(be);
        this.powerChecker = powerChecker;
        this.program = new BehaviourRedstoneProgram();
        program.addBlankChannel(Couple.create(
            Frequency.of(new ItemStack(Items.DIAMOND)),
            Frequency.of(new ItemStack(Items.DIAMOND))
        ));
    };

    @Override
	public void initialize() {
		super.initialize();
		if (getWorld().isClientSide()) return;
		program.load();
	};

    @Override
	public void unload() {
		super.unload();
		if (getWorld().isClientSide()) return;
		program.unload();
	};

    @Override
    public void tick() {
        program.tick();
        super.tick();
    };

    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        super.read(nbt, clientPacket);
        setProgram(nbt.getCompound("Program"));
    };

    public void setProgram(CompoundTag tag) {
        program = RedstoneProgram.read(BehaviourRedstoneProgram::new, tag);
    };

    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        super.write(nbt, clientPacket);
        nbt.put("Program", program.write());
    };

    public class BehaviourRedstoneProgram extends RedstoneProgram {

        @Override
        public boolean hasPower() {
            return powerChecker.getAsBoolean();
        };

        @Override
        public BlockPos getBlockPos() {
            return getPos();
        };

        /**
         * Copied from the {@link com.simibubi.create.content.redstone.link.LinkBehaviour Create source code}.
         */
        @Override
        public boolean shouldTransmit() {
            Level level = RedstoneProgrammerBehaviour.super.getWorld();
            BlockPos pos = getPos();
            if (blockEntity.isChunkUnloaded()) return false;
            if (blockEntity.isRemoved()) return false;
            if (!level.isLoaded(pos)) return false;
            return level.getBlockEntity(pos) == blockEntity;
        };

        @Override
        public LevelAccessor getWorld() {
            return RedstoneProgrammerBehaviour.super.getWorld();
        };
        
    };

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    @Override
    public String getClipboardKey() {
        return "Frequencies";
    };

    @Override
    public boolean writeToClipboard(CompoundTag tag, Direction side) {
        return false;
    };

    @Override
    public boolean readFromClipboard(CompoundTag tag, Player player, Direction side, boolean simulate) {
        if (!tag.contains("First") || !tag.contains("Last")) return false;
        Couple<Frequency> frequencies = Couple.create(Frequency.of(ItemStack.of(tag.getCompound("First"))), Frequency.of(ItemStack.of(tag.getCompound("Last"))));
        if (program.getChannels().stream().anyMatch(channel -> channel.networkKey.equals(frequencies))) return false;
        if (!simulate) program.addBlankChannel(frequencies);
        return true;
    };
    
};
