package com.petrolpark.destroy.chemistry.legacy.index;


import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.AcylChlorideEsterification;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.AcylChlorideFormation;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.AcylChlorideHydrolysis;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.AlcoholDehydration;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.AlcoholOxidation;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.AldehydeOxidation;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.AlkoxideProtonation;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.AmideHydrolysis;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.AminePhosgenation;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.BoraneOxidation;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.BorateEsterHydrolysis;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.BorateEsterification;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.CarboxylicAcidEsterification;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.CyanamideAddition;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.CyanideNucleophilicAddition;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.ElectrophilicChlorination;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.ElectrophilicChlorohydrination;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.ElectrophilicHydroboration;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.BoraneElectrophilicHydroboration;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.ElectrophilicHydrochlorination;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.ElectrophilicHydroiodination;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.ElectrophilicIodination;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.HalideAmineSubstitution;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.HalideAmmoniaSubstitution;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.HalideCyanideSubstitution;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.HalideHydroxideSubstitution;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.IsocyanateHydrolysis;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.NitrileHydrogenation;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.NitrileHydrolysis;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.NitroHydrogenation;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.SaturatedCarbonHydrogenation;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.SaturatedCarbonHydrolysis;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.WolffKishnerReduction;
import com.petrolpark.destroy.chemistry.legacy.index.genericreaction.ThionylChlorideSubstitution;


public class DestroyGenericReactions {

    public static final AcylChlorideEsterification ACYL_CHLORIDE_ESTERIFICATION = new AcylChlorideEsterification();
    public static final AcylChlorideFormation ACYL_CHLORIDE_FORMATION = new AcylChlorideFormation();
    public static final AcylChlorideHydrolysis ACYL_CHLORIDE_HYDROLYSIS = new AcylChlorideHydrolysis();
    public static final AlcoholDehydration ALCOHOL_DEHYDRATION = new AlcoholDehydration();
    public static final AlcoholOxidation ALCOHOL_OXIDATION = new AlcoholOxidation();
    public static final AldehydeOxidation ALDEHYDE_OXIDATION = new AldehydeOxidation();
    public static final ElectrophilicChlorination ALKENE_CHLORINATION = new ElectrophilicChlorination(false);
    public static final ElectrophilicChlorohydrination ALKENE_CHLOROHYDRINATION = new ElectrophilicChlorohydrination(false);
    public static final SaturatedCarbonHydrolysis ALKENE_HYDROLYSIS = new SaturatedCarbonHydrolysis(false);
    //public static final ElectrophilicHydroboration ALKENE_HYDROBORATION = new ElectrophilicHydroboration(false); // causes a game freeze whenever it occurs, my guess is excessive esterification, alkyne is the same
    public static final BoraneElectrophilicHydroboration ALKENE_HYDROBORATION_WITH_BORANE = new BoraneElectrophilicHydroboration(false);
    public static final ElectrophilicHydrochlorination ALKENE_HYDROCHLORINATION = new ElectrophilicHydrochlorination(false);
    public static final SaturatedCarbonHydrogenation ALKENE_HYDROGENATION = new SaturatedCarbonHydrogenation(false);
    public static final ElectrophilicHydroiodination ALKENE_HYDROIODINATION = new ElectrophilicHydroiodination(false);
    public static final ElectrophilicIodination ALKENE_IODINATION = new ElectrophilicIodination(false);
    public static final AlkoxideProtonation ALKOXIDE_PROTONATION = new AlkoxideProtonation();
    public static final ElectrophilicChlorination ALKYNE_CHLORINATION = new ElectrophilicChlorination(true);
    public static final ElectrophilicChlorohydrination ALKYNE_CHLOROHYDRINATION = new ElectrophilicChlorohydrination(true);
    public static final SaturatedCarbonHydrolysis ALKYNE_HYDROLYSIS = new SaturatedCarbonHydrolysis(true);
   //public static final ElectrophilicHydroboration ALKYNE_HYDROBORATION = new ElectrophilicHydroboration(true);
    public static final BoraneElectrophilicHydroboration ALKYNE_HYDROBORATION_WITH_BORANE = new BoraneElectrophilicHydroboration(true);
    public static final ElectrophilicHydrochlorination ALKYNE_HYDROCHLORINATION = new ElectrophilicHydrochlorination(true);
    public static final SaturatedCarbonHydrogenation ALKYNE_HYDROGENATION = new SaturatedCarbonHydrogenation(true);
    public static final ElectrophilicHydroiodination ALKYNE_HYDROIODINATION = new ElectrophilicHydroiodination(true);
    public static final ElectrophilicIodination ALKYNE_IODINATION = new ElectrophilicIodination(true);
    public static final AmideHydrolysis AMIDE_HYDROLYSIS = new AmideHydrolysis();
    public static final AminePhosgenation AMINE_PHOSGENATION = new AminePhosgenation();
    public static final BoraneOxidation BORANE_OXIDATION = new BoraneOxidation();
    public static final BorateEsterHydrolysis BORATE_ESTER_HYDROLYSIS = new BorateEsterHydrolysis();
    //public static final BorateEsterification BORATE_ESTERIFICATION = new BorateEsterification(); // any solution containing boron compounds, even just boric acid or trace borate ions, becomes a disgusting mess of constantly esterifying generics
    //public static final BorohydrideCarbonylReduction BOROHYDRIDE_CARBONYL_REDUCTION = new BorohydrideCarbonylReduction();
    //public static final CarboxylicAcidReduction CARBOXYLIC_ACID_REDUCTION = new CarboxylicAcidReduction();
    public static final CyanamideAddition CYANAMIDE_ADDITION = new CyanamideAddition();
    public static final CarboxylicAcidEsterification CARBOXYLIC_ACID_ESTERIFICATION = new CarboxylicAcidEsterification();
    public static final CyanideNucleophilicAddition CYANIDE_NUCLEOPHILIC_ADDITION = new CyanideNucleophilicAddition();
    public static final HalideAmineSubstitution HALIDE_AMINE_SUBSTITUION = new HalideAmineSubstitution();
    public static final HalideAmmoniaSubstitution HALIDE_AMMONIA_SUBSTITUTION = new HalideAmmoniaSubstitution();
    public static final HalideCyanideSubstitution HALIDE_CYANIDE_SUBSTITUTION = new HalideCyanideSubstitution();
    public static final HalideHydroxideSubstitution HALIDE_HYDROXIDE_SUBSTITUTION = new HalideHydroxideSubstitution();
    public static final IsocyanateHydrolysis ISOCYANATE_HYDROLYSIS = new IsocyanateHydrolysis();
    public static final NitrileHydrogenation NITRILE_HYDROGENATION = new NitrileHydrogenation();
    public static final NitrileHydrolysis NITRILE_HYDROLYSIS = new NitrileHydrolysis();
    public static final NitroHydrogenation NITRO_HYDROGENATION = new NitroHydrogenation();
    public static final ThionylChlorideSubstitution THIONYL_CHLORIDE_SUBSTITUTION = new ThionylChlorideSubstitution();
    public static final WolffKishnerReduction WOLFF_KISHNER_REDUCTION = new WolffKishnerReduction();

    public static void register() {};
};
