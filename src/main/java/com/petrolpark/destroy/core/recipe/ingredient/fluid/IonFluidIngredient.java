package com.petrolpark.destroy.core.recipe.ingredient.fluid;

import java.util.List;

import com.petrolpark.destroy.chemistry.legacy.LegacyMixture;
import com.petrolpark.destroy.chemistry.legacy.LegacySpecies;
import com.petrolpark.destroy.chemistry.legacy.ReadOnlyMixture;
import com.petrolpark.destroy.chemistry.legacy.index.DestroyMolecules;
import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.simibubi.create.foundation.item.TooltipHelper;
import net.createmod.catnip.lang.FontHelper.Palette;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class IonFluidIngredient extends MoleculeFluidIngredient {

    public static final Type TYPE = new Type();

    @Override
    public MixtureFluidIngredientSubType<MoleculeFluidIngredient> getType() {
        return TYPE;
    };

    @Override
    protected boolean testMixture(LegacyMixture mixture) {
        return mixture.hasUsableMolecule(molecule, minConcentration, maxConcentration, m -> m.getCharge() != 0 && Math.signum(m.getCharge()) != Math.signum(molecule.getCharge()));
    };

    @Override
    public List<ReadOnlyMixture> getExampleMixtures() {
        ReadOnlyMixture mixture = new ReadOnlyMixture();
        float targetConcentration = getTargetConcentration();
        mixture.addMolecule(DestroyMolecules.WATER, DestroyMolecules.WATER.getPureConcentration()); // We're assuming ions have 0 density
        mixture.addMolecule(molecule, targetConcentration);
        mixture.addMolecule(molecule.getCharge() > 0 ? DestroyMolecules.CHLORIDE : DestroyMolecules.SODIUM_ION, targetConcentration * Mth.abs(molecule.getCharge()));
        return List.of(mixture);
    };

    protected static class Type extends MoleculeFluidIngredient.Type {

        @Override
        public IonFluidIngredient getNew() {
            return new IonFluidIngredient();
        };

        @Override
        public String getMixtureFluidIngredientSubtype() {
            return "mixtureFluidWithIon";
        };
    
        @Override
        public List<Component> getDescription(CompoundTag fluidTag) {
            String moleculeID = fluidTag.getString("MoleculeRequired");
            float minConc = fluidTag.getFloat("MinimumConcentration");
            float maxConc = fluidTag.getFloat("MaximumConcentration");
    
            LegacySpecies molecule = LegacySpecies.getMolecule(moleculeID);
            Component moleculeName = molecule == null ? DestroyLang.translate("tooltip.unknown_molecule").component() : molecule.getName(DestroyAllConfigs.CLIENT.chemistry.iupacNames.get());
            boolean anion = molecule != null && molecule.getCharge() < 0;
    
            return TooltipHelper.cutStringTextComponent(DestroyLang.translate("tooltip.mixture_ingredient." + (anion ? "anion" : "cation"), moleculeName, df.format(minConc), df.format(maxConc)).string(), Palette.GRAY_AND_WHITE);
        };

    };
    
};
