package com.petrolpark.destroy.world.damage;

import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.config.DestroyAllConfigs;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;

public class ChemicalDamageSource extends DamageSource {

    public final Molecule molecule;

    public ChemicalDamageSource(Holder<DamageType> type, Molecule molecule) {
        super(type);
        this.molecule = molecule;
    };

    @Override
    public Component getLocalizedDeathMessage(LivingEntity livingEntity) {
        if (molecule == null) return Component.translatable("death.attack." + type().msgId() + ".unknown_molecule", livingEntity.getDisplayName());
        return Component.translatable("death.attack." + type().msgId(), livingEntity.getDisplayName(), molecule.getName(DestroyAllConfigs.CLIENT.chemistry.iupacNames.get()));
    };
    
};
