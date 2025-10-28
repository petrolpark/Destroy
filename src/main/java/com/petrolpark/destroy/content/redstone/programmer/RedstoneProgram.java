package com.petrolpark.destroy.content.redstone.programmer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.simibubi.create.Create;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;

import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public abstract class RedstoneProgram {

    /**
     * See {@link RedstoneProgram.PlayMode PlayMode}.
     */
    public PlayMode mode;
    /**
     * Length of the program in beats.
     */
    protected int length;
    /**
     * How far through the program we are, in beats.
     */
    protected int playtime;
    /**
     * How many ticks until the beat gets incremented.
     */
    protected int ticksToNextBeat;
    /**
     * If we are paused.
     */
    public boolean paused;
    /**
     * Whether we were paused last tick.
     */
    protected boolean pausedLastTick;
    /**
     * Whether we were powered last tick.
     */
    protected boolean poweredLastTick;
    /**
     * Each pair of Frequencies, and the list of strengths associated with it.
     */
    protected List<Channel> channels;

    /**
     * Channel sequences may not change strength more often than this if edited in the GUI.
     */
    protected int ticksPerBeat;
    /**
     * Purely visual, informs where lines should be drawn in the GUI.
     */
    public int beatsPerLine;
    /**
     * Purely visual, informs which lines drawn in the GUI should be thicker.
     */
    public int linesPerBar;

    /**
     * Whether a the Redstone Link network was notified of a change in the last tick and it needn't be notified again.
     */
    protected boolean notifiedChange;

    public RedstoneProgram() {
        mode = PlayMode.MANUAL;
        ticksPerBeat = DestroyAllConfigs.SERVER.blocks.redstoneProgrammerMinTicksPerBeat.get();
        length = 20;
        playtime = 0;
        paused = true;
        pausedLastTick = false;
        poweredLastTick = false;
        channels = new ArrayList<>();
        beatsPerLine = 2;
        linesPerBar = 4;
        notifiedChange = false;
    };

    public int getLength() {
        return length;
    };

    public int getTicksPerBeat() {
        return ticksPerBeat;
    };

    /**
     * The number of ticks this program has been playing for.
     */
    public int getAbsolutePlaytime() {
        return ticksPerBeat * playtime + (ticksPerBeat - ticksToNextBeat);
    };

    public void setTicksPerBeat(int value) {
        ticksPerBeat = Math.max(DestroyAllConfigs.SERVER.blocks.redstoneProgrammerMinTicksPerBeat.get(), value);
    };

    public void tick() {
        boolean powered = hasPower();

        if (mode.powerRequired) paused = !powered; // If we need power to run, make sure we're doing the right thing

        if (powered && !poweredLastTick) { // If we've been pulsed
            if (mode == PlayMode.SWITCH_ON_PULSE) {
                paused = !paused;
            } else if (mode == PlayMode.RESTART_ON_PULSE) {
                paused = false;
                playtime = 0;
            };
        };

        if (!powered && mode == PlayMode.LOOP_WITH_POWER) playtime = 0; // Restart if we should

        if (!paused) { // Play if not paused
            notifiedChange = false;
            if (ticksPerBeat == 1) {
                playtime++;
            } else {
                ticksToNextBeat--;
                if (ticksToNextBeat <= 0) {
                    ticksToNextBeat = ticksPerBeat;
                    playtime++;
                } else {
                    notifiedChange = true; // If nothing can have changed, there is no need to update again
                };
            };
        };

        if (paused != pausedLastTick) notifiedChange = false; // If we've unpaused, we need to start telling the Redstone Link network about changes again

        if (playtime >= length) { // Restart if we've reached the end
            playtime = 0;
            if (mode.pausesWhenFinished) paused = true; // If we shouldn't loop, don't
        };

        if (!notifiedChange) { // If we need to notify the Redstone Link network of our power change
            channels.forEach(Channel::updateNetwork);
            if (paused) notifiedChange = true; // If we're paused, don't notify next tick too
        };

        poweredLastTick = powered;
        pausedLastTick = paused;
    };

    public void restart() {
        playtime = 0;
        ticksToNextBeat = ticksPerBeat;
    };

    public abstract boolean hasPower();

    public abstract BlockPos getBlockPos();

    public abstract boolean shouldTransmit();

    public abstract LevelAccessor getWorld();

    public void whenChanged() {};

    public ImmutableList<Channel> getChannels() {
        return ImmutableList.copyOf(channels);
    };

    public void addBlankChannel(Couple<Frequency> frequencies) {
        Channel channel = new Channel(frequencies, new int[length]);
        channels.add(channel);
        if (!isValidWorld(getWorld())) return;
        getHandler().addToNetwork(getWorld(), channel);
    };

    public boolean remove(Channel channel) {
        boolean removed = channels.remove(channel);
        if (removed && isValidWorld(getWorld())) getHandler().removeFromNetwork(getWorld(), channel);
        return removed;
    };

    public void swap(Channel channel1, Channel channel2) {
        if (channels.contains(channel1) && channels.contains(channel2)) Collections.swap(channels, channels.indexOf(channel1), channels.indexOf(channel2));
    };

    public void load() {
        if (!isValidWorld(getWorld()) || getBlockPos() == null) return;
        channels.forEach(channel -> getHandler().addToNetwork(getWorld(), channel));
        notifiedChange = false;
    };

    public void unload() {
        if (!isValidWorld(getWorld())) return;
        channels.forEach(channel -> getHandler().removeFromNetwork(getWorld(), channel));
    };

    public void setDuration(int duration) {
        length = duration;
        for (Channel channel : channels) {
            channel.sequence = Arrays.copyOf(channel.sequence, duration);
        };
    };

    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Mode", mode.ordinal());
        tag.putInt("TicksPerBeat", ticksPerBeat);
        tag.putInt("Length", length);
        tag.putInt("Playtime", playtime);
        if (ticksPerBeat != 1) tag.putInt("TicksToNextBeat", ticksToNextBeat);
        tag.putBoolean("Paused", paused);
        tag.putBoolean("PoweredLastTick", poweredLastTick);
        tag.putInt("BeatsPerLine", beatsPerLine);
        tag.putInt("LinesPerBar", linesPerBar);

        ListTag sequencesTag = new ListTag();

        for (Channel channel : channels) {
            CompoundTag sequenceTag = new CompoundTag();
            sequenceTag.put("FrequencyFirst", channel.networkKey.getFirst().getStack().save(new CompoundTag()));
		    sequenceTag.put("FrequencyLast", channel.networkKey.getSecond().getStack().save(new CompoundTag()));
            sequenceTag.putIntArray("Sequence", getEncodedSequence(channel));
            sequencesTag.add(sequenceTag);
        };

        tag.put("Sequences", sequencesTag);

        return tag;
    };

    public static <T extends RedstoneProgram> T read(Supplier<T> newProgram, CompoundTag tag) {
        T program = newProgram.get();
        program.mode = PlayMode.values()[tag.getInt("Mode")];
        program.ticksPerBeat = tag.getInt("TicksPerBeat");
        program.length = tag.getInt("Length");
        program.playtime = tag.getInt("Playtime");
        if (program.ticksPerBeat != 1) program.ticksToNextBeat = tag.getInt("TicksToNextBeat");
        program.paused = tag.getBoolean("Paused");
        program.poweredLastTick = tag.getBoolean("PoweredLastTick");
        program.beatsPerLine = tag.getInt("BeatsPerLine");
        program.linesPerBar = tag.getInt("LinesPerBar");

        tag.getList("Sequences", Tag.TAG_COMPOUND).forEach(t -> {
            CompoundTag sequenceTag = (CompoundTag)t;
            int[] sequence = decodeSequence(program.length, sequenceTag.getIntArray("Sequence"));

            program.channels.add(
                program.new Channel(
                    Couple.create(
                        Frequency.of(ItemStack.of(sequenceTag.getCompound("FrequencyFirst"))),
                        Frequency.of(ItemStack.of(sequenceTag.getCompound("FrequencyLast")))
                    ),
                    sequence
                )
            );
        });

        return program;
    };

    public final void write(FriendlyByteBuf buf) {
        buf.writeVarInt(mode.ordinal());
        buf.writeVarInt(ticksPerBeat);
        buf.writeVarInt(length);
        buf.writeVarInt(playtime);
        buf.writeVarInt(ticksToNextBeat);
        buf.writeBoolean(paused);
        buf.writeBoolean(poweredLastTick);
        buf.writeVarInt(beatsPerLine);
        buf.writeVarInt(linesPerBar);
        buf.writeVarInt(channels.size());
        for (Channel channel : channels) {
            buf.writeItem(channel.networkKey.getFirst().getStack());
            buf.writeItem(channel.networkKey.getSecond().getStack());
            buf.writeVarIntArray(getEncodedSequence(channel));
        };
    };

    public void read(FriendlyByteBuf buf) {
        mode = PlayMode.values()[buf.readVarInt()];
        ticksPerBeat = buf.readVarInt();
        length = buf.readVarInt();
        playtime = buf.readVarInt();
        ticksToNextBeat = buf.readVarInt();
        paused = buf.readBoolean();
        poweredLastTick = buf.readBoolean();
        beatsPerLine = buf.readVarInt();
        linesPerBar = buf.readVarInt();
        int channels = buf.readVarInt();
        for (int i = 0; i < channels; i++) {
            this.channels.add(new Channel(
                Couple.create(Frequency.of(buf.readItem()), Frequency.of(buf.readItem())),
                decodeSequence(length, buf.readVarIntArray())
            ));
        };
    };

    public void copyFrom(RedstoneProgram otherProgram) {
        mode = otherProgram.mode;
        ticksPerBeat = otherProgram.ticksPerBeat;
        length = otherProgram.length;
        playtime = otherProgram.playtime;
        ticksToNextBeat = otherProgram.ticksToNextBeat;
        paused = otherProgram.paused;
        poweredLastTick = otherProgram.poweredLastTick;
        channels = new ArrayList<>(otherProgram.channels.stream().map(channel -> new Channel(channel.networkKey, Arrays.copyOf(channel.sequence, length))).toList());
        beatsPerLine = otherProgram.beatsPerLine;
        linesPerBar = otherProgram.linesPerBar;
        notifiedChange = false;
    };

    public boolean hasPowerChanged() {
        return hasPower() != poweredLastTick;
    };

    // As redstone powers only go up to 16, we can fit eight of them in one integer. We only fit seven to avoid messing with the sign bit.
    private int[] getEncodedSequence(Channel channel) {
        int[] encodedStrengths = new int[(length / 7) + 1];
        int i = 0;
        while (i < length) {
            int encodedStrength = 0;
            for (int j = 6; j >= 0; j--) {
                int strength = i < length ? channel.sequence[i] : 0;
                encodedStrength += strength << 4 * j;
                i++;
            };
            encodedStrengths[(i - 1) / 7] = encodedStrength;
        };
        return encodedStrengths;
    };

    protected static int[] decodeSequence(int length, int[] encodedSequence) {
        int[] sequence = new int[length];
        int i = 0;
        for (int encodedStrength : encodedSequence) {
            decodeStrengths: for (int j = 6; j >= 0; j--) {
                if (i >= length) break decodeStrengths;
                int strength = encodedStrength >> 4 * j;
                encodedStrength -= strength << 4 * j;
                sequence[i] = strength;
                i++;
            };
        };
        return sequence;
    };

    protected static RedstoneLinkNetworkHandler getHandler() {
        return Create.REDSTONE_LINK_NETWORK_HANDLER;
    };

    protected static boolean isValidWorld(LevelAccessor level) {
        return level != null && !level.isClientSide();
    };

    public class Channel implements IRedstoneLinkable {

        public final Couple<Frequency> networkKey;
        protected int[] sequence;

        protected Channel(Couple<Frequency> networkKey, int[] sequence) {
            this.networkKey = networkKey;
            this.sequence = sequence;
        };

        protected void updateNetwork() {
            if (!isValidWorld(getWorld())) return;
            if (playtime == 0 || sequence[playtime] != sequence[playtime - 1]) getHandler().updateNetworkOf(getWorld(), this); // If we've changed signal, update the Network
        };

        public int getStrength(int position) {
            if (position >= sequence.length || position < 0) return 0;
            return sequence[position];
        };

        public void setStrength(int position, int strength) {
            if (position < length) {
                if (strength >= 16 || strength < 0) strength = 0;
                sequence[position] = strength;  
            };
        };

        public void clear() {
            sequence = new int[length];
        };

        @Override
        public int getTransmittedStrength() {
            if (playtime >= length) return 0;
            return sequence[playtime];
        };

        @Override
        public void setReceivedStrength(int power) {
            // Do nothing
        };

        @Override
        public boolean isListening() {
            return false;
        };

        @Override
        public boolean isAlive() {
            return shouldTransmit();
        };

        @Override
        public Couple<Frequency> getNetworkKey() {
            return networkKey;
        };

        @Override
        public BlockPos getLocation() {
            return getBlockPos();
        };

    };
    
    public static enum PlayMode {
        /**
         * Manually play, pause, restart, and skip.
         */
        MANUAL(true, false, DestroyLang.translate("tooltip.redstone_programmer.mode.manual").component()),
        /**
         * If there is a redstone pulse, switch between playing and pausing. Restart if the end is reached.
         */
        SWITCH_ON_PULSE(false, false, DestroyLang.translate("tooltip.redstone_programmer.mode.switch_on_pulse").component()),
        /**
         * If there is a redstone pulse, start the program again, even if already running. Don't loop.
         */
        RESTART_ON_PULSE(true, false, DestroyLang.translate("tooltip.redstone_programmer.mode.restart_on_pulse").component()),
        /**
         * If there is power, play. If not, pause. If the end is reached, start again.
         */
        RESUME_WITH_POWER(false, true, DestroyLang.translate("tooltip.redstone_programmer.mode.resume_with_power").component()),
        /**
         * If there is power, play. If not, go back to the start and pause. Do not loop.
         */
        RESTART_WITH_POWER(true, true, DestroyLang.translate("tooltip.redstone_programmer.mode.restart_with_power").component()),
        /**
         * If there is power, play. If not, pause and restart. If the end is reached, start again.
         */
        LOOP_WITH_POWER(false, true, DestroyLang.translate("tooltip.redstone_programmer.mode.loop_with_power").component()),
        /**
         * Play on repeat infinitely.
         */
        LOOP(false, false, DestroyLang.translate("tooltip.redstone_programmer.mode.loop").component());

        PlayMode(boolean pausesWhenFinished, boolean powerRequired, Component description) {
            this.pausesWhenFinished = pausesWhenFinished;
            this.powerRequired = powerRequired;
            this.description = description;
        };

        /**
         * Whether we should stop once we reach the end and go back to the beginning, rather than play again.
         */
        public final boolean pausesWhenFinished;
        /**
         * Whether we should pause if we don't have power and play if we do.
         */
        public final boolean powerRequired;

        public final Component description;
    };
};
