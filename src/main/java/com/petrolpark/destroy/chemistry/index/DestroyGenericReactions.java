package com.petrolpark.destroy.chemistry.index;

import com.petrolpark.destroy.chemistry.index.genericreaction.AcylChlorideEsterification;
import com.petrolpark.destroy.chemistry.index.genericreaction.AcylChlorideFormation;
import com.petrolpark.destroy.chemistry.index.genericreaction.AlkeneHydration;
import com.petrolpark.destroy.chemistry.index.genericreaction.AlkeneHydrogenation;
import com.petrolpark.destroy.chemistry.index.genericreaction.CarboxylicAcidEsterification;
import com.petrolpark.destroy.chemistry.index.genericreaction.CyanideNucleophilicAddition;
import com.petrolpark.destroy.chemistry.index.genericreaction.HalideAmmoniaSubstitution;
import com.petrolpark.destroy.chemistry.index.genericreaction.HalideCyanideSubstitution;
import com.petrolpark.destroy.chemistry.index.genericreaction.HalideHydroxideSubstitution;

public class DestroyGenericReactions {

    public static final AcylChlorideEsterification ACYL_CHLORIDE_ESTERIFICATION = new AcylChlorideEsterification();
    public static final AcylChlorideFormation ACYL_CHLORIDE_FORMATION = new AcylChlorideFormation();
    public static final AlkeneHydration ALKENE_HYDRATION = new AlkeneHydration();
    public static final AlkeneHydrogenation ALKENE_HYDROGENATION = new AlkeneHydrogenation();
    public static final CarboxylicAcidEsterification CARBOXYLIC_ACID_ESTERIFICATION = new CarboxylicAcidEsterification();
    public static final CyanideNucleophilicAddition CYANIDE_NUCLEOPHILIC_ADDITION = new CyanideNucleophilicAddition();
    public static final HalideAmmoniaSubstitution HALIDE_AMMONIA_SUBSTITUTION = new HalideAmmoniaSubstitution();
    public static final HalideCyanideSubstitution HALIDE_CYANIDE_SUBSTITUTION = new HalideCyanideSubstitution();
    public static final HalideHydroxideSubstitution HALIDE_HYDROXIDE_SUBSTITUTION = new HalideHydroxideSubstitution();

    public static void register() {};
}
