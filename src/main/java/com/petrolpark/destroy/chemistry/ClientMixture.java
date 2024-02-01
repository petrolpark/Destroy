package com.petrolpark.destroy.chemistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.jozufozu.flywheel.util.Color;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.chemistry.naming.INameableProduct;
import com.petrolpark.destroy.chemistry.naming.NamedSalt;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.util.DestroyLang;

import net.minecraft.network.chat.Component;

public class ClientMixture extends ReadOnlyMixture {

    public void updateColor() {
        float totalColorContribution = 0f;
        float totalRed = 0;
        float totalGreen = 0;
        float totalBlue = 0;
        int totalAlpha = 64;
        for (Entry<Molecule, Float> entry : contents.entrySet()) {
            //if (entry.getKey().isColorless()) continue;
            Color color = new Color(entry.getKey().getColor());
            float colorContribution = entry.getValue() * color.getAlphaAsFloat();
            totalColorContribution += colorContribution;
            totalRed += color.getRed() * colorContribution;
            totalGreen += color.getGreen() * colorContribution;
            totalBlue += color.getBlue() * colorContribution;
            totalAlpha = Math.max(totalAlpha, color.getAlpha());
        };
        color = new Color((int)(totalRed / totalColorContribution), (int)(totalGreen / totalColorContribution), (int)(totalBlue / totalColorContribution), totalAlpha).getRGB();
    };

    protected void updateName() {

        try {

            if (translationKey != "") {
                name = Component.translatable(translationKey);
                return;
            };
            
            boolean iupac = DestroyAllConfigs.CLIENT.chemistry.iupacNames.get();

            if (contents.size() == 1) {
                name = contents.entrySet().iterator().next().getKey().getName(iupac);
                return;
            };

            boolean neutral = Mixture.areVeryClose(getConcentrationOf(DestroyMolecules.PROTON), getConcentrationOf(DestroyMolecules.HYDROXIDE));

            List<INameableProduct> products = new ArrayList<>();
            List<Molecule> cations = new ArrayList<>();
            List<Molecule> anions = new ArrayList<>();
            List<Molecule> solvents = new ArrayList<>();
            List<Molecule> impurities = new ArrayList<>();
            boolean thereAreNeutralMolecules = false;
            for (Entry<Molecule, Float> entry : contents.entrySet()) {
                Molecule molecule = entry.getKey();
                if (neutral && (molecule == DestroyMolecules.HYDROXIDE || molecule == DestroyMolecules.PROTON)) continue;
                if (entry.getValue() < IMPURITY_THRESHOLD) {
                    impurities.add(molecule);
                } else if (molecule.hasTag(DestroyMolecules.Tags.SOLVENT)) {
                    solvents.add(molecule);
                } else {
                    if (molecule.getCharge() > 0) {
                        cations.add(molecule);
                    } else if (molecule.getCharge() < 0) {
                        anions.add(molecule);
                    } else {
                        products.add(molecule);
                    };
                };
                if (molecule.getCharge() == 0) thereAreNeutralMolecules = true;
            };

            // Check for salts
            if (cations.size() != 0 || anions.size() != 0) {
                if (cations.size() == 1 && anions.size() == 1) { // Single simple salt (one cation, one anion)
                    if (cations.get(0) != DestroyMolecules.PROTON || anions.get(0) != DestroyMolecules.HYDROXIDE) products.add(new NamedSalt(cations.get(0), anions.get(0)));
                } else { // Multiple salts
                    products.add(b -> DestroyLang.translate("mixture.salts").component());
                };
            };
            
            boolean thereAreSolvents = solvents.size() != 0;
            boolean thereAreImpurities = impurities.size() != 0;

            // If two products have the same name, such as an acid having its undissociated and dissociated form salt name override the same, ignore one
            if (products.size() == 2 && products.get(0).getName(iupac).getString().equals(products.get(1).getName(iupac).getString())) products.remove(1);

            if (products.size() == 0) {
                if (solvents.size() == 1) { // One solvent, no products
                    name = solvents.get(0).getName(iupac).plainCopy();
                } else if (solvents.size() == 2) { // Two solvents, no products
                    name = DestroyLang.translate("mixture.and", solvents.get(0).getName(iupac).getString(), solvents.get(1).getName(iupac).getString()).component();
                } else if (thereAreSolvents) { // Many solvents, no products
                    name = DestroyLang.translate("mixture.solvents").component();
                };
                if (thereAreImpurities && name != null) name = DestroyLang.translate("mixture.dirty", name.getString()).component();

            } else if (products.size() <= 2) { // If there are not enough products to just call it "Mixture"
                if (products.size() == 1) {
                    name = products.get(0).getName(iupac).plainCopy();
                } else {
                    name = DestroyLang.translate("mixture.and", products.get(0).getName(iupac).getString(), products.get(1).getName(iupac).getString()).component();
                };
                if (thereAreSolvents) name = DestroyLang.translate("mixture.solution", name.getString()).component(); // If there are solvents, this is a solution
                if (thereAreImpurities) name = DestroyLang.translate("mixture.impure", name.getString()).component(); // If there impurities, this is 'impure', regardless of whether its a solution or not
            } else { // Many products

            }; 

            if (name == null) name = DestroyLang.translate("mixture.mixture").component();

            if (!thereAreNeutralMolecules && name != null) name = DestroyLang.translate("mixture.supersaturated", name.getString()).component();

        } catch (Throwable e) {
            name = DestroyLang.translate("mixture.mixture").component();
        };
    };
};
