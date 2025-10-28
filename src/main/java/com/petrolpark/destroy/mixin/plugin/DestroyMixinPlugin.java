package com.petrolpark.destroy.mixin.plugin;

import com.bawnorton.mixinsquared.canceller.MixinCancellerRegistrar;
import com.petrolpark.compat.CompatMods;
import com.petrolpark.mixin.plugin.PetrolparkMixinPlugin;

public class DestroyMixinPlugin extends PetrolparkMixinPlugin {

    @Override
    protected String getMixinPackage() {
        return "com.petrolpark.destroy.mixin";
    };

    @Override
    public void onLoad(String mixinPackage) {
        // JEI mixins
        requireMultipleMods("client.JeiProcessingRecipeMixin", CompatMods.JEI);

        // TFMG mixins
        requireMultipleMods("AdvancedDistillationCategoryMixin", CompatMods.TFMG, CompatMods.JEI);

        // Fixes a compatibility issue with TFMG caused by the way it registers its own custom pumps
        // Not a permanent solution (hopefully)
        MixinCancellerRegistrar.register((targetClassNames, mixinClassName) -> mixinClassName.equals("com.drmangotea.tfmg.mixin.FluidPropagatorMixin"));
    };
    
};
