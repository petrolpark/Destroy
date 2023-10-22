package com.petrolpark.destroy.chemistry.index;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.petrolpark.destroy.chemistry.Atom;
import com.petrolpark.destroy.chemistry.Bond;
import com.petrolpark.destroy.chemistry.Element;
import com.petrolpark.destroy.chemistry.Group;
import com.petrolpark.destroy.chemistry.GroupFinder;
import com.petrolpark.destroy.chemistry.Bond.BondType;
import com.petrolpark.destroy.chemistry.index.group.AcidAnhydrideGroup;
import com.petrolpark.destroy.chemistry.index.group.AcylChlorideGroup;
import com.petrolpark.destroy.chemistry.index.group.AlcoholGroup;
import com.petrolpark.destroy.chemistry.index.group.AlkeneGroup;
import com.petrolpark.destroy.chemistry.index.group.UnsubstitutedAmideGroup;
import com.petrolpark.destroy.chemistry.index.group.CarbonylGroup;
import com.petrolpark.destroy.chemistry.index.group.CarboxylicAcidGroup;
import com.petrolpark.destroy.chemistry.index.group.HalideGroup;
import com.petrolpark.destroy.chemistry.index.group.EsterGroup;
import com.petrolpark.destroy.chemistry.index.group.NitrileGroup;
import com.petrolpark.destroy.chemistry.index.group.NonTertiaryAmineGroup;

public class DestroyGroupFinder extends GroupFinder {

