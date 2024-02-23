package com.petrolpark.destroy.compat.crafttweaker.recipes.manager;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.CTFluidIngredient;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.platform.Services;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.petrolpark.destroy.compat.crafttweaker.CTDestroy;
import com.petrolpark.destroy.recipe.DestroyRecipeTypes;
import com.petrolpark.destroy.recipe.DistillationRecipe;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@Document("mods/destroy/DistillationManager")
@ZenCodeType.Name("mods.destroy.DistillationManager")
public class DistillationRecipeManager implements IDestroyRecipeManager<DistillationRecipe> {

    /**
     * Adds recipe to the distillation tower.
     * @param name Name of the recipe
     * @param heat Heat required
     * @param input Input fluid, can't be a mixture
     * @param fractions How much of other fluids should be created per 1 mB of input
     *
     * @docParam name "distill_fluid"
     * @docParam heat <constant:create:heat_condition:none>
     * @docParam input <fluid:minecraft:lava>
     */
    @ZenCodeType.Method
    public void addRecipe(String name, HeatCondition heat, CTFluidIngredient input, IFluidStack[] fractions) {
        name = fixRecipeName(name);

        checkMixture(input);

        ResourceLocation resourceLocation = new ResourceLocation("crafttweaker", name);
        ProcessingRecipeBuilder<DistillationRecipe> builder = new ProcessingRecipeBuilder<>(getSerializer().getFactory(), resourceLocation);
        builder.withFluidIngredients(CTDestroy.mapFluidIngredients(input));
        for(IFluidStack fraction : fractions) {
            CTDestroy.output(builder, fraction);
        }
        builder.requiresHeat(heat);
        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, builder.build()));
    }

    @Override
    public DestroyRecipeTypes getDestroyRecipeType() {
        return DestroyRecipeTypes.DISTILLATION;
    }
}
