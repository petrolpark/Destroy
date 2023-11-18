package com.petrolpark.destroy.capability.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.network.DestroyMessages;
import com.petrolpark.destroy.network.packet.ChemicalPoisonS2CPacket;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class EntityChemicalPoison {
  
    private Molecule molecule;

    public static void setMolecule(Entity entity, Molecule molecule) {
        if (!(entity instanceof LivingEntity)) return;
        entity.getCapability(Provider.ENTITY_CHEMICAL_POISON).ifPresent(cp -> {
            if (cp.molecule != null) return; // Don't replace existing poison
            cp.molecule = molecule;
            if (entity instanceof ServerPlayer serverPlayer) DestroyMessages.sendToClient(new ChemicalPoisonS2CPacket(molecule), serverPlayer);
        });
    };

    public static void removeMolecule(Entity entity) {
        if (!(entity instanceof LivingEntity)) return;
        entity.getCapability(Provider.ENTITY_CHEMICAL_POISON).ifPresent(cp -> {
            cp.molecule = null;
            if (entity instanceof ServerPlayer serverPlayer) DestroyMessages.sendToClient(new ChemicalPoisonS2CPacket((Molecule)null), serverPlayer);
        });
    };

    @Nullable
    public Molecule getMolecule() {
        return molecule;
    };

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

        public static Capability<EntityChemicalPoison> ENTITY_CHEMICAL_POISON = CapabilityManager.get(new CapabilityToken<EntityChemicalPoison>() {});

        private EntityChemicalPoison chemicalPoison = null;
        private final LazyOptional<EntityChemicalPoison> optional = LazyOptional.of(this::createEntityChemicalPoison);

        private EntityChemicalPoison createEntityChemicalPoison() {
            if (chemicalPoison == null) chemicalPoison = new EntityChemicalPoison();
            return chemicalPoison;
        };

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            if (createEntityChemicalPoison().molecule != null)
                tag.putString("ToxicMolecule", createEntityChemicalPoison().molecule.getFullID());
            return tag;
        };

        @Override
        public void deserializeNBT(CompoundTag tag) {
            if (tag.contains("ToxicMolecule", Tag.TAG_STRING))
                createEntityChemicalPoison().molecule = Molecule.getMolecule(tag.getString("ToxicMolecule"));
        };

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == ENTITY_CHEMICAL_POISON) return optional.cast();
            return LazyOptional.empty();
        };

    };
};
