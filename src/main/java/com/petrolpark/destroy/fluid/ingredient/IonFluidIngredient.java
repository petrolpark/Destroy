package com.petrolpark.destroy.fluid.ingredient;

import java.util.List;

import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.fluid.ingredient.mixturesubtype.MixtureFluidIngredientSubType;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipHelper.Palette;

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
    protected boolean testMixture(Mixture mixture) {
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

    public static class Type extends MoleculeFluidIngredient.Type {

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
    
            Molecule molecule = Molecule.getMolecule(moleculeID);
            Component moleculeName = molecule == null ? DestroyLang.translate("tooltip.unknown_molecule").component() : molecule.getName(DestroyAllConfigs.CLIENT.chemistry.iupacNames.get());
            boolean anion = molecule != null && molecule.getCharge() < 0;
    
            return TooltipHelper.cutStringTextComponent(DestroyLang.translate("tooltip.mixture_ingredient." + (anion ? "anion" : "cation"), moleculeName, df.format(minConc), df.format(maxConc)).string(), Palette.GRAY_AND_WHITE);
        };

    };
    
};
