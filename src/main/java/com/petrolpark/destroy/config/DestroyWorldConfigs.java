package com.petrolpark.destroy.config;

public class DestroyWorldConfigs extends DestroyConfigBase {

    public final ConfigBool automaticGoggles = b(true, "automaticGoggles", "Players in Creative mode are treated as if they are wearing Engineer's Goggles even if they are not");
    public final ConfigBool extendedInventorySafeMode = b(true, "extendedInventorySafeMode", "Only show extra inventory slots in menus which are known not to cause problems", "[Disabling will let extra slots show up in new menus]", "[If you disable this and try a new menu from another mod, then tell me whether it crashes or not at https://github.com/petrolpark/Destroy/issues/1]");
    public final DestroyBlocksConfigs blocks = nested(0, DestroyBlocksConfigs::new, "Destroy's blocks");
	public final DestroyPollutionConfigs pollution = nested(0, DestroyPollutionConfigs::new, "The effects of pollution on the world");
    public final DestroySubstancesConfigs substances = nested(0, DestroySubstancesConfigs::new, "Destroy's drugs and medicines");
    public final DestroyEquipmentConfigs equipment = nested(0, DestroyEquipmentConfigs::new, "Destroy's armor and tools");
    public final DestroyCompatConfigs compat = nested(0, DestroyCompatConfigs::new, "Compatibility with other mods");
    public final DestroyKineticsConfigs kinetics = nested(0, DestroyKineticsConfigs::new, "Parameters and abilities of Destroy's kinetic mechanisms");
    
    @Override
    public String getName() {
        return "world";
    };
};
