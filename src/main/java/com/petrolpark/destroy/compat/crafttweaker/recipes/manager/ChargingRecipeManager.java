package com.petrolpark.destroy.compat.crafttweaker.recipes.manager;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.CTFluidIngredient;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.petrolpark.destroy.compat.crafttweaker.CTDestroy;
import com.petrolpark.destroy.recipe.CentrifugationRecipe;
import com.petrolpark.destroy.recipe.ChargingRecipe;
import com.petrolpark.destroy.recipe.DestroyRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@Document("mods/destroy/ChargingManager")
@ZenCodeType.Name("mods.destroy.ChargingManager")
public class ChargingRecipeManager implements IDestroyRecipeManager<ChargingRecipe> {

    /**
     * Adds a recipe to the dynamo machine. (This feature is likely to get removed or heavily modified in future release of Destroy)
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
        ProcessingRecipeBuilder<ChargingRecipe> builder = new ProcessingRecipeBuilder<>(getSerializer().getFactory(), resourceLocation);
        builder.withItemIngredients(input.asVanillaIngredient());
        builder.withItemOutputs(CTDestroy.mapItemStackToProcessingOutput(output));
        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, builder.build()));
    }

    @Override
    public DestroyRecipeTypes getDestroyRecipeType() {
        return DestroyRecipeTypes.CHARGING;
    }
}
