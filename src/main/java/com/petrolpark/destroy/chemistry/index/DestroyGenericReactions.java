package com.petrolpark.destroy.chemistry.index;

import com.petrolpark.destroy.chemistry.index.genericreaction.AcylChlorideEsterification;
import com.petrolpark.destroy.chemistry.index.genericreaction.AcylChlorideFormation;
import com.petrolpark.destroy.chemistry.index.genericreaction.AcylChlorideHydrolysis;
import com.petrolpark.destroy.chemistry.index.genericreaction.AlcoholOxidation;
import com.petrolpark.destroy.chemistry.index.genericreaction.AldehydeOxidation;
import com.petrolpark.destroy.chemistry.index.genericreaction.AlkeneHydrolysis;
import com.petrolpark.destroy.chemistry.index.genericreaction.AlkeneHydrogenation;
import com.petrolpark.destroy.chemistry.index.genericreaction.AmideHydrolysis;
import com.petrolpark.destroy.chemistry.index.genericreaction.CarbonylReduction;
import com.petrolpark.destroy.chemistry.index.genericreaction.CarboxylicAcidEsterification;
import com.petrolpark.destroy.chemistry.index.genericreaction.CarboxylicAcidReduction;
import com.petrolpark.destroy.chemistry.index.genericreaction.CyanideNucleophilicAddition;
import com.petrolpark.destroy.chemistry.index.genericreaction.HalideAmineSubstitution;
import com.petrolpark.destroy.chemistry.index.genericreaction.HalideAmmoniaSubstitution;
import com.petrolpark.destroy.chemistry.index.genericreaction.HalideCyanideSubstitution;
import com.petrolpark.destroy.chemistry.index.genericreaction.HalideHydroxideSubstitution;
import com.petrolpark.destroy.chemistry.index.genericreaction.NitrileHydrogenation;
import com.petrolpark.destroy.chemistry.index.genericreaction.NitrileHydrolysis;

public class DestroyGenericReactions {

    public static final AcylChlorideEsterification ACYL_CHLORIDE_ESTERIFICATION = new AcylChlorideEsterification();
    public static final AcylChlorideFormation ACYL_CHLORIDE_FORMATION = new AcylChlorideFormation();
    public static final AcylChlorideHydrolysis ACYL_CHLORIDE_HYDROLYSIS = new AcylChlorideHydrolysis();
    public static final AlcoholOxidation ALCOHOL_OXIDATION = new AlcoholOxidation();
    public static final AldehydeOxidation ALDEHYDE_OXIDATION = new AldehydeOxidation();
    public static final AlkeneHydrolysis ALKENE_HYDROLYSIS = new AlkeneHydrolysis();
    public static final AlkeneHydrogenation ALKENE_HYDROGENATION = new AlkeneHydrogenation();
    public static final AmideHydrolysis AMIDE_HYDROLYSIS = new AmideHydrolysis();
    public static final CarbonylReduction CARBONYL_REDUCTION = new CarbonylReduction();
    public static final CarboxylicAcidReduction CARBOXYLIC_ACID_REDUCTION = new CarboxylicAcidReduction();
    public static final CarboxylicAcidEsterification CARBOXYLIC_ACID_ESTERIFICATION = new CarboxylicAcidEsterification();
    public static final CyanideNucleophilicAddition CYANIDE_NUCLEOPHILIC_ADDITION = new CyanideNucleophilicAddition();
    public static final HalideAmineSubstitution HALIDE_AMINE_SUBSTITUION = new HalideAmineSubstitution();
    public static final HalideAmmoniaSubstitution HALIDE_AMMONIA_SUBSTITUTION = new HalideAmmoniaSubstitution();
    public static final HalideCyanideSubstitution HALIDE_CYANIDE_SUBSTITUTION = new HalideCyanideSubstitution();
    public static final HalideHydroxideSubstitution HALIDE_HYDROXIDE_SUBSTITUTION = new HalideHydroxideSubstitution();
    public static final NitrileHydrogenation NITRILE_HYDROGENATION = new NitrileHydrogenation();
    public static final NitrileHydrolysis NITRILE_HYDROLYSIS = new NitrileHydrolysis();

    public static void register() {};
}
