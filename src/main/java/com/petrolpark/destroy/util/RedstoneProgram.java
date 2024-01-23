package com.petrolpark.destroy.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.Create;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import com.simibubi.create.foundation.utility.Couple;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public abstract class RedstoneProgram {

    /**
     * See {@link RedstoneProgram.PlayMode PlayMode}.
     */
    public PlayMode mode;
    /**
     * Length of the program in ticks.
     */
    protected int length;
    /**
     * How far through the program we are, in ticks.
     */
    protected int playtime;
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
     * Whether a the Redstone Link network was notified of a change in the last tick and it needn't be notified again.
     */
    protected boolean notifiedChange;

    public RedstoneProgram() {
        mode = PlayMode.MANUAL;
        length = 20;
        playtime = 0;
        paused = true;
        pausedLastTick = false;
        poweredLastTick = false;
        channels = new ArrayList<>();
        notifiedChange = false;
    };

    public void tick() {
        boolean powered = hasPower();

        if (paused != pausedLastTick) notifiedChange = false; // If we've unpaused, we need to start telling the Redstone Link network about changes again

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

        if (!paused) playtime++; // Play if not paused

        if (playtime >= length) { // Restart if we've reached the end
            playtime = 0;
            if (mode.pausesWhenFinished) paused = true; // If we shouldn't loop, don't
        };

        if (!notifiedChange) { // If we need to notify the Redstone Link network of our power change
            channels.forEach(Channel::updateNetwork);
            if (paused) notifiedChange = false; // If we're paused, don't notify next tick too
        };

        poweredLastTick = powered;
        pausedLastTick = paused;
    };

    public abstract boolean hasPower();

    public abstract BlockPos getBlockPos();

    public abstract boolean shouldTransmit();

    public abstract LevelAccessor getWorld();

    public ImmutableList<Channel> getChannels() {
        return ImmutableList.copyOf(channels);
    };

    public void addBlankChannel(Couple<Frequency> frequencies) {
        if (!isValidWorld(getWorld())) return;
        Channel channel = new Channel(frequencies, new int[length]);
        getHandler().addToNetwork(getWorld(), channel);

        // Temporary
        for (int i = 0; i < length; i++) channel.sequence[i] = 15 * ((i/2) % 2);
        paused = false;
        mode = PlayMode.LOOP;

        channels.add(channel);
    };

    public boolean remove(Channel channel) {
        if (!isValidWorld(getWorld())) return false;
        boolean removed = channels.remove(channel);
        if (removed) getHandler().removeFromNetwork(getWorld(), channel);
        return removed;
    };

    public void swap(Channel channel1, Channel channel2) {
        if (channels.contains(channel1) && channels.contains(channel2)) Collections.swap(channels, channels.indexOf(channel1), channels.indexOf(channel2));
    };

    public void load() {
        if (!isValidWorld(getWorld())) return;
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
        tag.putInt("Length", length);
        tag.putInt("Playtime", playtime);
        tag.putBoolean("Paused", paused);
        tag.putBoolean("PoweredLastTick", poweredLastTick);

        ListTag sequencesTag = new ListTag();
        // As redstone powers only go up to 16, we can fit eight of them in one integer. We only fit seven to avoid messing with the sign bit.
        for (Channel channel : channels) {
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
            
            CompoundTag sequenceTag = new CompoundTag();
            sequenceTag.put("FrequencyFirst", channel.networkKey.getFirst().getStack().save(new CompoundTag()));
		    sequenceTag.put("FrequencyLast", channel.networkKey.getSecond().getStack().save(new CompoundTag()));
            sequenceTag.putIntArray("Sequence", encodedStrengths);
            sequencesTag.add(sequenceTag);
        };

        tag.put("Sequences", sequencesTag);

        return tag;
    };

    public static <T extends RedstoneProgram> T read(Supplier<T> newProgram, CompoundTag tag) {
        T program = newProgram.get();
        program.mode = PlayMode.values()[tag.getInt("Mode")];
        program.length = tag.getInt("Length");
        program.playtime = tag.getInt("Playtime");
        program.paused = tag.getBoolean("Paused");
        program.poweredLastTick = tag.getBoolean("PoweredLastTick");

        tag.getList("Sequences", Tag.TAG_COMPOUND).forEach(t -> {
            CompoundTag sequenceTag = (CompoundTag)t;
            int[] sequence = new int[program.length];
            int[] encodedStrengths = sequenceTag.getIntArray("Sequence");
            int i = 0;
            for (int encodedStrength : encodedStrengths) {
                decodeStrengths: for (int j = 6; j >= 0; j--) {
                    if (i >= program.length) break decodeStrengths;
                    int strength = encodedStrength >> 4 * j;
                    encodedStrength -= strength << 4 * j;
                    sequence[i] = strength;
                    i++;
                };
            };

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
            if (playtime != 0 && sequence[playtime] != sequence[playtime - 1]) getHandler().updateNetworkOf(getWorld(), this); // If we've changed signal, update the Network
        };

        public void setStrength(int position, int strength) {
            if (position < length) {
                if (strength <= 16 || strength < 0) strength = 0;
                sequence[position] = strength;  
            };
        };

        @Override
        public int getTransmittedStrength() {
            if (paused || playtime >= length) return 0;
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
