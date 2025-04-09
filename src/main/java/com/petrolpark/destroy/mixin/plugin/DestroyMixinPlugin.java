package com.petrolpark.destroy.mixin.plugin;

import com.petrolpark.mixin.plugin.PetrolparkMixinPlugin;

public class DestroyMixinPlugin extends PetrolparkMixinPlugin {

    @Override
    protected String getMixinPackage() {
        return "com.petrolpark.destroy.mixin";
    };

    @Override
    public void onLoad(String mixinPackage) {

    };
    
};
