package com.petrolpark.destroy.chemistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jozufozu.flywheel.util.Color;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.chemistry.index.DestroyMolecules;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.NBTHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;

/**
 * A {@link com.petrolpark.destroy.chemistry.Mixture Mixture} which cannot react.
 * Instantiating - or adding a {@link Molecule} to - a Read-Only Mixture skips out on the processing-intensive generation of {@link Reaction Reactions},
 * making them useful for GUI displays.
 */
public class ReadOnlyMixture {

    /**
     * The minimum value below which a {@link Molecule} is considered an impurity and not a significant part of the Mixture.
     */
    public static final float IMPURITY_THRESHOLD = 0.1f;

    /**
     * The display name of this Mixture. This may be a custom name (if this Mixture comes from a Recipe), or {@link ReadOnlyMixture#getName generated}
     * if this Mixture has been {@link Mixture#react reacted}.
     * <p>It is recommemded that Mixtures obtained from Recipes are given custom names using the tag {@code TranslationKey} to avoid having to calculate a name.</p>
     */
    private Component name;

    /**
     * The full translation key of this Mixture if it has a custom name, for example {@code destroy.mixture.brine}.
     */
    protected String translationKey;
    
    /**
     * How hot (in kelvins) this Mixture is. Temperature affects the rate of {@link Reaction Reactions}.
     */
    protected float temperature;

    /**
     * The {@link Molecule Molecules} contained by this Mixture, mapped to their concentrations (in moles per Bucket).
     */
    protected Map<Molecule, Float> contents;

    public ReadOnlyMixture() {
        translationKey = "";
    
        contents = new HashMap<>();
        
        temperature = 298f;
    };

    /**
     * Converts this Mixture into a storeable String that can be {@link ReadOnlyMixture#readNBT parsed back} into a Mixture.
     */
    public CompoundTag writeNBT() {
        CompoundTag compound = new CompoundTag();
        if (translationKey != "") {
            compound.putString("TranslationKey", translationKey);
        };
        compound.putFloat("Temperature", temperature);
        compound.put("Contents", NBTHelper.writeCompoundList(contents.entrySet(), (entry) -> {
            CompoundTag moleculeTag = new CompoundTag();
            moleculeTag.putString("Molecule", entry.getKey().getFullID());
            moleculeTag.putFloat("Concentration", entry.getValue());
            return moleculeTag;
        }));
        return compound;
    };

    /**
     * Generates a Read-Only Mixture from the given Compound Tag.
     * @param compound
     * @return A new Read-Only Mixture instance
     */
    public static ReadOnlyMixture readNBT(CompoundTag compound) {
        ReadOnlyMixture mixture = new ReadOnlyMixture();
        mixture.translationKey = compound.getString("TranslationKey"); // Set to "" if the key is not present
        if (compound.contains("Temperature")) mixture.temperature = compound.getFloat("Temperature");
        ListTag contents = compound.getList("Contents", 10);
        contents.forEach(tag -> {
            CompoundTag moleculeTag = (CompoundTag) tag;
            mixture.addMolecule(Molecule.getMolecule(moleculeTag.getString("Molecule")), moleculeTag.getFloat("Concentration"));
        });
        mixture.updateName();
        return mixture;
    };

    /**
     * The display name of this Mixture. This may be a custom name (if this Mixture comes from a Recipe), or {@link ReadOnlyMixture#getName generated}
     * if this Mixture has been {@link Mixture#react reacted}.
     * <p>It is recommemded that Mixtures obtained from Recipes are given custom names using the tag {@code TranslationKey} to avoid having to calculate a name.</p>
     */
    public Component getName() {
        if (name == null) updateName();
        return name;
    };

    /**
     * Sets the display name of this Mixture to avoid having to calculate what the name should be based on the {@link ReadOnlyMixture#contents contents}. 
     * @param translationKey The full translation key of this Mixture, for example {@code destroy.mixture.brine}.
     */
    public void setTranslationKey(String translationKey) {
        this.translationKey = translationKey;
    };

