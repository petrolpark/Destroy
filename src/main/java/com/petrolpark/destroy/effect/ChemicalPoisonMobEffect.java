package com.petrolpark.destroy.effect;

import com.petrolpark.destroy.capability.entity.EntityChemicalPoison;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.world.damage.DestroyDamageSources;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraftforge.common.util.LazyOptional;

public class ChemicalPoisonMobEffect extends MobEffect {

    protected ChemicalPoisonMobEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFFFFF);
    };

    @Override
    public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(livingEntity, attributeMap, amplifier);
        getCap(livingEntity).ifPresent(EntityChemicalPoison::removeMolecule);
    };

    @Override
    @SuppressWarnings("null") // We know the effect isn't null if its ticking
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide()) {
            int duration = livingEntity.getEffect(DestroyMobEffects.CHEMICAL_POISON.get()).getDuration(); // This is the bit it says is null
            if (duration % 50 == 0) {
                Molecule molecule = null;
                LazyOptional<EntityChemicalPoison> cap = getCap(livingEntity);
                if (cap.isPresent()) molecule = cap.resolve().get().getMolecule();
                livingEntity.hurt(DestroyDamageSources.chemicalPoison(livingEntity.level(), molecule), 1f);
            };
        };
        super.applyEffectTick(livingEntity, amplifier);
    };

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    };

    private static LazyOptional<EntityChemicalPoison> getCap(LivingEntity livingEntity) {
        return livingEntity.getCapability(EntityChemicalPoison.Provider.ENTITY_CHEMICAL_POISON);
    };
    
};
