package com.petrolpark.destroy.content.processing.centrifuge.potion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.DestroyPotions;
import com.petrolpark.destroy.content.processing.centrifuge.CentrifugationRecipe;
import com.simibubi.create.content.fluids.potion.PotionFluid;
import com.simibubi.create.content.fluids.potion.PotionFluid.BottleType;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import net.createmod.catnip.data.Pair;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class PotionSeparationRecipes {

    public static Map<Pair<Potion, BottleType>, CentrifugationRecipe> ALL = createSeparationRecipes();
    
    private static Map<Pair<Potion, BottleType>, CentrifugationRecipe> createSeparationRecipes() {
        Map<Pair<Potion, BottleType>, CentrifugationRecipe> recipes = new HashMap<>();

        int recipeIndex = 0;

        // Highest priority - lingering and splash potions
        for (PotionBrewing.Mix<Item> mix : PotionBrewing.CONTAINER_MIXES) {
			Item from = mix.from.get();
			if (!PotionFluidMixingRecipes.VANILLA_CONTAINERS.contains(from)) continue;

			Item to = mix.to.get();
			BottleType fromBottleType = PotionFluidHandler.bottleTypeFromItem(from);
			BottleType toBottleType = PotionFluidHandler.bottleTypeFromItem(to); // The potion the original recipe produces, and so the one which the separation will take as input

            for (Entry<Item, FluidStack> entry : PotionFluidMixingRecipes.FLUID_EQUIVALENTS.entrySet()) {
                if (mix.ingredient.test(new ItemStack(entry.getKey()))) {
                    addEachPotion: for (Potion potion : ForgeRegistries.POTIONS.getValues()) {
                        Pair<Potion, BottleType> potionAndType = Pair.of(potion, toBottleType);
                        if (potion == Potions.EMPTY) continue addEachPotion;
        
                        FluidStack fromFluid = PotionFluidHandler.getFluidFromPotion(potion, fromBottleType, 1000);
                        FluidStack toFluid = PotionFluidHandler.getFluidFromPotion(potion, toBottleType, 1000);
        
                        recipes.putIfAbsent(potionAndType, createRecipe("potion_separation_" + recipeIndex++, toFluid, entry.getValue(), fromFluid));
                    };
                };
            };
		};

        // Next highest priority - strong, long and fermented spider eye potions
        for (Item container : PotionFluidMixingRecipes.VANILLA_CONTAINERS) {
			BottleType bottleType = PotionFluidHandler.bottleTypeFromItem(container);
			for (PotionBrewing.Mix<Potion> mix : PotionBrewing.POTION_MIXES) {
                for (Entry<Item, FluidStack> entry : PotionFluidMixingRecipes.FLUID_EQUIVALENTS.entrySet()) {
                    Potion toPotion = mix.to.get();
                    Pair<Potion, BottleType> potionAndType = Pair.of(toPotion, bottleType);
                    if (mix.ingredient.test(new ItemStack(entry.getKey())) && !recipes.containsKey(potionAndType)) {

                        FluidStack fromFluid = PotionFluidHandler.getFluidFromPotion(mix.from.get(), bottleType, 1000);
                        FluidStack toFluid = PotionFluidHandler.getFluidFromPotion(toPotion, bottleType, 1000); // The potion the original recipe produces, and so the one which the separation will take as input

                        if (!fromFluid.getFluid().isSame(Fluids.WATER)) recipes.putIfAbsent(potionAndType, createRecipe("potion_separation_" + recipeIndex++, toFluid, entry.getValue(), fromFluid));
                    };
                };
			};
		};

        // Lowest priority - split one potion with multiple effects into multiple potions
        for (Potion potion : ForgeRegistries.POTIONS.getValues()) {
            if (potion == Potions.EMPTY) continue;
            List<MobEffectInstance> effects = potion.getEffects();
            if (effects.size() > 1) {
                List<MobEffectInstance> firstEffects = new ArrayList<>();
                List<MobEffectInstance> secondEffects = new ArrayList<>();
                int i = 0;
                for (MobEffectInstance effect : effects) {
                    (i < effects.size() / 2 ? firstEffects : secondEffects).add(effect);
                    i++;
                };
                addEachContainer: for (Item item : PotionFluidMixingRecipes.VANILLA_CONTAINERS) {
                    BottleType bottleType = PotionFluidHandler.bottleTypeFromItem(item); // The potion the original recipe produces, and so the one which the separation will take as input

                    Pair<Potion, BottleType> potionAndType = Pair.of(potion, bottleType);

                    if (recipes.containsKey(potionAndType)) continue addEachContainer;

                    FluidStack lightOutput = PotionFluidHandler.getFluidFromPotion(DestroyPotions.UNIQUE.get(), bottleType, 1000);
                    FluidStack denseOutput = PotionFluidHandler.getFluidFromPotion(DestroyPotions.UNIQUE.get(), bottleType, 1000);

                    PotionFluid.appendEffects(lightOutput, firstEffects);
                    PotionFluid.appendEffects(denseOutput, secondEffects);

                    recipes.put(potionAndType, createRecipe("potion_separation_" + recipeIndex++, PotionFluidHandler.getFluidFromPotion(potion, bottleType, 1000), lightOutput, denseOutput));
                };
            };
        };

        return recipes;
    };

    private static CentrifugationRecipe createRecipe(String name, FluidStack from, FluidStack light, FluidStack dense) {
        return new ProcessingRecipeBuilder<>(CentrifugationRecipe::new, Destroy.asResource(name))
            .require(FluidIngredient.fromFluidStack(from))
            .duration(200)
            .output(light)
            .output(dense)
            .build();
    };
};
