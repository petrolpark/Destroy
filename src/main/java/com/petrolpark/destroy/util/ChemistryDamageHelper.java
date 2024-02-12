package com.petrolpark.destroy.util;

import com.petrolpark.destroy.capability.entity.EntityChemicalPoison;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.effect.DestroyMobEffects;
import com.petrolpark.destroy.fluid.DestroyFluids;
import com.petrolpark.destroy.util.DestroyTags.DestroyItemTags;
import com.petrolpark.destroy.world.damage.DestroyDamageSources;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

public class ChemistryDamageHelper {
  
    /**
     * Apply the effects of exposure to a Mixture to an Entity
     * @param level
     * @param entity
     * @param mixture
     * @param skinContact Whether the entity has contact with the Mixture on a body part other than their mouth (if they are "submerged" in it), regardless of any protective clothing
     */
    public static void damage(Level level, LivingEntity entity, FluidStack stack, boolean skinContact) {
        if (!DestroyFluids.isMixture(stack)) return;
        ReadOnlyMixture mixture = ReadOnlyMixture.readNBT(ReadOnlyMixture::new, stack.getOrCreateChildTag("Mixture"));
        if (mixture.isEmpty()) return;

        boolean burning = mixture.getConcentrationOf(DestroyMolecules.PROTON) > 0.01f || mixture.getConcentrationOf(DestroyMolecules.HYDROXIDE) > 0.01f;
        boolean nauseating = false;
        boolean carcinogen = false;
        Molecule toxicMolecule = null;

        for (Molecule molecule : mixture.getContents(true)) {
            if (molecule.hasTag(DestroyMolecules.Tags.ACUTELY_TOXIC)) toxicMolecule = molecule;
            if (molecule.hasTag(DestroyMolecules.Tags.SMELLY)) nauseating = true;
            if (molecule.hasTag(DestroyMolecules.Tags.CARCINOGEN)) carcinogen = true;
            if (toxicMolecule != null && nauseating && carcinogen) break;
        };

        boolean gasMask = DestroyItemTags.CHEMICAL_PROTECTION_HEAD.matches(entity.getItemBySlot(EquipmentSlot.HEAD).getItem());
        boolean hazmat = gasMask && DestroyItemTags.CHEMICAL_PROTECTION_TORSO.matches(entity.getItemBySlot(EquipmentSlot.CHEST).getItem()) && DestroyItemTags.CHEMICAL_PROTECTION_LEGS.matches(entity.getItemBySlot(EquipmentSlot.LEGS).getItem()) && DestroyItemTags.CHEMICAL_PROTECTION_FEET.matches(entity.getItemBySlot(EquipmentSlot.FEET).getItem());
        boolean perfume = entity.hasEffect(DestroyMobEffects.FRAGRANCE.get());

        // Smelly chemicals
        if (nauseating && !perfume && !gasMask && !(entity instanceof Player player && player.isCreative())) {
            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0, false, false));
        };

        // Acutely toxic Molecules
        if (toxicMolecule != null && !gasMask && !level.isClientSide()) {
            EntityChemicalPoison.setMolecule(entity, toxicMolecule);
            if (!entity.hasEffect(DestroyMobEffects.CHEMICAL_POISON.get())) entity.addEffect(new MobEffectInstance(DestroyMobEffects.CHEMICAL_POISON.get(), 219, 0, false, false));
        };
        
        // Carcinogens
        if (carcinogen && !gasMask) {
            if (entity.getRandom().nextInt(2400) == 0) entity.addEffect(DestroyMobEffects.cancerInstance());
        };

        // Acid/base burns
        if (skinContact) {
            if (hazmat && (burning || nauseating || carcinogen || toxicMolecule != null)) { // If there is a hazard
                for (ItemStack armor : entity.getArmorSlots()) contaminate(armor, stack);
            } else {
                if (burning) {
                    entity.hurt(DestroyDamageSources.chemicalBurn(level), 5f);
                };
            };
        };

    };

    public static void contaminate(ItemStack stack, FluidStack fluidStack) {
        Item item = stack.getItem();
        if (LivingEntity.getEquipmentSlotForItem(stack) != null && (
            DestroyItemTags.CHEMICAL_PROTECTION_FEET.matches(item) ||
            DestroyItemTags.CHEMICAL_PROTECTION_HEAD.matches(item) ||
            DestroyItemTags.CHEMICAL_PROTECTION_LEGS.matches(item) ||
            DestroyItemTags.CHEMICAL_PROTECTION_TORSO.matches(item)
        )) {
            CompoundTag tag = stack.getOrCreateTag();
            if (tag.contains("ContaminatingFluid")) return;
            CompoundTag fluidTag = new CompoundTag();
            fluidStack.writeToNBT(fluidTag);
            tag.put("ContaminatingFluid", fluidTag);
        };
    };

    public static void decontaminate(ItemStack stack) {
        stack.getOrCreateTag().remove("ContaminatingFluid");
    };
};
