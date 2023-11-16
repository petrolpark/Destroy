package com.petrolpark.destroy.capability.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.petrolpark.destroy.chemistry.Molecule;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class EntityChemicalPoison {
  
    private Molecule molecule;

    public void setMolecule(Molecule molecule) {
        if (molecule == null) this.molecule = molecule;
    };

    @Nullable
    public Molecule getMolecule() {
        return molecule;
    };

    public void removeMolecule() {
        this.molecule = null;
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
            if (createEntityChemicalPoison().molecule != null) tag.putString("ToxicMolecule", createEntityChemicalPoison().molecule.getFullID());
            return tag;
        };

        @Override
        public void deserializeNBT(CompoundTag tag) {
            if (tag.contains("ToxicMolecule", Tag.TAG_STRING)) createEntityChemicalPoison().molecule = Molecule.getMolecule(tag.getString("ToxicMolecule"));
        };

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == ENTITY_CHEMICAL_POISON) return optional.cast();
            return LazyOptional.empty();
        };

    };
};
