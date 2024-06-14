package com.petrolpark.destroy.mixin.plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.minecraftforge.fml.loading.FMLLoader;

public class DestroyMixinPlugin implements IMixinConfigPlugin {

    private static final Map<String, Supplier<Boolean>> SHOULD_LOAD = new HashMap<>();

    @Override
    public void onLoad(String mixinPackage) {
        // JEI & Create JEI mixins
        onlyLoadIfModPresent("com.petrolpark.destroy.mixin.accessor.TooltipRendererAccessor", "jei");
        onlyLoadIfModPresent("com.petrolpark.destroy.mixin.BasinCategoryMixin.java", "jei");
        onlyLoadIfModPresent("com.petrolpark.destroy.mixin.CreateJEIMixin.java", "jei");
        onlyLoadIfModPresent("com.petrolpark.destroy.mixin.CreateRecipeCategoryMixin", "jei");
        onlyLoadIfModPresent("com.petrolpark.destroy.mixin.MixingRecipeCategoryMixin", "jei");
        onlyLoadIfModPresent("com.petrolpark.destroy.mixin.PackingRecipeCategoryMixin", "jei");
        onlyLoadIfModPresent("com.petrolpark.destroy.mixin.ProcessingRecipeMixin", "jei");
        onlyLoadIfModPresent("com.petrolpark.destroy.mixin.TooltipRendererMixin", "jei");

        // CraftTweaker
        onlyLoadIfModPresent("com.petrolpark.destroy.mixin.RecipeListMixin", "crafttweaker");

        // Farmers' Delight mixins
        onlyLoadIfModPresent("com.petrolpark.destroy.mixin.CuttingBoardMixin", "farmersdelight");
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
     * @param mixinClassName Fully-qualified class name. <strong>Don't use {@code SomeMixin.getClass().getSimpleName()} for this</strong>,
     * as this calls the class, which will crash as it can't find the class into which its mixing
     * @param modID ID of the Mod on which this Mixin depends
     */
    private static void onlyLoadIfModPresent(String mixinClassName, String modID) {
        SHOULD_LOAD.put(mixinClassName, () -> FMLLoader.getLoadingModList().getModFileById(modID) != null);
    };
    
};
