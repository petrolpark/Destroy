package com.petrolpark.destroy.compat.crafttweaker.recipes.manager;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.petrolpark.destroy.compat.crafttweaker.CTDestroy;
import com.petrolpark.destroy.recipe.ChargingRecipe;
import com.petrolpark.destroy.recipe.DestroyRecipeTypes;
import com.petrolpark.destroy.recipe.ExtrusionRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

public class ExtrusionRecipeManager implements IDestroyRecipeManager<ExtrusionRecipe> {
    /**
     * Adds a recipe to the extruder
     * @param name Name of the recipe
     * @param input Item to charge
     * @param output Output item
     *
     * @docParam name "charge_iron"
     * @docParam input <item:minecraft:coal>
     * @docParam output <item:minecraft:iron_ingot>
     */
    @ZenCodeType.Method
    public void addRecipe(String name, IIngredient input, IItemStack output) {
        name = fixRecipeName(name);
        ResourceLocation resourceLocation = new ResourceLocation("crafttweaker", name);
        ProcessingRecipeBuilder<ExtrusionRecipe> builder = new ProcessingRecipeBuilder<>(getSerializer().getFactory(), resourceLocation);
        builder.withItemIngredients(input.asVanillaIngredient());
        builder.withItemOutputs(CTDestroy.mapItemStackToProcessingOutput(output));
        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, builder.build()));
    }
    @Override
    public DestroyRecipeTypes getDestroyRecipeType() {
        return DestroyRecipeTypes.EXTRUSION;
    }
}
