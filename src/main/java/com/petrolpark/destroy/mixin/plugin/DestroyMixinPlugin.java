package com.petrolpark.destroy.mixin.plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import com.petrolpark.destroy.Destroy;

import net.minecraftforge.fml.loading.FMLLoader;

public class DestroyMixinPlugin implements IMixinConfigPlugin {

    private static final Map<String, Supplier<Boolean>> SHOULD_LOAD = new HashMap<>();

    @Override
    public void onLoad(String mixinPackage) {
        onlyLoadIfModPresent("com.petrolpark.destroy.mixin.TooltipRendererMixin", "jei");
        onlyLoadIfModPresent("com.petrolpark.destroy.mixin.accessor.TooltipRendererAccessor", "jei");
    };

    @Override
    public String getRefMapperConfig() {
        return null; // Use the default refmap
    };

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        Supplier<Boolean> predicate = SHOULD_LOAD.get(mixinClassName);
        if (predicate == null) return true; // Always load by default
        return predicate.get();
    };

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        // Do nothing
    };

    @Override
    public List<String> getMixins() {
        return null;
    };

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // Do nothing
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // Do nothing
    };
    
    /**
     * Tells Mixin to only apply a Mixin if a given Mod is present.
     * @param mixinClassName Fully-qualified class name
     * @param modID ID of the Mod on which this Mixin depends
     */
    private static void onlyLoadIfModPresent(String mixinClassName, String modID) {
        Destroy.LOGGER.info("Mixin "+ mixinClassName + " will only load if "+modID+" is loaded.");
        SHOULD_LOAD.put(mixinClassName, () -> FMLLoader.getLoadingModList().getModFileById(modID) != null);
    };
    
};
