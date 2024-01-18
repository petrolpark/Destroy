package com.petrolpark.destroy.fluid.ingredient;

import java.util.List;

import com.petrolpark.destroy.chemistry.Mixture;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipHelper.Palette;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class IonFluidIngredient extends MoleculeFluidIngredient {

    @Override
    public MixtureFluidIngredient getNew() {
        return new IonFluidIngredient();
    };

    @Override
    protected boolean testMixture(Mixture mixture) {
        return mixture.hasUsableMolecule(molecule, minConcentration, maxConcentration, m -> m.getCharge() != 0 && Math.signum(m.getCharge()) != Math.signum(molecule.getCharge()));
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