    /**
     * How hot (in kelvins) this Mixture is. Temperature affects the rate of {@link Reaction Reactions}.
     */
    public float getTemperature() {
        return temperature;
    };

    /**
     * Whether this Mixture has no {@link Molecule Molecules} in it.
     */
    public boolean isEmpty() {
        return contents.isEmpty();
    };

    /**
     * The concentration of the given {@link Molecule} in this Mixture.
     * @param molecule
     * @return 0 if the Mixture does not contain the given Molecule
     */
    public float getConcentrationOf(Molecule molecule) {
        if (contents.containsKey(molecule)) {
            return contents.get(molecule);
        } else {
            return 0f;
        }
    };

    /**
     * Adds the given {@link Molecule} to this Read-Only Mixture with the given concentration.
     * If the Read-Only Mixture already contained the Molecule its concentration will be replaced,
     * thought this should not happen - to modify the contents of existing Mixtures, use a {@link Mixture} and not a Read-Only Mixture.
     * @param molecule Will not be added if hypothetical (for example an {@link Group#getExampleMolecule example Molecule} of a {@link Group functional Group})
     * @param concentration
     * @return This Mixture
     */
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

    /**
     * Get all the {@link Molecule Molecules} present in this Mixture.
     * @param excludeNovel Whether to exclude novel Molecules
     */
    public List<Molecule> getContents(boolean excludeNovel) {
        return contents.keySet().stream().filter(molecule -> !molecule.isNovel() || !excludeNovel).toList();
    };

    /**
     * Get the list of {@link Molecule Molecules} in this Mixture as a String.
     * (Used for debugging).
     */
    public String getContentsString() {
        String string = "";
        for (Entry<Molecule, Float> entry : contents.entrySet()) {
            string += entry.getKey().getFullID() + " (" + entry.getValue() + "M), ";
        };
        return string.substring(0, string.length() - 2);
    };

    /**
     * The tooltip listing the {@link ReadOnlyMixture#contents contents} of this Mixture.
     * @param iupac Whether to use IUPAC names instead of common names
     */
    public List<Component> getContentsTooltip(boolean iupac) {
        int i = 0;
        List<Component> tooltip = new ArrayList<>();
        for (Entry<Molecule, Float> entry : contents.entrySet()) {
            tooltip.add(i, DestroyLang.builder()
                .space().space()
                .add(entry.getKey().getName(iupac).plainCopy())
                .add(Component.literal(
                    entry.getKey().getCharge() == 0 ? "" : " [" + entry.getKey().getSerializedCharge(false) + "]" + // Show charge, if there is one
                    " ("+entry.getValue()+"M)" // Show concentration
                ))
                .style(ChatFormatting.GRAY)
                .component()
            );
            i++;
        };
        return tooltip;
    };

    public int getColor() {
        float totalColorContribution = 0f;
        float totalRed = 0;
        float totalGreen = 0;
        float totalBlue = 0;
        int totalAlpha = 64;
        for (Entry<Molecule, Float> entry : contents.entrySet()) {
            if (entry.getKey().isColorless()) continue;
            Color color = new Color(entry.getKey().getColor());
            float colorContribution = entry.getValue() * color.getAlphaAsFloat();
            totalColorContribution += colorContribution;
            totalRed += color.getRed() * colorContribution;
            totalGreen += color.getGreen() * colorContribution;
            totalBlue += color.getBlue() * colorContribution;
            totalAlpha = Math.max(totalAlpha, color.getAlpha());
        };
        return new Color((int)(totalRed / totalColorContribution), (int)(totalGreen / totalColorContribution), (int)(totalBlue / totalColorContribution), totalAlpha).getRGB();
    };

    /**
     * Update the {@link ReadOnlyMixture#name name} of this Mixture to reflect what's in it.
     */
    protected void updateName() {

        if (translationKey != "") {
            name = Component.translatable(translationKey);
            return;
        };

        boolean iupac = false; //TODO change to use config values
        //boolean iupac = DestroyAllConfigs.CLIENT.chemistry.iupacNames.get();

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
