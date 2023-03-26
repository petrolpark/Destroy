package com.petrolpark.destroy.chemistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.NBTHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;

/**
 * A {@link com.petrolpark.destroy.chemistry.Mixture Mixture} which cannot react.
 * Instantiating or adding a Molecule to a Read-Only Mixture skips out on the processing-intensive generation of Reactions,
 * making them useful for GUI displays.
 */
public class ReadOnlyMixture {

    public static final float IMPURITY_THRESHOLD = 0.1f; // The minimum value below which a Molecule is considered an impurity and not a significant part of the Mixture

    private Component name;
    private String translationKey;
    
    protected float temperature;
    protected Map<Molecule, Float> contents;

    public ReadOnlyMixture() {
        translationKey = "";
    
        contents = new HashMap<>();
        
        temperature = 600f;
    };

    /**
     * Converts this Mixture into a storeable String that can be read by {@link #readNBT readNBT()}.
     */
    public CompoundTag writeNBT() {
        CompoundTag compound = new CompoundTag();
        if (translationKey != "") {
            compound.putString("TranslationKey", translationKey);
        };
        compound.putFloat("Temperature", temperature);
        compound.put("Contents", NBTHelper.writeCompoundList(contents.keySet(), (molecule) -> {
            CompoundTag moleculeTag = new CompoundTag();
            moleculeTag.putString("Molecule", molecule.getFullID());
            moleculeTag.putFloat("Concentration", contents.get(molecule));
            return moleculeTag;
        }));
        return compound;
    };

    /**
     * Generates a Read-Only Mixture from the given Compound Tag.
     * @param compound
     */
    public static ReadOnlyMixture readNBT(CompoundTag compound) {
        ReadOnlyMixture mixture = new ReadOnlyMixture();
        mixture.translationKey = compound.getString("TranslationKey"); // Set to "" if the key is not present
        mixture.temperature = compound.getFloat("Temperature");
        ListTag contents = compound.getList("Contents", 10);
        contents.forEach(tag -> {
            CompoundTag moleculeTag = (CompoundTag) tag;
            mixture.addMolecule(Molecule.getMolecule(moleculeTag.getString("Molecule")), moleculeTag.getFloat("Concentration"));
        });
        mixture.updateName();
        return mixture;
    };

    public Component getName() {
        if (name == null) updateName();
        return name;
    };

    public void setTranslationKey(String translationKey) {
        this.translationKey = translationKey;
    };

    /**
     * Whether this Mixture has no Molecules in it.
     */
    public boolean isEmpty() {
        return contents.isEmpty();
    };

    public float getConcentrationOf(Molecule molecule) {
        if (contents.containsKey(molecule)) {
            return contents.get(molecule);
        } else {
            return 0f;
        }
    };

    public ReadOnlyMixture addMolecule(Molecule molecule, float concentration) {

        if (molecule == null) {
            return this;
        };
        if (molecule.isHypothetical()) {
            Destroy.LOGGER.warn("Could not add hypothetical Molecule '"+molecule.getFullID()+"'' to a real Mixture.");
            return this;
        };

        contents.put(molecule, concentration);

        updateName();
        return this;
    };

    public List<Component> getContentsTooltip(boolean iupac) {
        int i = 0;
        List<Component> tooltip = new ArrayList<>();
        for (Entry<Molecule, Float> entry : contents.entrySet()) {
            tooltip.add(i, DestroyLang.builder()
                .add(entry.getKey().getName(iupac).plainCopy())
                .add(Component.literal(" ("+entry.getValue()+"M)"))
                .style(ChatFormatting.GRAY)
                .component()
            );
            i++;
        };
        return tooltip;
    };

    protected void updateName() {

        if (translationKey != "") {
            name = Component.translatable(translationKey);
            return;
        };

        boolean iupac = DestroyAllConfigs.CLIENT.chemistry.iupacNames.get();

        List<INameableProduct> products = new ArrayList<>();
        List<Molecule> cations = new ArrayList<>();
        List<Molecule> anions = new ArrayList<>();
        List<Molecule> solvents = new ArrayList<>();
        List<Molecule> impurities = new ArrayList<>();
        for (Entry<Molecule, Float> entry : contents.entrySet()) {
            Molecule molecule = entry.getKey();
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
        };

        // Check for salts
        if (cations.size() != 0 || anions.size() != 0) {
            if (cations.size() == 1 && anions.size() == 1) { // Single simple salt (one cation, one anion)
                products.add(b -> DestroyLang.translate("mixture.simple_salt", cations.get(0).getName(b).getString(), anions.get(0).getName(b).getString()).component());
            } else { // Multiple salts
                products.add(b -> DestroyLang.translate("mixture.salts").component());
            };
        };

        //Destroy.LOGGER.info("There are "+products.size()+" products, "+cations.size()+" cations, "+anions.size()+" anions, "+solvents.size()+" solvents, and "+impurities.size()+" impurities.");
        
        boolean thereAreSolvents = solvents.size() != 0;
        boolean thereAreImpurities = impurities.size() != 0;

        if (products.size() == 0) {
            if (solvents.size() == 1) { // One solvent, no products
                name = (thereAreImpurities ? DestroyLang.translate("mixture.dirty").space() : DestroyLang.builder())
                    .add(solvents.get(0).getName(iupac).plainCopy())
                    .component();
                return;
            } else if (solvents.size() == 2) { // Two solvents, no products
                name = (thereAreImpurities ? DestroyLang.translate("mixture.dirty").space() : DestroyLang.builder())
                    .add(DestroyLang.translate("mixture.and", 
                        solvents.get(0).getName(iupac).getString(), solvents.get(1).getName(iupac).getString()
                    )).component();
                return;
            } else if (thereAreSolvents) { // Many solvents, no products
                name = (thereAreImpurities ? DestroyLang.translate("mixture.dirty").space() : DestroyLang.builder())
                    .add(DestroyLang.translate("mixture.solvents"))
                    .component();
                return;
            };
        } else if (products.size() == 1) { // One product
            name = (thereAreImpurities ? DestroyLang.translate("mixture.impure").space() : DestroyLang.builder())
                .add(products.get(0).getName(iupac).plainCopy())
                .add(thereAreSolvents ? DestroyLang.builder().space().translate("mixture.solution") : Lang.text(""))
                .component();
            return;
        } else if (products.size() == 2) { // Two products
            name = (thereAreImpurities ? DestroyLang.translate("mixture.impure").space() : DestroyLang.builder())
                .add(DestroyLang.translate("mixture.and", 
                    products.get(0).getName(iupac).getString(), products.get(1).getName(iupac).getString()
                )).add(thereAreSolvents ? DestroyLang.builder().space().translate("mixture.solution") : Lang.text(""))
                .component();
            return;
        } else {}; // Many products

        name = DestroyLang.translate("mixture.mixture").component();
    };
};
