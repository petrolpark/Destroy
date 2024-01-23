package com.petrolpark.destroy.util;

import java.util.List;

import com.petrolpark.destroy.advancement.DestroyAdvancements;
import com.petrolpark.destroy.capability.level.pollution.LevelPollutionProvider;
import com.petrolpark.destroy.capability.level.pollution.LevelPollution.PollutionType;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.network.DestroyMessages;
import com.petrolpark.destroy.network.packet.EvaporatingFluidS2CPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;

public class PollutionHelper {

    /**
     * Whether Pollution is enabled in this world.
     */
    public static boolean pollutionEnabled() {
        return DestroyAllConfigs.SERVER.pollution.enablePollution.get();
    };

    /**
     * Gets the level of pollution of the given Type in the given Level.
     * @param level
     * @param pollutionType
     * @return 0 if the Level does not have the Level Pollution capability
     */
    public static int getPollution(Level level, PollutionType pollutionType) {
        return level.getCapability(LevelPollutionProvider.LEVEL_POLLUTION).map(levelPollution -> levelPollution.get(pollutionType)).orElse(0);
    };

    /**
     * Sets the level of pollution of the given Type in the given Level.
     * The change is broadcast to all clients (Avoid this by using the {@link com.petrolpark.destroy.capability.level.pollution.LevelPollution#set set()} method instead).
     * @param level
     * @param pollutionType
     * @param value Will be set within the {@link com.petrolpark.destroy.capability.level.pollution.LevelPollution.PollutionType bounds}.
     * @return The actual value to which the level of pollution was set (0 if there was no Capability)
     */
    public static int setPollution(Level level, PollutionType pollutionType, int value) {
        return level.getCapability(LevelPollutionProvider.LEVEL_POLLUTION).map(levelPollution -> {
            //int oldValue = levelPollution.get(pollutionType);
            int newValue = levelPollution.set(pollutionType, value); // Actually set the Pollution level

            // This has been disabled as updating the SMOG level only updates the colours of unloaded chunks, which creates weird-looking boundaries if the change is large
            // For now this only happens when reloading the world
            // if (oldValue != newValue) { // If there has been a change (which needs to be broadcast to clients)
            //     DestroyMessages.sendToAllClients(new LevelPollutionS2CPacket(levelPollution));
            // };

            // Award Advancements for fully polluting/repairing the world
            if (level instanceof ServerLevel serverLevel && levelPollution.hasPollutionEverBeenMaxed()) {
                serverLevel.players().forEach(player -> DestroyAdvancements.FULLY_POLLUTE.award(serverLevel, player));
                if (levelPollution.hasPollutionEverBeenFullyReduced()) {
                    serverLevel.players().forEach(player -> DestroyAdvancements.UNPOLLUTE.award(serverLevel, player));
                };
            };
            return newValue;
        }).orElse(0);
    };

    /**
     * Changes the level of pollution of the given Type in the given Level by the given amount.
     * The change is broadcast to all clients (Avoid this by using the {@link com.petrolpark.destroy.capability.level.pollution.LevelPollution#change change()} method instead).
     * @param level
     * @param pollutionType
     * @param change Can be positive or negative; will be set within the {@link com.petrolpark.destroy.capability.level.pollution.LevelPollution.PollutionType bounds}.
     * @return The actual value to which the level of pollution was set (0 if there was no Capability)
     */
    public static int changePollution(Level level, PollutionType pollutionType, int change) {
        return setPollution(level, pollutionType, Mth.clamp(getPollution(level, pollutionType) + change, 0, pollutionType.max));
    };

    /**
     * Pollute a single Fluid Stack, with no Particles, and without damaging nearby Entities.
     * @param level
     * @param fluidStack
     * @see PollutionHelper#pollute(Level, BlockPos, int, FluidStack...) Harming Entities and showing evaporation particles too
     */
    public static void pollute(Level level, FluidStack fluidStack) {
        if (DestroyFluids.isMixture(fluidStack) && fluidStack.getOrCreateTag().contains("Mixture", Tag.TAG_COMPOUND)) {
            ReadOnlyMixture mixture = ReadOnlyMixture.readNBT(fluidStack.getOrCreateTag().getCompound("Mixture"));
            for (Molecule molecule : mixture.getContents(true)) {
                float pollutionAmount = mixture.getConcentrationOf(molecule) * fluidStack.getAmount() / 1000; // One mole of polluting Molecule = one point of Pollution
                for (PollutionType pollutionType : PollutionType.values()) {
                    if (molecule.hasTag(pollutionType.moleculeTag) && level.random.nextFloat() <= pollutionAmount) changePollution(level, pollutionType, (int)pollutionAmount);
                };
            };
        };
    };

    /**
     * Release the given Fluids into the environment, sometimes summon evaporation particles, and expose nearby entities to the effects of the chemicals.
     * @param level The level in which the pollution is taking place
     * @param blockPos The position from which the evaporation Particles should originate
     * @param particleWeight There will be a {@code 1} in {@code particleWeight} chance of a Particle being shown. If this is {@code 1} (as is the default), there will always be a Particle
     * @param fluidStacks The Fluids with which to pollute
     */
    public static void pollute(Level level, BlockPos blockPos, int particleWeight, FluidStack ...fluidStacks) {
        if (level.isClientSide()) return;
        List<LivingEntity> nearbyEntities = level.getEntities(null, new AABB(blockPos).inflate(2)).stream().filter(e -> e instanceof LivingEntity).map(e -> (LivingEntity)e).toList();
        for (FluidStack fluidStack : List.of(fluidStacks)) {
            pollute(level, fluidStack);
            if (particleWeight == 1 || level.getRandom().nextInt(particleWeight) == 0); DestroyMessages.sendToAllClients(new EvaporatingFluidS2CPacket(blockPos, fluidStack));
            for (LivingEntity entity : nearbyEntities) {
                ChemistryDamageHelper.damage(level, entity, fluidStack, true);
            };
        };
    };

    /**
     * Release the given Fluids into the environment and summon evaporation particles.
     * @param level The Level in which the pollution is taking place
     * @param blockPos The position from which the evaporation Particles should originate
     * @param fluidStacks The Fluids with which to pollute
     */
    public static void pollute(Level level, BlockPos blockPos, FluidStack ...fluidStacks) {
        pollute(level, blockPos, 1, fluidStacks);
    };
};