    @Override
    public List<Group<?>> findGroups(Map<Atom, List<Bond>> structure) {

        List<Group<?>> groups = new ArrayList<>();

        List<Atom> carbonsToIgnore = new ArrayList<>();
        List<Atom> carbonsToIgnoreForAlkenes = new ArrayList<>();

        for (Atom carbon : structure.keySet()) {

            if (carbon.getElement() != Element.CARBON || carbonsToIgnore.contains(carbon)) {
                continue;
            };

            List<Atom> carbonylOxygens = bondedAtomsOfElementTo(structure, carbon, Element.OXYGEN, BondType.DOUBLE);
            List<Atom> singleBondOxygens = bondedAtomsOfElementTo(structure, carbon, Element.OXYGEN, BondType.SINGLE);
            List<Atom> chlorines = bondedAtomsOfElementTo(structure, carbon, Element.CHLORINE, BondType.SINGLE);
            List<Atom> halogens = new ArrayList<>(chlorines);
            halogens.addAll(bondedAtomsOfElementTo(structure, carbon, Element.IODINE, BondType.SINGLE));
            List<Atom> nitrogens = bondedAtomsOfElementTo(structure, carbon, Element.NITROGEN, BondType.SINGLE);
            List<Atom> hydrogens = bondedAtomsOfElementTo(structure, carbon, Element.HYDROGEN, BondType.SINGLE);
            List<Atom> carbons = bondedAtomsOfElementTo(structure, carbon, Element.CARBON, BondType.SINGLE);
            List<Atom> alkeneCarbons = bondedAtomsOfElementTo(structure, carbon, Element.CARBON, BondType.DOUBLE);
            List<Atom> nitrileNitrogens = bondedAtomsOfElementTo(structure, carbon, Element.NITROGEN, BondType.TRIPLE);
            List<Atom> rGroups = bondedAtomsOfElementTo(structure, carbon, Element.R_GROUP);

            if (carbonylOxygens.size() == 1) { // Ketones, aldehydes, esters, acids, acid anhydrides, acyl chlorides, amides
                Atom carbonylOxygen = carbonylOxygens.get(0);
                if (singleBondOxygens.size() == 1) { // Esters, carboxylic acids and acid anhydrides
                    Atom alcoholOxygen = singleBondOxygens.get(0);
                    if (bondedAtomsOfElementTo(structure, alcoholOxygen, Element.CARBON, BondType.SINGLE).size() == 2) { // Esters and acid anhydrides
                        Atom otherCarbon = getCarbonBondedToOxygenWhichIsntThisCarbonInThisStructure(alcoholOxygen, carbon, structure);
                        if (bondedAtomsOfElementTo(structure, otherCarbon, Element.OXYGEN, BondType.DOUBLE).size() == 1) { // Acid anhydride
                            groups.add(new AcidAnhydrideGroup(carbon, carbonylOxygen, otherCarbon, bondedAtomsOfElementTo(structure, otherCarbon, Element.OXYGEN, BondType.DOUBLE).get(0), alcoholOxygen));
                        } else { // Ester
                            groups.add(new EsterGroup(carbon, otherCarbon, carbonylOxygen, alcoholOxygen));
                        };
                        carbonsToIgnore.add(otherCarbon);
                        continue;
                    } else if (bondedAtomsOfElementTo(structure, alcoholOxygen, Element.HYDROGEN, BondType.SINGLE).size() == 1) { //Carboxylic Acid
                        groups.add(new CarboxylicAcidGroup(carbon, carbonylOxygen, alcoholOxygen, bondedAtomsOfElementTo(structure, alcoholOxygen, Element.HYDROGEN, BondType.SINGLE).get(0)));
                        continue;
                    };
                } else { // Ketones, aldehydes, acyl chlorides, amides
                     if (nitrogens.size() == 1) { // Amide
                        List<Atom> amideHydrogens = bondedAtomsOfElementTo(structure, nitrogens.get(0), Element.HYDROGEN);
                        if (amideHydrogens.size() == 2) {
                            groups.add(new UnsubstitutedAmideGroup(carbon, carbonylOxygen, nitrogens.get(0), amideHydrogens.get(0), amideHydrogens.get(1)));
                            continue;
                        };
                     } else if (chlorines.size() == 1) {
                        groups.add(new AcylChlorideGroup(carbon, carbonylOxygen, chlorines.get(0)));
                        continue;
                     } else {
                        if (carbons.size() == 2) {
                            groups.add(new CarbonylGroup(carbon, carbonylOxygen, true));
                        } else if (carbons.size() + hydrogens.size() + rGroups.size() == 2) {
                            groups.add(new CarbonylGroup(carbon, carbonylOxygen, false));
                        };
                     }
                };
            } else { // Alcohols, halides, nitriles, amines
                for (Atom halogen : halogens) {
                    groups.add(new HalideGroup(carbon, halogen, carbons.size()));
                };
                for (Atom oxygen : singleBondOxygens) { // Alcohols
                    if (bondedAtomsOfElementTo(structure, oxygen, Element.HYDROGEN).size() == 1) {
                        groups.add(new AlcoholGroup(carbon, oxygen, bondedAtomsOfElementTo(structure, oxygen, Element.HYDROGEN).get(0), carbons.size()));
                    };
                };
                for (Atom nitrogen : nitrogens) { // Primary and secondary amines
                    for (Atom hydrogen : bondedAtomsOfElementTo(structure, nitrogen, Element.HYDROGEN)) {
                        groups.add(new NonTertiaryAmineGroup(carbon, nitrogen, hydrogen));
                    };
                };

                // Nitriles
                if (nitrileNitrogens.size() == 1 && carbons.size() == 1) {
                    groups.add(new NitrileGroup(carbon, nitrileNitrogens.get(0)));
                };
            };

            addAllAlkenes: for (Atom alkeneCarbon : alkeneCarbons) {
                if (carbonsToIgnoreForAlkenes.contains(alkeneCarbon)) continue addAllAlkenes;
                int firstCarbonDegree = bondedAtomsOfElementTo(structure, carbon, Element.CARBON).size() - 1;
                int secondCarbonDegree = bondedAtomsOfElementTo(structure, alkeneCarbon, Element.CARBON).size() - 1;
                // If the two Carbons have the same degree, then there are two alkene Groups
                if (firstCarbonDegree >= secondCarbonDegree) {
                    groups.add(new AlkeneGroup(carbon, alkeneCarbon));
                };
                if (secondCarbonDegree >= firstCarbonDegree) {
                    groups.add(new AlkeneGroup(alkeneCarbon, carbon));
                };
                carbonsToIgnoreForAlkenes.add(carbon);
            };

        };

        return groups;
    };

    /**
     * Who needs JavaDocs when you explain everything perfectly in the method identifier?
     * @param oxygen The oxygen bonded to both the carbons
     * @param carbon The carbon we don't want
     * @param structure The structure in which all of these silly little Atoms are
     * @return The carbon we do want
     */
    private Atom getCarbonBondedToOxygenWhichIsntThisCarbonInThisStructure(Atom oxygen, Atom carbon, Map<Atom, List<Bond>> structure) { //clear method names are my passion
        List<Atom> carbonsBondedToOxygen = bondedAtomsOfElementTo(structure, oxygen, Element.CARBON); //get both the carbons
        carbonsBondedToOxygen.remove(carbon); //remove the carbonyl one
        return carbonsBondedToOxygen.get(0);
    };

    public static void register() {
        new DestroyGroupFinder();
    };
    
};
