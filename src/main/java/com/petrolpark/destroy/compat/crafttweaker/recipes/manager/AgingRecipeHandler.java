package com.petrolpark.destroy.compat.crafttweaker.recipes.manager;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.CTFluidIngredient;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.petrolpark.destroy.compat.crafttweaker.CTDestroy;
import com.petrolpark.destroy.recipe.AgingRecipe;
import com.petrolpark.destroy.recipe.DestroyRecipeTypes;
import com.petrolpark.destroy.recipe.DistillationRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@Document("mods/destroy/AgingManager")
@ZenCodeType.Name("mods.destroy.AgingManager")
public class AgingRecipeHandler implements IDestroyRecipeManager<AgingRecipe> {

    @ZenCodeType.Method
    public void addRecipe(String name, CTFluidIngredient input, IIngredient[] items, IFluidStack result) {
        name = fixRecipeName(name);
        ResourceLocation resourceLocation = new ResourceLocation("crafttweaker", name);
        ProcessingRecipeBuilder<AgingRecipe> builder = new ProcessingRecipeBuilder<>(getSerializer().getFactory(), resourceLocation);
        builder.withFluidIngredients(CTDestroy.mapFluidIngredients(input));
        for(IIngredient itemIngredient : items) {
            builder.require(itemIngredient.asVanillaIngredient());
        }
        CTDestroy.output(builder, result);
        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, builder.build()));
    }

    @Override
    public DestroyRecipeTypes getDestroyRecipeType() {
        return DestroyRecipeTypes.AGING;
    }
}
