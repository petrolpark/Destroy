package com.petrolpark.destroy.chemistry.index;

import com.petrolpark.destroy.chemistry.GroupType;
import com.petrolpark.destroy.chemistry.index.group.AcidAnhydrideGroup;
import com.petrolpark.destroy.chemistry.index.group.AcylChlorideGroup;
import com.petrolpark.destroy.chemistry.index.group.AlcoholGroup;
import com.petrolpark.destroy.chemistry.index.group.AlkeneGroup;
import com.petrolpark.destroy.chemistry.index.group.UnsubstitutedAmideGroup;
import com.petrolpark.destroy.chemistry.index.group.NonTertiaryAmineGroup;
import com.petrolpark.destroy.chemistry.index.group.CarbonylGroup;
import com.petrolpark.destroy.chemistry.index.group.CarboxylicAcidGroup;
import com.petrolpark.destroy.chemistry.index.group.HalideGroup;
import com.petrolpark.destroy.chemistry.index.group.EsterGroup;
import com.petrolpark.destroy.chemistry.index.group.NitrileGroup;

public class DestroyGroupTypes {
    
    public static GroupType<AcidAnhydrideGroup> ACID_ANHYDRIDE = new GroupType<>(() -> DestroyMolecules.GENERIC_ACID_ANHYDRIDE);
    public static GroupType<AcylChlorideGroup> ACYL_CHLORIDE = new GroupType<>(() -> DestroyMolecules.GENERIC_ACYL_CHLORIDE);
    public static GroupType<AlcoholGroup> ALCOHOL = new GroupType<>(() -> DestroyMolecules.GENERIC_ALCOHOL);
    public static GroupType<AlkeneGroup> ALKENE = new GroupType<>(() -> DestroyMolecules.GENERIC_ALKENE);
    public static GroupType<CarbonylGroup> CARBONYL = new GroupType<>(() -> DestroyMolecules.GENERIC_CARBONYL);
    public static GroupType<CarboxylicAcidGroup> CARBOXYLIC_ACID = new GroupType<>(() -> DestroyMolecules.GENERIC_CARBOXYLIC_ACID);
    public static GroupType<EsterGroup> ESTER = new GroupType<>(() -> DestroyMolecules.GENERIC_ESTER);
    public static GroupType<HalideGroup> HALIDE = new GroupType<>(() -> DestroyMolecules.GENERIC_CHLORIDE);
    public static GroupType<NitrileGroup> NITRILE = new GroupType<>(() -> DestroyMolecules.GENERIC_NITRILE);
    public static GroupType<NonTertiaryAmineGroup> NON_TERTIARY_AMINE = new GroupType<>(() -> DestroyMolecules.GENERIC_PRIMARY_AMINE);
    public static GroupType<UnsubstitutedAmideGroup> UNSUBSTITUTED_AMIDE = new GroupType<>(() -> DestroyMolecules.GENERIC_AMIDE);
};
