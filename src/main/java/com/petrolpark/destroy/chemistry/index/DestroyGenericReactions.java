package com.petrolpark.destroy.chemistry.index;

import com.petrolpark.destroy.chemistry.index.genericreaction.AcylChlorideFormation;
import com.petrolpark.destroy.chemistry.index.genericreaction.AlkeneHydration;
import com.petrolpark.destroy.chemistry.index.genericreaction.CarboxylicAcidEsterification;
import com.petrolpark.destroy.chemistry.index.genericreaction.CyanideNucleophilicAddition;
import com.petrolpark.destroy.chemistry.index.genericreaction.HydroxideSubstitutions;

public class DestroyGenericReactions {

    public static final AcylChlorideFormation ACYL_CHLORIDE_FORMATION = new AcylChlorideFormation();
    public static final AlkeneHydration ALKENE_HYDRATION = new AlkeneHydration();
    public static final CarboxylicAcidEsterification CARBOXYLIC_ACID_ESTERIFICATION = new CarboxylicAcidEsterification();
    public static final CyanideNucleophilicAddition CYANIDE_NUCLEOPHILIC_ADDITION = new CyanideNucleophilicAddition();
    public static final HydroxideSubstitutions HYDROXIDE_SUBSTITUTION = new HydroxideSubstitutions();

    public static void register() {};
}
